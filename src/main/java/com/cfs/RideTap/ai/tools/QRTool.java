package com.cfs.RideTap.ai.tools;


import com.cfs.RideTap.models.Booking;
import com.cfs.RideTap.models.UserEntity;
import com.cfs.RideTap.repositories.BookingRepository;
import com.cfs.RideTap.services.interfaces.BookingServices;
import com.cfs.RideTap.services.interfaces.QRServices;
import com.cfs.RideTap.services.interfaces.UserServices;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class QRTool {

    @Autowired
    private QRServices qrServices;

    @Autowired
    private UserServices userServices;

    @Autowired
    private BookingServices bookingServices;

    @Tool(
            description = """
        Retrieve QR code for a booking using origin and destination stations.
        Only returns bookings belonging to the logged-in user.
        """
    )
    public String getQRCodeForRoute(
            String originStation,
            String destinationStation) {

        String email =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        Booking booking =
                bookingServices
                        .findUserBookingByRoute(
                                email,
                                originStation,
                                destinationStation)
                        .orElse(null);

        if (booking == null) {
            return """
               No booking found for this route
               belonging to the current user.
               """;
        }

        return booking.getQrCodePath();
    }
}
