package com.cfs.RideTap.booking.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.cfs.RideTap.booking.api.BookingHistoryApi;
import com.cfs.RideTap.booking.dto.response.BookingDTO;
import com.cfs.RideTap.booking.enums.BookingStatus;
import com.cfs.RideTap.booking.mapper.BookingDTOMapper;
import com.cfs.RideTap.booking.model.Booking;
import com.cfs.RideTap.booking.service.interfaces.BookingHistoryServices;
import com.cfs.RideTap.user.model.UserEntity;
import com.cfs.RideTap.user.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/booking-history")
@CrossOrigin(origins = "http://localhost:3000")
public class BookingHistoryController implements BookingHistoryApi {

    @Autowired
    private BookingHistoryServices bookingHistoryServices;

    @Autowired
    private UserRepository userRepository;

    private UserEntity resolveAuthenticatedUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private BookingStatus parseStatus(Integer bookingStatus) {
        if (bookingStatus == null) return null;
        return switch (bookingStatus) {
            case 1 -> BookingStatus.CONFIRMED;
            case 2 -> BookingStatus.EXPIRED;
            case 3 -> BookingStatus.USED;
            default -> BookingStatus.CANCELLED;
        };
    }

    @Override
    @GetMapping("/all")
    public ResponseEntity<List<BookingDTO>> getAllBookings(
            @RequestParam(required = false) String metroCardNumber,
            @RequestParam(required = false) Integer bookingStatus) {
        List<Booking> bookings;
        BookingStatus status = parseStatus(bookingStatus);

        if (metroCardNumber != null && status != null) {
            bookings = bookingHistoryServices.findByMetroCardNumberAndBookingStatus(metroCardNumber, status);
        } else if (metroCardNumber != null) {
            bookings = bookingHistoryServices.findAllUserBookings(metroCardNumber);
        } else if (status != null) {
            bookings = bookingHistoryServices.findAllBookingsByBookingStatus(status);
        } else {
            bookings = bookingHistoryServices.findAllBookings();
        }

        List<BookingDTO> allBookingDTOs = bookings
                .stream()
                .map(BookingDTOMapper::map)
                .collect(Collectors.toList());

        return new ResponseEntity<>(allBookingDTOs, HttpStatus.OK);
    }

    @Override
    @GetMapping
    public ResponseEntity<List<BookingDTO>> getAllBookingsOfUser(
            @RequestParam(required = false) Integer bookingStatus,
            Authentication authentication) {

        UserEntity user = resolveAuthenticatedUser(authentication);
        String metroCardNumber = user.getMetroCardNumber();

        List<Booking> bookings;
        BookingStatus status = parseStatus(bookingStatus);

        if (status != null) {
            bookings = bookingHistoryServices.findByMetroCardNumberAndBookingStatus(metroCardNumber, status);
        } else {
            bookings = bookingHistoryServices.findAllUserBookings(metroCardNumber);
        }

        List<BookingDTO> filteredBookingDTOs = bookings
                .stream()
                .map(BookingDTOMapper::map)
                .collect(Collectors.toList());

        return new ResponseEntity<>(filteredBookingDTOs, HttpStatus.OK);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<BookingDTO> getById(
            @Valid @PathVariable Long id,
            Authentication authentication) {

        UserEntity user = resolveAuthenticatedUser(authentication);
        String metroCardNumber = user.getMetroCardNumber();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        Booking booking = bookingHistoryServices.findById(metroCardNumber, id);

        if (!isAdmin && !booking.getUser().getMetroCardNumber().equals(metroCardNumber)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>(BookingDTOMapper.map(booking), HttpStatus.OK);
    }
}
