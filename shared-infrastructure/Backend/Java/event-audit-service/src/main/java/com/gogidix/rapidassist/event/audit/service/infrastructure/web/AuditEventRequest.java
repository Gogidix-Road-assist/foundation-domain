package com.gogidix.rapidassist.event.audit.service.infrastructure.web;

import jakarta.validation.constraints.NotBlank;

import java.time.Instant;
import java.util.Map;

public class AuditEventRequest {

    @NotBlank
    private String eventType;

    @NotBlank
    private String entityType;

    @NotBlank
    private String entityId;

    private Instant occurredAt;

    private Map<String, Object> payload;

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(Instant occurredAt) {
        this.occurredAt = occurredAt;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public void setPayload(Map<String, Object> payload) {
        this.payload = payload;
    }
}
