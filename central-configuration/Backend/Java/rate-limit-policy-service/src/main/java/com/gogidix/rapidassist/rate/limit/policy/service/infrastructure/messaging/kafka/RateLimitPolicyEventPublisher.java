package com.gogidix.rapidassist.rate.limit.policy.service.infrastructure.messaging.kafka;

import com.gogidix.rapidassist.rate.limit.policy.service.domain.event.DomainEvent;
import com.gogidix.rapidassist.rate.limit.policy.service.infrastructure.messaging.events.RateLimitPolicyEventStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.concurrent.CompletableFuture;

/**
 * Kafka event publisher for Rate Limit Policy domain events.
 *
 * <p>This component publishes domain events to Kafka topics for consumption
 * by other services in the system.
 */
@Component
@Profile("!kafka-disabled")
public class RateLimitPolicyEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitPolicyEventPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final RateLimitPolicyEventStore eventStore;

    public RateLimitPolicyEventPublisher(
            KafkaTemplate<String, Object> kafkaTemplate,
            RateLimitPolicyEventStore eventStore) {
        this.kafkaTemplate = kafkaTemplate;
        this.eventStore = eventStore;
    }

    /**
     * Publishes a domain event to Kafka.
     *
     * @param event the domain event to publish
     */
    @Transactional
    public void publish(DomainEvent<?> event) {
        try {
            // Store event in event store first
            eventStore.store(event);

            // Publish to Kafka
            String topic = determineTopic(event);
            String key = event.aggregateId();

            CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(topic, key, event);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    logger.info(
                        "Successfully published event {} to topic {} for aggregate {}",
                        event.eventType(),
                        topic,
                        event.aggregateId()
                    );
                } else {
                    logger.error(
                        "Failed to publish event {} to topic {} for aggregate {}: {}",
                        event.eventType(),
                        topic,
                        event.aggregateId(),
                        ex.getMessage(),
                        ex
                    );
                }
            });
        } catch (Exception e) {
            logger.error("Error publishing event {}: {}", event.eventType(), e.getMessage(), e);
            throw new RuntimeException("Failed to publish event", e);
        }
    }

    /**
     * Determines the appropriate Kafka topic for an event.
     *
     * @param event the domain event
     * @return the topic name
     */
    private String determineTopic(DomainEvent<?> event) {
        return switch (event.eventType()) {
            case "RateLimitPolicyCreated",
                 "RateLimitPolicyUpdated",
                 "RateLimitPolicyDeleted" -> KafkaConfig.RATE_LIMIT_POLICY_EVENTS_TOPIC;
            default -> KafkaConfig.RATE_LIMIT_POLICY_CHANGES_TOPIC;
        };
    }
}
