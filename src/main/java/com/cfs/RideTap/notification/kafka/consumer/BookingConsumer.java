package com.cfs.RideTap.notification.kafka.consumer;

import com.cfs.RideTap.notification.dto.event.BookingCancelledEvent;
import com.cfs.RideTap.notification.dto.event.BookingCreatedEvent;
import com.cfs.RideTap.notification.service.interfaces.NotificationServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class BookingConsumer {

    @Autowired
    private NotificationServices notificationServices;

    @KafkaListener(
            topics = "booking-created",
            groupId = "ridetap-group"
    )
    public void consume(BookingCreatedEvent bookingCreatedEvent) {
        notificationServices.sendBookingConfirmation(bookingCreatedEvent);
    }

    @KafkaListener(
            topics = "booking-cancelled",
            groupId = "ridetap-group"
    )
    public void consumeCancellation(BookingCancelledEvent event) {
        notificationServices.sendBookingCancellation(event);
    }
}
