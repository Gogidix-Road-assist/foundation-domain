package com.gogidix.rapidassist.dynamic.routing.config.service.infrastructure.messaging.events;

import com.gogidix.rapidassist.dynamic.routing.config.service.domain.event.DomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Event store for routing rule domain events.
 *
 * <p>This component stores all domain events in MongoDB for audit purposes
 * and replays events when needed.
 */
@Component
@Profile("!event-store-disabled")
public class RoutingRuleEventStore {

    private static final Logger logger = LoggerFactory.getLogger(RoutingRuleEventStore.class);

    private final MongoTemplate mongoTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String eventsCollection;
    private final boolean eventPublishingEnabled;

    @Autowired
    public RoutingRuleEventStore(
            MongoTemplate mongoTemplate,
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${app.event-store.collection:routing-rule-events}") String eventsCollection,
            @Value("${app.events.enabled:true}") boolean eventPublishingEnabled) {
        this.mongoTemplate = mongoTemplate;
        this.kafkaTemplate = kafkaTemplate;
        this.eventsCollection = eventsCollection;
        this.eventPublishingEnabled = eventPublishingEnabled;
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
                event.tenantId(),
                event.version(),
                event.occurredAt(),
                event // Store the actual event object
            );

            mongoTemplate.save(storedEvent, eventsCollection);
            logger.debug("Stored event: {} for aggregate: {}", event.eventType(), event.aggregateId());
        } catch (Exception e) {
            logger.error("Failed to store event: {} for aggregate: {}",
                event.eventType(), event.aggregateId(), e);
            // Event storage failure should not prevent the main operation
        }
    }

    /**
     * Retrieves all events for an aggregate.
     *
     * @param aggregateId the aggregate ID
     * @return list of stored events
     */
    public List<StoredEvent> getEventsForAggregate(String aggregateId) {
        Query query = new Query(Criteria.where("aggregateId").is(aggregateId));
        query.with(org.springframework.data.domain.Sort.by(
            org.springframework.data.domain.Sort.Order.asc("occurredAt")
        ));
        return mongoTemplate.find(query, StoredEvent.class, eventsCollection);
    }

    /**
     * Retrieves all events for a tenant.
     *
     * @param tenantId the tenant ID
     * @return list of stored events
     */
    public List<StoredEvent> getEventsForTenant(String tenantId) {
        Query query = new Query(Criteria.where("tenantId").is(tenantId));
        query.with(org.springframework.data.domain.Sort.by(
            org.springframework.data.domain.Sort.Order.asc("occurredAt")
        ));
        return mongoTemplate.find(query, StoredEvent.class, eventsCollection);
    }

    /**
     * Retrieves events for an aggregate since a specific version.
     *
     * @param aggregateId the aggregate ID
     * @param sinceVersion the version to start from
     * @return list of stored events
     */
    public List<StoredEvent> getEventsForAggregateSince(String aggregateId, int sinceVersion) {
        Query query = new Query(Criteria.where("aggregateId").is(aggregateId)
            .and("version").gt(sinceVersion));
        query.with(org.springframework.data.domain.Sort.by(
            org.springframework.data.domain.Sort.Order.asc("occurredAt")
        ));
        return mongoTemplate.find(query, StoredEvent.class, eventsCollection);
    }

    /**
     * Record class for stored events in MongoDB.
     */
    public static class StoredEvent {
        private String eventId;
        private String aggregateId;
        private String eventType;
        private String tenantId;
        private Integer version;
        private Instant occurredAt;
        private DomainEvent<?> eventData;

        // Default constructor for MongoDB
        public StoredEvent() {
        }

        public StoredEvent(String eventId, String aggregateId, String eventType,
                          String tenantId, Integer version, Instant occurredAt,
                          DomainEvent<?> eventData) {
            this.eventId = eventId;
            this.aggregateId = aggregateId;
            this.eventType = eventType;
            this.tenantId = tenantId;
            this.version = version;
            this.occurredAt = occurredAt;
            this.eventData = eventData;
        }

        // Getters and setters
        public String getEventId() { return eventId; }
        public void setEventId(String eventId) { this.eventId = eventId; }

        public String getAggregateId() { return aggregateId; }
        public void setAggregateId(String aggregateId) { this.aggregateId = aggregateId; }

        public String getEventType() { return eventType; }
        public void setEventType(String eventType) { this.eventType = eventType; }

        public String getTenantId() { return tenantId; }
        public void setTenantId(String tenantId) { this.tenantId = tenantId; }

        public Integer getVersion() { return version; }
        public void setVersion(Integer version) { this.version = version; }

        public Instant getOccurredAt() { return occurredAt; }
        public void setOccurredAt(Instant occurredAt) { this.occurredAt = occurredAt; }

        public DomainEvent<?> getEventData() { return eventData; }
        public void setEventData(DomainEvent<?> eventData) { this.eventData = eventData; }
    }
}
