package com.gogidix.rapidassist.policy.configuration.service.infrastructure.messaging.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.gogidix.rapidassist.policy.configuration.service.domain.event.DomainEvent;
import com.gogidix.rapidassist.policy.configuration.service.domain.event.PolicyCreatedEvent;
import com.gogidix.rapidassist.policy.configuration.service.domain.event.PolicyDeletedEvent;
import com.gogidix.rapidassist.policy.configuration.service.domain.event.PolicyUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

/**
 * Kafka event publisher for policy domain events.
 *
 * <p>This publisher sends domain events to Kafka topics for consumption
 * by other services in the system.
 */
@Component
@Profile("!kafka-disabled")
public class PolicyEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(PolicyEventPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String eventsTopic;

    @Autowired
    public PolicyEventPublisher(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${app.kafka.topics.events:policy.events}") String eventsTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.eventsTopic = eventsTopic;
        this.objectMapper = JsonMapper.builder().findAndAddModules().build();
    }

    /**
     * Publishes a policy created event.
     *
     * @param event the event to publish
     * @return CompletableFuture that completes when published
     */
    @Transactional
    public CompletableFuture<Void> publishPolicyCreated(PolicyCreatedEvent event) {
        return publishEvent(event, "PolicyCreated");
    }

    /**
     * Publishes a policy updated event.
     *
     * @param event the event to publish
     * @return CompletableFuture that completes when published
     */
    @Transactional
    public CompletableFuture<Void> publishPolicyUpdated(PolicyUpdatedEvent event) {
        return publishEvent(event, "PolicyUpdated");
    }

    /**
     * Publishes a policy deleted event.
     *
     * @param event the event to publish
     * @return CompletableFuture that completes when published
     */
    @Transactional
    public CompletableFuture<Void> publishPolicyDeleted(PolicyDeletedEvent event) {
        return publishEvent(event, "PolicyDeleted");
    }

    /**
     * Publishes a generic domain event.
     *
     * @param event the event to publish
     * @return CompletableFuture that completes when published
     */
    @Transactional
    public CompletableFuture<Void> publishEvent(DomainEvent<?> event) {
        return publishEvent(event, event.eventType());
    }

    /**
     * Publishes an event to Kafka with tenant-aware routing.
     *
     * @param event     the domain event
     * @param eventType the event type for logging
     * @return CompletableFuture that completes when published
     */
    private CompletableFuture<Void> publishEvent(DomainEvent<?> event, String eventType) {
        String key = event.tenantId() + ":" + event.aggregateId();

        logger.debug("Publishing {} event for tenant: {}, aggregate: {}",
            eventType, event.tenantId(), event.aggregateId());

        try {
            // Validate event can be serialized
            String json = objectMapper.writeValueAsString(event);
            logger.trace("Event JSON: {}", json);
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize event: {}", eventType, e);
            CompletableFuture<Void> failed = new CompletableFuture<>();
            failed.completeExceptionally(e);
            return failed;
        }

        CompletableFuture<SendResult<String, Object>> future =
            kafkaTemplate.send(eventsTopic, key, event);

        return future.thenAccept(result -> {
            logger.info("Successfully published {} event for tenant: {}, offset: {}",
                eventType, event.tenantId(), result.getRecordMetadata().offset());
        }).exceptionally(throwable -> {
            logger.error("Failed to publish {} event for tenant: {}",
                eventType, event.tenantId(), throwable);
            throw new RuntimeException("Failed to publish event to Kafka", throwable);
        });
    }

    /**
     * Checks if Kafka is available and ready.
     *
     * @return true if Kafka is available
     */
    public boolean isKafkaAvailable() {
        try {
            kafkaTemplate.getProducerFactory().createProducer().partitionsFor(eventsTopic);
            return true;
        } catch (Exception e) {
            logger.warn("Kafka is not available: {}", e.getMessage());
            return false;
        }
    }
}
