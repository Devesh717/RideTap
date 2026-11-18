package com.cfs.RideTap.notification.kafka.producer;

import com.cfs.RideTap.notification.dto.event.BookingCancelledEvent;
import com.cfs.RideTap.notification.dto.event.BookingCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String CONFIRM_TOPIC = "booking-created";
    private static final String CANCEL_TOPIC = "booking-cancelled";

    public void createPublish(BookingCreatedEvent event) {
        kafkaTemplate.send(CONFIRM_TOPIC, event);
    }

    public void cancelPublish(BookingCancelledEvent event) {
        kafkaTemplate.send(CANCEL_TOPIC, event);
    }
}
