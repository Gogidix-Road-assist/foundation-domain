package com.gogidix.rapidassist.common.domain.models.infrastructure.messaging.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Profile;

import java.util.Map;

@Component
@Profile("!kafka-disabled")
public class CommonDomainModelsEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(CommonDomainModelsEventPublisher.class);
    private static final String TOPIC = "common-domain-models.events";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public CommonDomainModelsEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishEvent(String eventType, Object payload) {
        publishEvent(eventType, null, payload);
    }

    public void publishEvent(String eventType, String aggregateId, Object payload) {
        Map<String, Object> event = Map.of(
            "eventType", eventType,
            "aggregateId", aggregateId != null ? aggregateId : "",
            "service", "common-domain-models",
            "timestamp", java.time.Instant.now().toString(),
            "payload", payload
        );
        kafkaTemplate.send(TOPIC, aggregateId != null ? aggregateId : eventType, event)
            .whenComplete((result, ex) -> {
                if (ex != null) {
                    logger.error("Failed to publish {} event to {}: {}", eventType, TOPIC, ex.getMessage());
                } else {
                    logger.debug("Published {} event to {} [partition={} offset={}]",
                        eventType, TOPIC, result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
                }
            });
    }
}