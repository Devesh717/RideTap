package com.cfs.RideTap.services.interfaces;

import java.util.List;

import com.cfs.RideTap.enums.BookingStatus;
import com.cfs.RideTap.models.Booking;
import org.springframework.web.server.ResponseStatusException;



public interface BookingHistoryServices {
  public List<Booking> findAllBookings();

  public List<Booking> findAllBookingsByBookingStatus(BookingStatus bookingStatus);

  public List<Booking> findAllUserBookings(String metroCardNumber);

  public List<Booking> findByMetroCardNumberAndBookingStatus(String metroCardNumber, BookingStatus bookingStatus);

  public Booking findById(String metroCardNumber, Long id) throws ResponseStatusException;
}
