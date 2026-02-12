package com.gogidix.rapidassist.orchestration.dispatching.infrastructure.messaging.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.rapidassist.orchestration.dispatching.domain.event.DispatchEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Kafka publisher for dispatch events
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String DISPATCH_EVENTS_TOPIC = "dispatch-events";

    public void publish(DispatchEvent event) {
        try {
            String key = event.getDispatchId();
            kafkaTemplate.send(DISPATCH_EVENTS_TOPIC, key, event);

            log.debug("Published event {} for dispatch {}",
                event.getEventType(), event.getDispatchId());

        } catch (Exception e) {
            log.error("Failed to publish event {} for dispatch {}",
                event.getEventType(), event.getDispatchId(), e);

            // Event publishing failure should not break the main flow
            // Consider implementing dead letter queue pattern
        }
    }

    public void publish(String topic, String key, Object payload) {
        try {
            kafkaTemplate.send(topic, key, payload);
            log.debug("Published message to topic {} with key {}", topic, key);
        } catch (Exception e) {
            log.error("Failed to publish message to topic {}", topic, e);
        }
    }
}
