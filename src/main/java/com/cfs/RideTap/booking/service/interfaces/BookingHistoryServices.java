package com.cfs.RideTap.booking.service.interfaces;

import java.util.List;

import com.cfs.RideTap.booking.enums.BookingStatus;
import com.cfs.RideTap.booking.model.Booking;
import org.springframework.web.server.ResponseStatusException;

public interface BookingHistoryServices {
  List<Booking> findAllBookings();

  List<Booking> findAllBookingsByBookingStatus(BookingStatus bookingStatus);

  List<Booking> findAllUserBookings(String metroCardNumber);

  List<Booking> findByMetroCardNumberAndBookingStatus(String metroCardNumber, BookingStatus bookingStatus);

  Booking findById(String metroCardNumber, Long id) throws ResponseStatusException;
}
