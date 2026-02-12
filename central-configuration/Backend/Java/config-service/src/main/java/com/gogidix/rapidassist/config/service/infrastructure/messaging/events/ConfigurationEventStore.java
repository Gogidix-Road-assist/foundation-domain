package com.gogidix.rapidassist.config.service.infrastructure.messaging.events;

import com.gogidix.rapidassist.config.service.domain.event.DomainEvent;
import com.gogidix.rapidassist.config.service.infrastructure.messaging.kafka.ConfigurationEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.concurrent.CompletableFuture;

/**
 * Event store component for publishing domain events after successful transactions.
 *
 * <p>This component listens for transaction completion and publishes
 * all domain events that were generated during the transaction.
 *
 * <p>Uses Spring's transaction event listener to ensure events are only
 * published after the transaction commits successfully.
 */
@Component
@Profile("!kafka-disabled")
public class ConfigurationEventStore {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationEventStore.class);

    private final ConfigurationEventPublisher eventPublisher;

    @Autowired
    public ConfigurationEventStore(ConfigurationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * Handles domain events after transaction commits.
     *
     * @param event the domain event to publish
     * @return CompletableFuture that completes when published
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public CompletableFuture<Void> handle(DomainEvent<?> event) {
        logger.debug("Handling domain event: {} for tenant: {}",
            event.eventType(), event.tenantId());

        return eventPublisher.publishEvent(event)
            .exceptionally(throwable -> {
                logger.error("Failed to publish event {} for tenant: {}. Event will be lost.",
                    event.eventType(), event.tenantId(), throwable);
                // In production, implement dead letter queue or retry mechanism here
                return null;
            });
    }

    /**
     * Checks if the event store is available.
     *
     * @return true if Kafka is available
     */
    public boolean isAvailable() {
        return eventPublisher.isKafkaAvailable();
    }
}
