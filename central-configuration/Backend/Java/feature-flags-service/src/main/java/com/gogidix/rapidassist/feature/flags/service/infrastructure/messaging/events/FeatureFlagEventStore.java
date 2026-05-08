package com.gogidix.rapidassist.feature.flags.service.infrastructure.messaging.events;

import com.gogidix.rapidassist.feature.flags.service.domain.event.DomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Event store for feature flag domain events.
 *
 * <p>This component listens for domain events and persists them
 * to the event store for audit and replay purposes.
 */
@Component
public class FeatureFlagEventStore {

    private static final Logger logger = LoggerFactory.getLogger(FeatureFlagEventStore.class);

    /**
     * Listens for feature flag events and persists them.
     *
     * @param events          the list of events
     * @param acknowledgment  the acknowledgment for manual commit
     * @param partition       the partition number
     * @param offset          the offset
     */
    @KafkaListener(
        topics = "${app.kafka.topics.events:feature-flag.events}",
        groupId = "${spring.kafka.consumer.group-id:feature-flags-service}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleFeatureFlagEvents(
            @Payload List<DomainEvent<?>> events,
            Acknowledgment acknowledgment,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        for (DomainEvent<?> event : events) {
            logger.info("Storing event: type={}, aggregateId={}, tenantId={}, version={}",
                event.eventType(), event.aggregateId(), event.tenantId(), event.version());

            // Persist event to event store (database)
            // This would typically save to an events collection
            try {
                persistEvent(event);
            } catch (Exception e) {
                logger.error("Failed to persist event: {}", event.eventType(), e);
                // Depending on requirements, we might want to throw here
                // to trigger retry or dead letter queue handling
            }
        }

        // Manually acknowledge the batch
        if (acknowledgment != null) {
            acknowledgment.acknowledge();
        }
    }

    /**
     * Persists a domain event to the event store.
     *
     * @param event the event to persist
     */
    private void persistEvent(DomainEvent<?> event) {
        // Implementation would save to MongoDB events collection
        // This is a placeholder for the actual persistence logic
        logger.debug("Persisting event to store: eventId={}, eventType={}",
            event.eventId(), event.eventType());
    }
}
