package com.gogidix.rapidassist.feature.flags.service.infrastructure.messaging.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.gogidix.rapidassist.feature.flags.service.domain.event.DomainEvent;
import com.gogidix.rapidassist.feature.flags.service.domain.event.FeatureFlagCreatedEvent;
import com.gogidix.rapidassist.feature.flags.service.domain.event.FeatureFlagDeletedEvent;
import com.gogidix.rapidassist.feature.flags.service.domain.event.FeatureFlagUpdatedEvent;
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
 * Kafka event publisher for feature flag domain events.
 *
 * <p>This publisher sends domain events to Kafka topics for consumption
 * by other services in the system.
 */
@Component
@Profile("!kafka-disabled")
public class FeatureFlagEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(FeatureFlagEventPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String eventsTopic;

    @Autowired
    public FeatureFlagEventPublisher(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${app.kafka.topics.events:feature-flag.events}") String eventsTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.eventsTopic = eventsTopic;
        this.objectMapper = JsonMapper.builder().findAndAddModules().build();
    }

    /**
     * Publishes a feature flag created event.
     *
     * @param event the event to publish
     * @return CompletableFuture that completes when published
     */
    @Transactional
    public CompletableFuture<Void> publishFeatureFlagCreated(FeatureFlagCreatedEvent event) {
        return publishEvent(event, "FeatureFlagCreated");
    }

    /**
     * Publishes a feature flag updated event.
     *
     * @param event the event to publish
     * @return CompletableFuture that completes when published
     */
    @Transactional
    public CompletableFuture<Void> publishFeatureFlagUpdated(FeatureFlagUpdatedEvent event) {
        return publishEvent(event, "FeatureFlagUpdated");
    }

    /**
     * Publishes a feature flag deleted event.
     *
     * @param event the event to publish
     * @return CompletableFuture that completes when published
     */
    @Transactional
    public CompletableFuture<Void> publishFeatureFlagDeleted(FeatureFlagDeletedEvent event) {
        return publishEvent(event, "FeatureFlagDeleted");
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
