package com.cfs.RideTap.booking.controller;

import com.cfs.RideTap.booking.dto.request.BookingRequest;
import com.cfs.RideTap.booking.dto.response.BookingResponse;
import com.cfs.RideTap.booking.enums.BookingStatus;
import com.cfs.RideTap.booking.model.Booking;
import com.cfs.RideTap.booking.service.interfaces.BookingServices;
import com.cfs.RideTap.user.model.UserEntity;
import com.cfs.RideTap.user.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class BookingController {

    private final BookingServices bookingServices;
    private final UserRepository userRepository;

    public BookingController(BookingServices bookingServices, UserRepository userRepository) {
        this.bookingServices = bookingServices;
        this.userRepository = userRepository;
    }

    private String resolveMetroCardNumber(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        UserEntity user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return user.getMetroCardNumber();
    }

    @PostMapping("/book")
    public ResponseEntity<BookingResponse> bookTicket(
            @Valid @RequestBody BookingRequest request,
            Authentication authentication) {
        String metroCardNumber = resolveMetroCardNumber(authentication);
        Booking savedBooking = bookingServices.book(metroCardNumber, request);
        return new ResponseEntity<>(toResponse(savedBooking), HttpStatus.CREATED);
    }

    @PutMapping({"/book/cancel/{id}", "/book/{id}/cancel"})
    public ResponseEntity<BookingResponse> cancelTicket(
            @PathVariable Long id,
            Authentication authentication) {
        String metroCardNumber = resolveMetroCardNumber(authentication);
        Booking cancelled = bookingServices.cancelById(metroCardNumber, id);
        return ResponseEntity.ok(toResponse(cancelled));
    }

    @PostMapping("/book/{id}/scan")
    public ResponseEntity<BookingResponse> scanTicketAtGate(@PathVariable Long id) {
        Booking scanned = bookingServices.validateTicketForEntry(id);
        return ResponseEntity.ok(toResponse(scanned));
    }

    private BookingResponse toResponse(Booking booking) {
        boolean expired = booking.isExpired();
        BookingStatus effectiveStatus = (expired && booking.getBookingStatus() == BookingStatus.CONFIRMED)
                ? BookingStatus.EXPIRED
                : booking.getBookingStatus();

        return BookingResponse.builder()
                .id(booking.getId())
                .originStation(booking.getOriginStation() != null ? booking.getOriginStation().getName() : null)
                .destinationStation(booking.getDestinationStation() != null ? booking.getDestinationStation().getName() : null)
                .fare(booking.getFare())
                .bookingStatus(effectiveStatus)
                .qrCodePath(booking.getQrCodePath())
                .dateTime(booking.getDateTime())
                .expiresAt(booking.getExpiresAt())
                .expired(expired)
                .paymentMethod(booking.getPaymentMethod())
                .razorpayPaymentId(booking.getRazorpayPaymentId())
                .metroCardNumber(booking.getUser() != null ? booking.getUser().getMetroCardNumber() : null)
                .passengerName(booking.getUser() != null ? booking.getUser().getHolderName() : null)
                .build();
    }
}
