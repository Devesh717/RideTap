package com.cfs.RideTap.services.interfaces;

import com.cfs.RideTap.dtos.eventDTO.BookingCancelledEvent;
import com.cfs.RideTap.dtos.eventDTO.BookingCreatedEvent;

public interface NotificationServices {

    public void sendBookingConfirmation(BookingCreatedEvent bookingCreatedEvent);

    void sendBookingCancellation(BookingCancelledEvent bookingCancelledEvent);
}
