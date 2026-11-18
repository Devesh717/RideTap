package com.cfs.RideTap.notification.service.interfaces;

import com.cfs.RideTap.notification.dto.event.BookingCancelledEvent;
import com.cfs.RideTap.notification.dto.event.BookingCreatedEvent;

public interface NotificationServices {
    void sendBookingConfirmation(BookingCreatedEvent bookingCreatedEvent);
    void sendBookingCancellation(BookingCancelledEvent bookingCancelledEvent);
}
