package com.gogidix.rapidassist.orchestration.dispatching.infrastructure.messaging.events;

import com.gogidix.rapidassist.orchestration.dispatching.domain.event.DispatchEvent;
import com.gogidix.rapidassist.orchestration.dispatching.infrastructure.messaging.kafka.KafkaEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Implementation of EventPublisher for dispatch events
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DispatchEventPublisher implements EventPublisher {

    private final KafkaEventPublisher kafkaEventPublisher;

    @Override
    public void publish(DispatchEvent event) {
        kafkaEventPublisher.publish(event);
    }

    @Override
    public void publish(String eventType, Object payload) {
        kafkaEventPublisher.publish("dispatch-events", eventType, payload);
    }
}
