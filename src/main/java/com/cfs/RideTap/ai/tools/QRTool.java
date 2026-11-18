package com.cfs.RideTap.ai.tools;

import com.cfs.RideTap.booking.enums.BookingStatus;
import com.cfs.RideTap.booking.model.Booking;
import com.cfs.RideTap.booking.service.interfaces.BookingHistoryServices;
import com.cfs.RideTap.booking.service.interfaces.BookingServices;
import com.cfs.RideTap.user.model.UserEntity;
import com.cfs.RideTap.user.service.interfaces.UserServices;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class QRTool {

    @Autowired
    private UserServices userServices;

    @Autowired
    private BookingServices bookingServices;

    @Autowired
    private BookingHistoryServices bookingHistoryServices;

    private String formatGatePassStatus(Booking booking) {
        boolean expired = booking.isExpired();
        BookingStatus status = (expired && booking.getBookingStatus() == BookingStatus.CONFIRMED)
                ? BookingStatus.EXPIRED
                : booking.getBookingStatus();

        if (status == BookingStatus.CANCELLED) {
            return String.format("Ticket #%d (%s -> %s) is CANCELLED and voided. QR gate pass is no longer valid.",
                    booking.getId(), booking.getOriginStation().getName(), booking.getDestinationStation().getName());
        }
        if (status == BookingStatus.EXPIRED) {
            return String.format("Ticket #%d (%s -> %s) EXPIRED at %s. QR gate pass is no longer valid for entry.",
                    booking.getId(), booking.getOriginStation().getName(), booking.getDestinationStation().getName(),
                    booking.getEffectiveExpiresAt());
        }
        if (status == BookingStatus.USED) {
            return String.format("Ticket #%d (%s -> %s) has already been USED for travel.",
                    booking.getId(), booking.getOriginStation().getName(), booking.getDestinationStation().getName());
        }

        return String.format(
                "Active Gate Pass Ready — Ticket #%d | Route: %s -> %s | Status: CONFIRMED | Valid Until: %s. Open 'My Tickets' -> 'Show QR Pass' in RideTap to scan at the AFC gate.",
                booking.getId(),
                booking.getOriginStation().getName(),
                booking.getDestinationStation().getName(),
                booking.getEffectiveExpiresAt()
        );
    }

    @Tool(
            description = """
        Check QR gate pass status and validity for a route (origin and destination stations)
        belonging to the logged-in user. Never returns raw file paths or cancelled/expired QR passes.
        """
    )
    public String getQRCodeForRoute(String originStation, String destinationStation) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Booking booking = bookingServices
                .findUserBookingByRoute(email, originStation.trim(), destinationStation.trim())
                .orElse(null);

        if (booking == null) {
            return "No booking found between " + originStation + " and " + destinationStation + " for your account.";
        }
        return formatGatePassStatus(booking);
    }

    @Tool(
            description = """
        Check QR gate pass status and validity by Booking ID for the logged-in user.
        """
    )
    public String getQRPassStatusById(Long bookingId) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            UserEntity user = userServices.findByEmail(email);
            Booking booking = bookingHistoryServices.findById(user.getMetroCardNumber(), bookingId);
            return formatGatePassStatus(booking);
        } catch (Exception e) {
            return "Ticket #" + bookingId + " was not found for your account.";
        }
    }
}
