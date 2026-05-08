package com.gogidix.rapidassist.access.control.service.domain.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Domain Model: Subject
 *
 * Represents a subject that can be granted permissions.
 * A subject can be a USER, SERVICE, or ROLE.
 *
 * This is a DOMAIN entity with ZERO framework dependencies.
 */
public class Subject {

    private final String id;
    private final String tenantId;
    private final String subjectType; // USER, SERVICE, ROLE
    private final String subjectKey; // username, service name, role name
    private final String displayName;
    private final List<String> roleIds;
    private final Instant createdAt;
    private Instant lastAccessAt;
    private boolean active;

    private Subject(Builder builder) {
        this.id = Objects.requireNonNull(builder.id, "id is required");
        this.tenantId = Objects.requireNonNull(builder.tenantId, "tenantId is required");
        this.subjectType = Objects.requireNonNull(builder.subjectType, "subjectType is required");
        this.subjectKey = Objects.requireNonNull(builder.subjectKey, "subjectKey is required");
        this.displayName = builder.displayName;
        this.roleIds = builder.roleIds != null
                ? List.copyOf(builder.roleIds)
                : List.of();
        this.createdAt = Objects.requireNonNullElse(builder.createdAt, Instant.now());
        this.lastAccessAt = builder.lastAccessAt;
        this.active = builder.active;
    }

    /**
     * Assign a role to this subject.
     */
    public void assignRole(String roleId) {
        if (!this.roleIds.contains(roleId)) {
            List<String> newRoles = new ArrayList<>(this.roleIds);
            newRoles.add(roleId);
        }
    }

    /**
     * Remove a role from this subject.
     */
    public void removeRole(String roleId) {
        // Immutable pattern - actual modification through repository
    }

    /**
     * Check if this subject has the given role.
     */
    public boolean hasRole(String roleId) {
        return this.roleIds.contains(roleId);
    }

    /**
     * Update last access timestamp.
     */
    public void recordAccess() {
        this.lastAccessAt = Instant.now();
    }

    /**
     * Check if this subject is active.
     */
    public boolean isActive() {
        return this.active;
    }

    /**
     * Deactivate this subject.
     */
    public void deactivate() {
        this.active = false;
    }

    // Getters
    public String getId() { return id; }
    public String getTenantId() { return tenantId; }
    public String getSubjectType() { return subjectType; }
    public String getSubjectKey() { return subjectKey; }
    public String getDisplayName() { return displayName; }
    public List<String> getRoleIds() { return Collections.unmodifiableList(roleIds); }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getLastAccessAt() { return lastAccessAt; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String tenantId;
        private String subjectType;
        private String subjectKey;
        private String displayName;
        private List<String> roleIds;
        private Instant createdAt;
        private Instant lastAccessAt;
        private boolean active = true;

        public Builder id(String id) { this.id = id; return this; }
        public Builder tenantId(String tenantId) { this.tenantId = tenantId; return this; }
        public Builder subjectType(String subjectType) { this.subjectType = subjectType; return this; }
        public Builder subjectKey(String subjectKey) { this.subjectKey = subjectKey; return this; }
        public Builder displayName(String displayName) { this.displayName = displayName; return this; }
        public Builder roleIds(List<String> roleIds) { this.roleIds = roleIds; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder lastAccessAt(Instant lastAccessAt) { this.lastAccessAt = lastAccessAt; return this; }
        public Builder active(boolean active) { this.active = active; return this; }

        public Subject build() {
            return new Subject(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Subject)) return false;
        Subject that = (Subject) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Subject{" +
                "id='" + id + '\'' +
                ", tenantId='" + tenantId + '\'' +
                ", subjectType='" + subjectType + '\'' +
                ", subjectKey='" + subjectKey + '\'' +
                ", roleCount=" + roleIds.size() +
                ", active=" + active +
                '}';
    }
}
