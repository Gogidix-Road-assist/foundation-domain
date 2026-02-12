package com.gogidix.rapidassist.access.control.service.domain.event;

import java.time.Instant;
import java.util.Objects;

/**
 * Domain Event: AccessDeniedEvent
 *
 * Published when an access control check results in denial.
 * This event is important for security monitoring and audit trails.
 *
 * This is a DOMAIN event with ZERO framework dependencies.
 */
public class AccessDeniedEvent {

    private final String eventId;
    private final String tenantId;
    private final String subjectId;
    private final String subjectType;
    private final String resource;
    private final String action;
    private final String denialReason;
    private final Instant deniedAt;
    private final String correlationId;
    private final String sourceIp;
    private final String userAgent;

    private AccessDeniedEvent(Builder builder) {
        this.eventId = Objects.requireNonNull(builder.eventId, "eventId is required");
        this.tenantId = Objects.requireNonNull(builder.tenantId, "tenantId is required");
        this.subjectId = Objects.requireNonNull(builder.subjectId, "subjectId is required");
        this.subjectType = Objects.requireNonNull(builder.subjectType, "subjectType is required");
        this.resource = Objects.requireNonNull(builder.resource, "resource is required");
        this.action = Objects.requireNonNull(builder.action, "action is required");
        this.denialReason = Objects.requireNonNull(builder.denialReason, "denialReason is required");
        this.deniedAt = Objects.requireNonNullElse(builder.deniedAt, Instant.now());
        this.correlationId = builder.correlationId;
        this.sourceIp = builder.sourceIp;
        this.userAgent = builder.userAgent;
    }

    /**
     * Create a new builder for this event.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Get the event type name for serialization.
     */
    public String getEventType() {
        return "AccessDenied";
    }

    // Getters
    public String getEventId() { return eventId; }
    public String getTenantId() { return tenantId; }
    public String getSubjectId() { return subjectId; }
    public String getSubjectType() { return subjectType; }
    public String getResource() { return resource; }
    public String getAction() { return action; }
    public String getDenialReason() { return denialReason; }
    public Instant getDeniedAt() { return deniedAt; }
    public String getCorrelationId() { return correlationId; }
    public String getSourceIp() { return sourceIp; }
    public String getUserAgent() { return userAgent; }

    public static class Builder {
        private String eventId;
        private String tenantId;
        private String subjectId;
        private String subjectType;
        private String resource;
        private String action;
        private String denialReason;
        private Instant deniedAt;
        private String correlationId;
        private String sourceIp;
        private String userAgent;

        public Builder eventId(String eventId) { this.eventId = eventId; return this; }
        public Builder tenantId(String tenantId) { this.tenantId = tenantId; return this; }
        public Builder subjectId(String subjectId) { this.subjectId = subjectId; return this; }
        public Builder subjectType(String subjectType) { this.subjectType = subjectType; return this; }
        public Builder resource(String resource) { this.resource = resource; return this; }
        public Builder action(String action) { this.action = action; return this; }
        public Builder denialReason(String denialReason) { this.denialReason = denialReason; return this; }
        public Builder deniedAt(Instant deniedAt) { this.deniedAt = deniedAt; return this; }
        public Builder correlationId(String correlationId) { this.correlationId = correlationId; return this; }
        public Builder sourceIp(String sourceIp) { this.sourceIp = sourceIp; return this; }
        public Builder userAgent(String userAgent) { this.userAgent = userAgent; return this; }

        public AccessDeniedEvent build() {
            return new AccessDeniedEvent(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccessDeniedEvent)) return false;
        AccessDeniedEvent that = (AccessDeniedEvent) o;
        return Objects.equals(eventId, that.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId);
    }

    @Override
    public String toString() {
        return "AccessDeniedEvent{" +
                "eventId='" + eventId + '\'' +
                ", tenantId='" + tenantId + '\'' +
                ", subjectId='" + subjectId + '\'' +
                ", resource='" + resource + '\'' +
                ", action='" + action + '\'' +
                ", denialReason='" + denialReason + '\'' +
                ", deniedAt=" + deniedAt +
                '}';
    }
}
