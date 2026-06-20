package com.cfs.RideTap.ai.tools;

import com.cfs.RideTap.models.Booking;
import com.cfs.RideTap.models.UserEntity;
import com.cfs.RideTap.services.interfaces.BookingHistoryServices;
import com.cfs.RideTap.services.interfaces.UserServices;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookingHistoryTool {
    @Autowired
    private BookingHistoryServices bookingHistoryServices;

    @Autowired
    private UserServices userServices;

    @Tool(
            description = """
    Retrieve booking history of a user.
    Use this tool when the user asks about previous trips,
    booking history, past bookings, travel history,
    or booked tickets.
    """
    )    public List<Booking> getUserBookings() {

        String email =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        UserEntity user =
                userServices.findByEmail(email);

        return bookingHistoryServices.findAllUserBookings(
                user.getMetroCardNumber()
        );
    }
}
