package com.cfs.RideTap.services.implementations;

import java.util.List;

import com.cfs.RideTap.enums.BookingStatus;
import com.cfs.RideTap.models.Booking;
import com.cfs.RideTap.repositories.BookingRepository;
import com.cfs.RideTap.services.interfaces.BookingHistoryServices;
import com.cfs.RideTap.services.interfaces.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;



@Service
public class BookingHistoryServicesImpl implements BookingHistoryServices {
  @Autowired
  private BookingRepository bookingRepository;

  @Autowired
  private UserServices userServices;

  @Override
  public List<Booking> findAllBookings() {
    return bookingRepository.findAll();
  }

  @Override
  public List<Booking> findAllBookingsByBookingStatus(BookingStatus bookingStatus) {
    return bookingRepository.findByBookingStatus(bookingStatus);
  }

  @Override
  public List<Booking> findAllUserBookings(String metroCardNumber) {
    if (!userServices.existsByMetroCardNumber(metroCardNumber)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND,
          "User by metroCardNumber " + metroCardNumber + " does not exist");
    }

    return bookingRepository.findByUserMetroCardNumber(metroCardNumber);
  }

  @Override
  public List<Booking> findByMetroCardNumberAndBookingStatus(String metroCardNumber, BookingStatus bookingStatus) {
    if (!userServices.existsByMetroCardNumber(metroCardNumber)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND,
          "User by metroCardNumber " + metroCardNumber + " does not exist");
    }

    List<Booking> filteredBookings = bookingRepository
        .findByUserMetroCardNumberAndBookingStatus(metroCardNumber, bookingStatus);

    return filteredBookings;
  }

  @Override
  public Booking findById(String metroCardNumber, Long id) {
    Booking booking = bookingRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    if (!booking.getUser().getMetroCardNumber().equals(metroCardNumber)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    return booking;
  }
}
