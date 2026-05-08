package com.gogidix.rapidassist.access.control.service.domain.event;

import java.time.Instant;
import java.util.Objects;

/**
 * Domain Event: PermissionRevokedEvent
 *
 * Published when a permission is revoked from a subject.
 * This event can be consumed by other services for audit,
 * cache invalidation, or synchronization purposes.
 *
 * This is a DOMAIN event with ZERO framework dependencies.
 */
public class PermissionRevokedEvent {

    private final String eventId;
    private final String tenantId;
    private final String permissionId;
    private final String subjectId;
    private final String subjectType;
    private final String resource;
    private final String action;
    private final String revokedBy;
    private final Instant revokedAt;
    private final String reason;
    private final String correlationId;

    private PermissionRevokedEvent(Builder builder) {
        this.eventId = Objects.requireNonNull(builder.eventId, "eventId is required");
        this.tenantId = Objects.requireNonNull(builder.tenantId, "tenantId is required");
        this.permissionId = Objects.requireNonNull(builder.permissionId, "permissionId is required");
        this.subjectId = Objects.requireNonNull(builder.subjectId, "subjectId is required");
        this.subjectType = Objects.requireNonNull(builder.subjectType, "subjectType is required");
        this.resource = Objects.requireNonNull(builder.resource, "resource is required");
        this.action = Objects.requireNonNull(builder.action, "action is required");
        this.revokedBy = builder.revokedBy;
        this.revokedAt = Objects.requireNonNullElse(builder.revokedAt, Instant.now());
        this.reason = builder.reason;
        this.correlationId = builder.correlationId;
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
        return "PermissionRevoked";
    }

    // Getters
    public String getEventId() { return eventId; }
    public String getTenantId() { return tenantId; }
    public String getPermissionId() { return permissionId; }
    public String getSubjectId() { return subjectId; }
    public String getSubjectType() { return subjectType; }
    public String getResource() { return resource; }
    public String getAction() { return action; }
    public String getRevokedBy() { return revokedBy; }
    public Instant getRevokedAt() { return revokedAt; }
    public String getReason() { return reason; }
    public String getCorrelationId() { return correlationId; }

    public static class Builder {
        private String eventId;
        private String tenantId;
        private String permissionId;
        private String subjectId;
        private String subjectType;
        private String resource;
        private String action;
        private String revokedBy;
        private Instant revokedAt;
        private String reason;
        private String correlationId;

        public Builder eventId(String eventId) { this.eventId = eventId; return this; }
        public Builder tenantId(String tenantId) { this.tenantId = tenantId; return this; }
        public Builder permissionId(String permissionId) { this.permissionId = permissionId; return this; }
        public Builder subjectId(String subjectId) { this.subjectId = subjectId; return this; }
        public Builder subjectType(String subjectType) { this.subjectType = subjectType; return this; }
        public Builder resource(String resource) { this.resource = resource; return this; }
        public Builder action(String action) { this.action = action; return this; }
        public Builder revokedBy(String revokedBy) { this.revokedBy = revokedBy; return this; }
        public Builder revokedAt(Instant revokedAt) { this.revokedAt = revokedAt; return this; }
        public Builder reason(String reason) { this.reason = reason; return this; }
        public Builder correlationId(String correlationId) { this.correlationId = correlationId; return this; }

        public PermissionRevokedEvent build() {
            return new PermissionRevokedEvent(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PermissionRevokedEvent)) return false;
        PermissionRevokedEvent that = (PermissionRevokedEvent) o;
        return Objects.equals(eventId, that.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId);
    }

    @Override
    public String toString() {
        return "PermissionRevokedEvent{" +
                "eventId='" + eventId + '\'' +
                ", tenantId='" + tenantId + '\'' +
                ", permissionId='" + permissionId + '\'' +
                ", subjectId='" + subjectId + '\'' +
                ", resource='" + resource + '\'' +
                ", action='" + action + '\'' +
                ", revokedAt=" + revokedAt +
                ", reason='" + reason + '\'' +
                '}';
    }
}
