package com.gogidix.rapidassist.access.control.service.domain.model;

import java.time.Instant;
import java.util.Objects;

/**
 * Domain Model: Permission
 *
 * Represents a granular permission that grants a subject the ability to perform
 * a specific action on a resource within a tenant context.
 *
 * This is a DOMAIN entity with ZERO framework dependencies.
 * All business rules are encapsulated within this class.
 */
public class Permission {

    private final String id;
    private final String tenantId;
    private final String subjectId;
    private final String subjectType; // USER, SERVICE, ROLE
    private final String resource;
    private final String action;
    private final String effect; // ALLOW, DENY
    private final Instant grantedAt;
    private final String grantedBy;
    private final Instant validUntil;
    private final String condition;
    private boolean active;

    private Permission(Builder builder) {
        this.id = Objects.requireNonNull(builder.id, "id is required");
        this.tenantId = Objects.requireNonNull(builder.tenantId, "tenantId is required");
        this.subjectId = Objects.requireNonNull(builder.subjectId, "subjectId is required");
        this.subjectType = Objects.requireNonNull(builder.subjectType, "subjectType is required");
        this.resource = Objects.requireNonNull(builder.resource, "resource is required");
        this.action = Objects.requireNonNull(builder.action, "action is required");
        this.effect = Objects.requireNonNullElse(builder.effect, "ALLOW");
        this.grantedAt = Objects.requireNonNullElse(builder.grantedAt, Instant.now());
        this.grantedBy = builder.grantedBy;
        this.validUntil = builder.validUntil;
        this.condition = builder.condition;
        this.active = builder.active;
    }

    /**
     * Check if this permission is currently valid.
     * A permission is valid if it is active and not expired.
     */
    public boolean isValid() {
        if (!active) {
            return false;
        }
        if (validUntil != null && Instant.now().isAfter(validUntil)) {
            return false;
        }
        return true;
    }

    /**
     * Check if this permission matches the given criteria.
     */
    public boolean matches(String subjectId, String resource, String action) {
        if (!isValid()) {
            return false;
        }
        if (!this.subjectId.equals(subjectId)) {
            return false;
        }
        if (!this.resource.equals(resource) && !this.resource.equals("*")) {
            return false;
        }
        if (!this.action.equals(action) && !this.action.equals("*")) {
            return false;
        }
        return true;
    }

    /**
     * Revoke this permission.
     */
    public void revoke() {
        this.active = false;
    }

    /**
     * Check if this permission is an ALLOW effect.
     */
    public boolean isAllowed() {
        return "ALLOW".equals(this.effect);
    }

    // Getters
    public String getId() { return id; }
    public String getTenantId() { return tenantId; }
    public String getSubjectId() { return subjectId; }
    public String getSubjectType() { return subjectType; }
    public String getResource() { return resource; }
    public String getAction() { return action; }
    public String getEffect() { return effect; }
    public Instant getGrantedAt() { return grantedAt; }
    public String getGrantedBy() { return grantedBy; }
    public Instant getValidUntil() { return validUntil; }
    public String getCondition() { return condition; }
    public boolean isActive() { return active; }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(Permission existing) {
        return new Builder()
                .id(existing.id)
                .tenantId(existing.tenantId)
                .subjectId(existing.subjectId)
                .subjectType(existing.subjectType)
                .resource(existing.resource)
                .action(existing.action)
                .effect(existing.effect)
                .grantedAt(existing.grantedAt)
                .grantedBy(existing.grantedBy)
                .validUntil(existing.validUntil)
                .condition(existing.condition)
                .active(existing.active);
    }

    public static class Builder {
        private String id;
        private String tenantId;
        private String subjectId;
        private String subjectType;
        private String resource;
        private String action;
        private String effect;
        private Instant grantedAt;
        private String grantedBy;
        private Instant validUntil;
        private String condition;
        private boolean active = true;

        public Builder id(String id) { this.id = id; return this; }
        public Builder tenantId(String tenantId) { this.tenantId = tenantId; return this; }
        public Builder subjectId(String subjectId) { this.subjectId = subjectId; return this; }
        public Builder subjectType(String subjectType) { this.subjectType = subjectType; return this; }
        public Builder resource(String resource) { this.resource = resource; return this; }
        public Builder action(String action) { this.action = action; return this; }
        public Builder effect(String effect) { this.effect = effect; return this; }
        public Builder grantedAt(Instant grantedAt) { this.grantedAt = grantedAt; return this; }
        public Builder grantedBy(String grantedBy) { this.grantedBy = grantedBy; return this; }
        public Builder validUntil(Instant validUntil) { this.validUntil = validUntil; return this; }
        public Builder condition(String condition) { this.condition = condition; return this; }
        public Builder active(boolean active) { this.active = active; return this; }

        public Permission build() {
            return new Permission(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Permission)) return false;
        Permission that = (Permission) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Permission{" +
                "id='" + id + '\'' +
                ", tenantId='" + tenantId + '\'' +
                ", subjectId='" + subjectId + '\'' +
                ", subjectType='" + subjectType + '\'' +
                ", resource='" + resource + '\'' +
                ", action='" + action + '\'' +
                ", effect='" + effect + '\'' +
                ", active=" + active +
                '}';
    }
}
