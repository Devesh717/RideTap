package com.cfs.RideTap.services.interfaces;

import com.cfs.RideTap.dtos.bookingDTO.BookingRequest;
import com.cfs.RideTap.models.Booking;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;


public interface BookingServices {
  public Booking book(String metroCardNumber, BookingRequest bookingRequest)
      throws ResponseStatusException;

  Optional<Booking> findByIdAndUserEmail(Long bookingId, String email);

  Optional<Booking> findUserBookingByRoute(String email, String originStation, String destinationStation);

  Booking cancelById(String metroCardNumber, Long id) throws ResponseStatusException;
}
