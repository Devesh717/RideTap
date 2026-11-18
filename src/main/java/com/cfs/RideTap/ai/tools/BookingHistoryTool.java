package com.cfs.RideTap.ai.tools;

import com.cfs.RideTap.booking.enums.BookingStatus;
import com.cfs.RideTap.booking.model.Booking;
import com.cfs.RideTap.booking.service.interfaces.BookingHistoryServices;
import com.cfs.RideTap.user.model.UserEntity;
import com.cfs.RideTap.user.service.interfaces.UserServices;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookingHistoryTool {

    @Autowired
    private BookingHistoryServices bookingHistoryServices;

    @Autowired
    private UserServices userServices;

    private String formatBookingSummary(Booking b) {
        boolean expired = b.isExpired();
        BookingStatus effectiveStatus = (expired && b.getBookingStatus() == BookingStatus.CONFIRMED)
                ? BookingStatus.EXPIRED
                : b.getBookingStatus();

        return String.format(
                "Ticket #%d | %s -> %s | Fare: ₹%.2f | Status: %s | Booked: %s | Valid Until: %s",
                b.getId(),
                b.getOriginStation() != null ? b.getOriginStation().getName() : "N/A",
                b.getDestinationStation() != null ? b.getDestinationStation().getName() : "N/A",
                b.getFare(),
                effectiveStatus,
                b.getDateTime(),
                b.getEffectiveExpiresAt()
        );
    }

    @Tool(
            description = """
    Retrieve all metro bookings (or recent bookings) of the currently logged-in user,
    including ticket status (CONFIRMED, EXPIRED, CANCELLED, USED), fare, and expiration timestamp.
    """
    )
    public String getUserBookings() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userServices.findByEmail(email);

        List<Booking> bookings = bookingHistoryServices.findAllUserBookings(user.getMetroCardNumber());
        if (bookings.isEmpty()) {
            return "You have no metro ticket bookings yet.";
        }

        return bookings.stream()
                .sorted(Comparator.comparing(Booking::getDateTime, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(this::formatBookingSummary)
                .collect(Collectors.joining("\n"));
    }

    @Tool(
            description = """
    Retrieve detailed information for a specific booking by its Booking ID for the logged-in user,
    including whether it is still valid, expired, used, or cancelled.
    """
    )
    public String getBookingDetailsById(Long bookingId) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            UserEntity user = userServices.findByEmail(email);
            Booking booking = bookingHistoryServices.findById(user.getMetroCardNumber(), bookingId);
            return formatBookingSummary(booking);
        } catch (Exception e) {
            return "Booking #" + bookingId + " was not found for your account.";
        }
    }

    @Tool(
            description = """
    Filter the currently logged-in user's bookings by status: CONFIRMED, EXPIRED, CANCELLED, or USED.
    """
    )
    public String getUserBookingsByStatus(String status) {
        try {
            BookingStatus targetStatus = BookingStatus.valueOf(status.trim().toUpperCase());
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            UserEntity user = userServices.findByEmail(email);

            List<Booking> bookings = bookingHistoryServices.findByMetroCardNumberAndBookingStatus(
                    user.getMetroCardNumber(), targetStatus);

            if (bookings.isEmpty()) {
                return "No bookings found with status: " + targetStatus;
            }

            return bookings.stream()
                    .sorted(Comparator.comparing(Booking::getDateTime, Comparator.nullsLast(Comparator.reverseOrder())))
                    .map(this::formatBookingSummary)
                    .collect(Collectors.joining("\n"));
        } catch (IllegalArgumentException e) {
            return "Invalid status. Valid statuses are CONFIRMED, EXPIRED, CANCELLED, or USED.";
        }
    }
}
