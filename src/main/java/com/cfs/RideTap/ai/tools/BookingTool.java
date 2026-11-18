package com.cfs.RideTap.ai.tools;

import com.cfs.RideTap.booking.dto.request.BookingRequest;
import com.cfs.RideTap.booking.enums.BookingStatus;
import com.cfs.RideTap.booking.model.Booking;
import com.cfs.RideTap.booking.service.interfaces.BookingServices;
import com.cfs.RideTap.fare.service.interfaces.FareCalculator;
import com.cfs.RideTap.payment.entity.PaymentMethod;
import com.cfs.RideTap.user.model.UserEntity;
import com.cfs.RideTap.user.service.interfaces.UserServices;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class BookingTool {

    @Autowired
    private BookingServices bookingServices;

    @Autowired
    private UserServices userServices;

    @Autowired
    private FareCalculator fareCalculator;

    private UserEntity getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userServices.findByEmail(email);
    }

    @Tool(
            description = """
    Book a metro ticket between an origin station and destination station for the currently logged-in user
    using their RideTap Metro Card balance. Never ask the user for their Metro Card number or email.
    """
    )
    public String createBooking(String originStation, String destinationStation) {
        try {
            UserEntity user = getCurrentUser();
            double fare = fareCalculator.calculate(originStation.trim(), destinationStation.trim());

            if (user.getBalance() < fare) {
                return String.format(
                        "Booking failed due to insufficient Metro Card balance. Required Fare: ₹%.2f | Available Balance: ₹%.2f | Shortfall: ₹%.2f. Please recharge your Metro Card in the Wallet section.",
                        fare, user.getBalance(), (fare - user.getBalance())
                );
            }

            BookingRequest bookingRequest = new BookingRequest();
            bookingRequest.setOriginStation(originStation.trim());
            bookingRequest.setDestinationStation(destinationStation.trim());
            bookingRequest.setPaymentMethod(PaymentMethod.METROCARD);

            Booking booking = bookingServices.book(user.getMetroCardNumber(), bookingRequest);
            UserEntity refreshedUser = userServices.findByEmail(user.getEmail());

            return String.format(
                    "Ticket Booked Successfully! Ticket #%d | Route: %s -> %s | Fare Deducted: ₹%.2f | Remaining Balance: ₹%.2f | Status: %s | Issued At: %s | Valid Until: %s",
                    booking.getId(),
                    booking.getOriginStation().getName(),
                    booking.getDestinationStation().getName(),
                    booking.getFare(),
                    refreshedUser.getBalance(),
                    booking.getBookingStatus(),
                    booking.getDateTime(),
                    booking.getEffectiveExpiresAt()
            );
        } catch (Exception e) {
            return "Unable to complete booking: " + e.getMessage();
        }
    }

    @Tool(
            description = """
    Cancel an active/confirmed metro ticket by its Booking ID for the currently logged-in user
    and automatically refund the ticket fare back to their RideTap Metro Card balance.
    """
    )
    public String cancelBooking(Long bookingId) {
        try {
            UserEntity user = getCurrentUser();
            Booking cancelled = bookingServices.cancelById(user.getMetroCardNumber(), bookingId);
            UserEntity refreshedUser = userServices.findByEmail(user.getEmail());

            return String.format(
                    "Ticket #%d Cancelled! Route: %s -> %s | Refunded Amount: ₹%.2f | Updated Metro Card Balance: ₹%.2f | Status: %s",
                    cancelled.getId(),
                    cancelled.getOriginStation().getName(),
                    cancelled.getDestinationStation().getName(),
                    cancelled.getFare(),
                    refreshedUser.getBalance(),
                    BookingStatus.CANCELLED
            );
        } catch (Exception e) {
            return "Unable to cancel Ticket #" + bookingId + ": " + e.getMessage();
        }
    }
}
