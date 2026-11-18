package com.cfs.RideTap.common.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@EnableKafka
@Slf4j
public class KafkaConfig {
    @Bean
    public NewTopic bookingCreatedTopic() {
        return TopicBuilder
                .name("booking-created")
                .build();
    }

    @Bean
    public NewTopic bookingCancelledTopic() {
        return TopicBuilder
                .name("booking-cancelled")
                .build();
    }
}
