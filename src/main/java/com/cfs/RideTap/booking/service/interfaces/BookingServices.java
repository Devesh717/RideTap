package com.cfs.RideTap.booking.service.interfaces;

import com.cfs.RideTap.booking.dto.request.BookingRequest;
import com.cfs.RideTap.booking.model.Booking;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

public interface BookingServices {
    Booking book(String metroCardNumber, BookingRequest bookingRequest)
            throws ResponseStatusException;

    Optional<Booking> findByIdAndUserEmail(Long bookingId, String email);

    Optional<Booking> findUserBookingByRoute(String email, String originStation, String destinationStation);

    Booking cancelById(String metroCardNumber, Long id) throws ResponseStatusException;

    Booking validateTicketForEntry(Long bookingId) throws ResponseStatusException;

    int expireOverdueTickets();

    List<Booking> getBookingsByMetroCardNumber(String metroCardNumber);

    List<Booking> getAllBookings();
}
