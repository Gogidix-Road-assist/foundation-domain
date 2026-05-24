package com.gogidix.rapidassist.country.localization.config.service.domain.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.UUID;

/**
 * Base class for all domain events in the Country Localization service.
 * Domain events represent something that happened in the domain that
 * domain experts care about.
 *
 * @param <T> The type of the aggregate that generated this event
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class DomainEvent<T> {

    @JsonProperty("eventId")
    private final String eventId;

    @JsonProperty("aggregateId")
    private final String aggregateId;

    @JsonProperty("eventType")
    private final String eventType;

    @JsonProperty("occurredAt")
    private final Instant occurredAt;

    @JsonProperty("tenantId")
    private final String tenantId;

    @JsonProperty("version")
    private final Integer version;

    protected DomainEvent(String aggregateId, String eventType, String tenantId, Integer version) {
        this.eventId = UUID.randomUUID().toString();
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.occurredAt = Instant.now();
        this.tenantId = tenantId;
        this.version = version;
    }

    /**
     * Gets the unique identifier of this event instance.
     *
     * @return the event ID
     */
    public String eventId() {
        return eventId;
    }

    /**
     * Gets the ID of the aggregate that generated this event.
     *
     * @return the aggregate ID
     */
    public String aggregateId() {
        return aggregateId;
    }

    /**
     * Gets the type of this event (e.g., "CountryLocalizationCreated", "CountryLocalizationUpdated").
     *
     * @return the event type
     */
    public String eventType() {
        return eventType;
    }

    /**
     * Gets the timestamp when this event occurred.
     *
     * @return the occurrence timestamp
     */
    public Instant occurredAt() {
        return occurredAt;
    }

    /**
     * Gets the tenant ID for multi-tenancy isolation.
     *
     * @return the tenant ID
     */
    public String tenantId() {
        return tenantId;
    }

    /**
     * Gets the version of the aggregate that generated this event.
     *
     * @return the aggregate version
     */
    public Integer version() {
        return version;
    }
}
