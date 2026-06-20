package com.cfs.RideTap.kafka.producer;

import com.cfs.RideTap.dtos.eventDTO.BookingCancelledEvent;
import com.cfs.RideTap.dtos.eventDTO.BookingCreatedEvent;
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
