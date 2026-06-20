package com.cfs.RideTap.kafka.consumer;

import com.cfs.RideTap.dtos.eventDTO.BookingCancelledEvent;
import com.cfs.RideTap.dtos.eventDTO.BookingCreatedEvent;
import com.cfs.RideTap.services.interfaces.NotificationServices;
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
