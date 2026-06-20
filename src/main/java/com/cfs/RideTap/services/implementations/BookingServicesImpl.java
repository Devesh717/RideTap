package com.cfs.RideTap.services.implementations;

import java.time.LocalDateTime;
import java.util.Optional;

import com.cfs.RideTap.dtos.bookingDTO.BookingRequest;
import com.cfs.RideTap.dtos.eventDTO.BookingCancelledEvent;
import com.cfs.RideTap.dtos.eventDTO.BookingCreatedEvent;
import com.cfs.RideTap.enums.BookingStatus;
import com.cfs.RideTap.kafka.producer.BookingProducer;
import com.cfs.RideTap.models.Booking;
import com.cfs.RideTap.models.Station;
import com.cfs.RideTap.models.UserEntity;
import com.cfs.RideTap.repositories.BookingRepository;
import com.cfs.RideTap.services.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class BookingServicesImpl implements BookingServices {
  @Autowired
  private UserServices userServices;

  @Autowired
  private StationServices stationServices;

  @Autowired
  private BookingRepository bookingRepository;

  @Autowired
  private FareCalculator fareCalculator;

  @Autowired
  private BalanceServices balanceServices;

  @Autowired
  private QRServices qrServices;

  @Autowired
  private BookingProducer bookingProducer;

  @Override
  public Booking book(String metroCardNumber, BookingRequest bookingRequest)
          throws ResponseStatusException {

    Station originStation = stationServices.findByName(
            bookingRequest.getOriginStation())
            .orElseThrow(()-> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Station not found: " + bookingRequest.getOriginStation()));

    Station destinationStation = stationServices.findByName(
            bookingRequest.getDestinationStation())
            .orElseThrow(()-> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Station not found: " + bookingRequest.getDestinationStation()));


    LocalDateTime dateTime = LocalDateTime.now();

    UserEntity userEntity =
            userServices.findByMetroCardNumber(metroCardNumber);

    double fare = fareCalculator.calculate(
            bookingRequest.getOriginStation(),
            bookingRequest.getDestinationStation());

    balanceServices.deductFare(userEntity, fare);

    Booking booking = new Booking(
            null,
            userEntity,
            originStation,
            destinationStation,
            dateTime,
            fare,
            BookingStatus.CONFIRMED,
            null
    );

    Booking savedBooking = bookingRepository.save(booking);

    String qrPath = qrServices.generateQRCode(savedBooking.getId());

    savedBooking.setQrCodePath(qrPath);

    savedBooking = bookingRepository.save(savedBooking);

    BookingCreatedEvent bookingCreatedEvent = new BookingCreatedEvent(
            savedBooking.getId(),
            savedBooking.getUser().getEmail(),
            savedBooking.getUser().getHolderName(),
            savedBooking.getOriginStation().getName(),
            savedBooking.getDestinationStation().getName(),
            fare,
            qrPath
    );

    bookingProducer.createPublish(bookingCreatedEvent);

    userEntity.getBookings().add(savedBooking);
    userServices.save(userEntity);

    return savedBooking;
  }

  @Override
  public Booking cancelById(String metroCardNumber, Long id)
          throws ResponseStatusException {

    Booking booking = bookingRepository.findById(id)
            .orElseThrow(() ->
                    new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Booking not found by provided id"));

    if (booking.getBookingStatus()
            .equals(BookingStatus.CANCELLED)) {

      throw new ResponseStatusException(
              HttpStatus.BAD_REQUEST,
              "Booking is already cancelled");
    }

    if (!booking.getUser()
            .getMetroCardNumber()
            .equals(metroCardNumber)) {

      throw new ResponseStatusException(
              HttpStatus.FORBIDDEN);
    }

    booking.setBookingStatus(
            BookingStatus.CANCELLED);

    Booking updatedBooking =
            bookingRepository.save(booking);

    UserEntity updatedUser =
            booking.getUser();

    updatedUser.recharge(
            booking.getFare());

    userServices.save(updatedUser);

    BookingCancelledEvent event =
            BookingCancelledEvent.builder()
                    .bookingId(updatedBooking.getId())
                    .email(updatedUser.getEmail())
                    .holderName(updatedUser.getHolderName())
                    .originStation(
                            updatedBooking
                                    .getOriginStation()
                                    .getName())
                    .destinationStation(
                            updatedBooking
                                    .getDestinationStation()
                                    .getName())
                    .refundedAmount(
                            updatedBooking.getFare())
                    .build();

    bookingProducer.cancelPublish(event);

    return updatedBooking;
  }

  @Override
  public Optional<Booking> findByIdAndUserEmail(
          Long bookingId,
          String email) {

    return bookingRepository
            .findByIdAndUser_Email(
                    bookingId,
                    email);
  }

  @Override
  public Optional<Booking> findUserBookingByRoute(String email, String originStation, String destinationStation) {
    return bookingRepository.findTopByUser_EmailAndOriginStation_NameAndDestinationStation_NameOrderByDateTimeDesc(
            email,
            originStation,
            destinationStation
    );
  }
}
