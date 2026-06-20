package com.cfs.RideTap.ai.tools;

import com.cfs.RideTap.dtos.bookingDTO.BookingRequest;
import com.cfs.RideTap.models.Booking;
import com.cfs.RideTap.services.interfaces.BookingServices;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BookingTool {

    @Autowired
    private BookingServices bookingServices;

    @Tool(
            description = """
                    Book a metro ticket between two stations.
                    Use when the user wants to book a ticket,
                    create a booking, reserve a ride,
                    or purchase a metro journey.
    """
    )
    public Booking createBooking(String metroCardNumber, String originStation, String destinationStation) {
        BookingRequest bookingRequest = new BookingRequest();

        bookingRequest.setOriginStation(originStation);
        bookingRequest.setDestinationStation(destinationStation);

        return bookingServices.book(metroCardNumber, bookingRequest);
    }
}
