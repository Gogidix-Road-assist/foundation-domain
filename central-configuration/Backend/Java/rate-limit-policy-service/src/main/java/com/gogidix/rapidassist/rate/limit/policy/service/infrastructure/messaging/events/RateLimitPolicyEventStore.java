package com.gogidix.rapidassist.rate.limit.policy.service.infrastructure.messaging.events;

import com.gogidix.rapidassist.rate.limit.policy.service.domain.event.DomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactoryBean;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Event store for Rate Limit Policy domain events.
 *
 * <p>This component stores all domain events in MongoDB for audit purposes
 * and event sourcing capabilities.
 */
@Component
public class RateLimitPolicyEventStore {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitPolicyEventStore.class);

    private static final String COLLECTION_NAME = "rate_limit_policy_events";

    private final MongoTemplate mongoTemplate;

    public RateLimitPolicyEventStore(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * Stores a domain event.
     *
     * @param event the domain event to store
     */
    public void store(DomainEvent<?> event) {
        try {
            StoredEvent storedEvent = new StoredEvent(
                event.eventId(),
                event.aggregateId(),
                event.eventType(),
                event.occurredAt(),
                event.tenantId(),
                event.version(),
                event
            );

            mongoTemplate.save(storedEvent, COLLECTION_NAME);
            logger.debug("Stored event {} for aggregate {}", event.eventType(), event.aggregateId());
        } catch (Exception e) {
            logger.error("Failed to store event {}: {}", event.eventType(), e.getMessage(), e);
            throw new RuntimeException("Failed to store event", e);
        }
    }

    /**
     * Retrieves all events for an aggregate.
     *
     * @param aggregateId the aggregate ID
     * @return list of events for the aggregate
     */
    public List<StoredEvent> getEventsForAggregate(String aggregateId) {
        Query query = new Query(Criteria.where("aggregateId").is(aggregateId));
        query.with(org.springframework.data.domain.Sort.by(
            org.springframework.data.domain.Sort.Order.asc("occurredAt")
        ));
        return mongoTemplate.find(query, StoredEvent.class, COLLECTION_NAME);
    }

    /**
     * Retrieves events for a tenant.
     *
     * @param tenantId the tenant ID
     * @param pageable the pagination information
     * @return page of events
     */
    public Page<StoredEvent> getEventsForTenant(String tenantId, Pageable pageable) {
        Query query = new Query(Criteria.where("tenantId").is(tenantId));
        long count = mongoTemplate.count(query, StoredEvent.class, COLLECTION_NAME);
        query.with(pageable);
        List<StoredEvent> events = mongoTemplate.find(query, StoredEvent.class, COLLECTION_NAME);
        return new org.springframework.data.domain.PageImpl<>(events, pageable, count);
    }

    /**
     * Represents a stored domain event.
     *
     * @param eventId     the event ID
     * @param aggregateId the aggregate ID
     * @param eventType   the event type
     * @param occurredAt  the timestamp when the event occurred
     * @param tenantId    the tenant ID
     * @param version     the aggregate version
     * @param eventData   the event data
     */
    public record StoredEvent(
        String eventId,
        String aggregateId,
        String eventType,
        Instant occurredAt,
        String tenantId,
        Integer version,
        DomainEvent<?> eventData
    ) {}
}
