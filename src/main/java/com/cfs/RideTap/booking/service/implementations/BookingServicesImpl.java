package com.cfs.RideTap.booking.service.implementations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.cfs.RideTap.balance.service.interfaces.BalanceServices;
import com.cfs.RideTap.booking.dto.request.BookingRequest;
import com.cfs.RideTap.booking.enums.BookingStatus;
import com.cfs.RideTap.booking.model.Booking;
import com.cfs.RideTap.booking.repository.BookingRepository;
import com.cfs.RideTap.booking.service.interfaces.BookingServices;
import com.cfs.RideTap.fare.service.interfaces.FareCalculator;
import com.cfs.RideTap.notification.dto.event.BookingCancelledEvent;
import com.cfs.RideTap.notification.dto.event.BookingCreatedEvent;
import com.cfs.RideTap.notification.kafka.producer.BookingProducer;
import com.cfs.RideTap.payment.entity.PaymentMethod;
import com.cfs.RideTap.qr.service.interfaces.QRServices;
import com.cfs.RideTap.station.model.Station;
import com.cfs.RideTap.station.service.interfaces.StationServices;
import com.cfs.RideTap.user.model.UserEntity;
import com.cfs.RideTap.user.service.interfaces.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    @Transactional
    public Booking book(String metroCardNumber, BookingRequest bookingRequest)
            throws ResponseStatusException {

        // Check if the user already has an active (CONFIRMED and unexpired) ticket
        List<Booking> userBookings = bookingRepository.findByUserMetroCardNumber(metroCardNumber);
        boolean hasActiveTicket = userBookings.stream()
                .anyMatch(b -> b.getBookingStatus() == BookingStatus.CONFIRMED && !b.isExpired());

        if (hasActiveTicket) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "You already have an active metro ticket. Please use, cancel, or wait for it to expire before booking a new one.");
        }

        Station originStation = stationServices.findByName(
                        bookingRequest.getOriginStation())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Station not found: " + bookingRequest.getOriginStation()));

        Station destinationStation = stationServices.findByName(
                        bookingRequest.getDestinationStation())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Station not found: " + bookingRequest.getDestinationStation()));

        LocalDateTime dateTime = LocalDateTime.now();
        LocalDateTime expiresAt = dateTime.plusHours(Booking.TICKET_VALIDITY_HOURS);

        UserEntity userEntity = userServices.findByMetroCardNumber(metroCardNumber);

        double fare = fareCalculator.calculate(
                bookingRequest.getOriginStation(),
                bookingRequest.getDestinationStation());

        PaymentMethod method = bookingRequest.getPaymentMethod() != null
                ? bookingRequest.getPaymentMethod()
                : PaymentMethod.METROCARD;

        // Deduct MetroCard wallet balance ONLY when paying via METROCARD
        if (method == PaymentMethod.METROCARD) {
            balanceServices.deductFare(userEntity, fare);
        }

        Booking booking = new Booking();
        booking.setUser(userEntity);
        booking.setOriginStation(originStation);
        booking.setDestinationStation(destinationStation);
        booking.setDateTime(dateTime);
        booking.setExpiresAt(expiresAt);
        booking.setFare(fare);
        booking.setBookingStatus(BookingStatus.CONFIRMED);
        booking.setPaymentMethod(method);
        booking.setRazorpayPaymentId(bookingRequest.getRazorpayPaymentId());

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
    @Transactional
    public Booking cancelById(String metroCardNumber, Long id)
            throws ResponseStatusException {

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Booking not found by provided id"));

        if (!booking.getUser().getMetroCardNumber().equals(metroCardNumber)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Booking is already cancelled");
        }

        if (booking.getBookingStatus() == BookingStatus.USED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Used ticket cannot be cancelled");
        }

        if (booking.isExpired()) {
            booking.setBookingStatus(BookingStatus.EXPIRED);
            bookingRepository.save(booking);
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Expired ticket cannot be cancelled");
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);
        Booking updatedBooking = bookingRepository.save(booking);

        // Refund fare to user's RideTap Metro Card balance
        UserEntity updatedUser = booking.getUser();
        updatedUser.recharge(booking.getFare());
        userServices.save(updatedUser);

        BookingCancelledEvent event =
                BookingCancelledEvent.builder()
                        .bookingId(updatedBooking.getId())
                        .email(updatedUser.getEmail())
                        .holderName(updatedUser.getHolderName())
                        .originStation(updatedBooking.getOriginStation().getName())
                        .destinationStation(updatedBooking.getDestinationStation().getName())
                        .refundedAmount(updatedBooking.getFare())
                        .build();

        bookingProducer.cancelPublish(event);

        return updatedBooking;
    }

    @Override
    @Transactional
    public Booking validateTicketForEntry(Long bookingId) throws ResponseStatusException {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Invalid ticket QR code"));

        if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ticket has been cancelled");
        }

        if (booking.getBookingStatus() == BookingStatus.USED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ticket has already been used");
        }

        if (booking.isExpired()) {
            if (booking.getBookingStatus() != BookingStatus.EXPIRED) {
                booking.setBookingStatus(BookingStatus.EXPIRED);
                bookingRepository.save(booking);
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ticket has expired");
        }

        booking.setBookingStatus(BookingStatus.USED);
        return bookingRepository.save(booking);
    }

    @Override
    @Scheduled(fixedRate = 900000) // Every 15 minutes
    @Transactional
    public int expireOverdueTickets() {
        int updated = bookingRepository.markExpiredBookings(
                BookingStatus.CONFIRMED,
                BookingStatus.EXPIRED,
                LocalDateTime.now());
        if (updated > 0) {
            log.info("Auto-expired {} overdue metro tickets", updated);
        }
        return updated;
    }

    @Override
    public Optional<Booking> findByIdAndUserEmail(Long bookingId, String email) {
        return bookingRepository.findByIdAndUser_Email(bookingId, email);
    }

    @Override
    public Optional<Booking> findUserBookingByRoute(String email, String originStation, String destinationStation) {
        return bookingRepository.findTopByUser_EmailAndOriginStation_NameAndDestinationStation_NameOrderByDateTimeDesc(
                email,
                originStation,
                destinationStation
        );
    }

    @Override
    public List<Booking> getBookingsByMetroCardNumber(String metroCardNumber) {
        expireOverdueTickets();
        return bookingRepository.findByUserMetroCardNumber(metroCardNumber);
    }

    @Override
    public List<Booking> getAllBookings() {
        expireOverdueTickets();
        return bookingRepository.findAll();
    }
}
