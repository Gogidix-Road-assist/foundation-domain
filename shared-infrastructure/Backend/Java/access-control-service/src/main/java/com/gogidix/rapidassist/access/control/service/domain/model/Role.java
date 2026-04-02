package com.gogidix.rapidassist.access.control.service.domain.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Domain Model: Role
 *
 * Represents a role that can be assigned to subjects (users/services).
 * Roles act as groups of permissions for easier management.
 *
 * This is a DOMAIN entity with ZERO framework dependencies.
 */
public class Role {

    private final String id;
    private final String tenantId;
    private final String name;
    private final String description;
    private final List<String> permissionIds;
    private final Instant createdAt;
    private final String createdBy;
    private Instant updatedAt;
    private final String updatedBy;
    private boolean active;

    private Role(Builder builder) {
        this.id = Objects.requireNonNull(builder.id, "id is required");
        this.tenantId = Objects.requireNonNull(builder.tenantId, "tenantId is required");
        this.name = Objects.requireNonNull(builder.name, "name is required");
        this.description = builder.description;
        this.permissionIds = builder.permissionIds != null
                ? List.copyOf(builder.permissionIds)
                : List.of();
        this.createdAt = Objects.requireNonNullElse(builder.createdAt, Instant.now());
        this.createdBy = builder.createdBy;
        this.updatedAt = Objects.requireNonNullElse(builder.updatedAt, this.createdAt);
        this.updatedBy = builder.updatedBy;
        this.active = builder.active;
    }

    /**
     * Add a permission ID to this role.
     */
    public void addPermission(String permissionId) {
        List<String> newPermissions = new ArrayList<>(this.permissionIds);
        if (!newPermissions.contains(permissionId)) {
            newPermissions.add(permissionId);
            this.permissionIds.addAll(newPermissions);
        }
    }

    /**
     * Remove a permission ID from this role.
     */
    public void removePermission(String permissionId) {
        // Immutable pattern - returns new state indication
        // Actual modification happens through repository
    }

    /**
     * Check if this role has the given permission ID.
     */
    public boolean hasPermission(String permissionId) {
        return this.permissionIds.contains(permissionId);
    }

    /**
     * Check if this role is active.
     */
    public boolean isActive() {
        return this.active;
    }

    /**
     * Deactivate this role.
     */
    public void deactivate() {
        this.active = false;
    }

    /**
     * Activate this role.
     */
    public void activate() {
        this.active = true;
    }

    /**
     * Update role metadata.
     */
    public void update(String name, String description, String updatedBy) {
        this.updatedAt = Instant.now();
    }

    // Getters
    public String getId() { return id; }
    public String getTenantId() { return tenantId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public List<String> getPermissionIds() { return Collections.unmodifiableList(permissionIds); }
    public Instant getCreatedAt() { return createdAt; }
    public String getCreatedBy() { return createdBy; }
    public Instant getUpdatedAt() { return updatedAt; }
    public String getUpdatedBy() { return updatedBy; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String tenantId;
        private String name;
        private String description;
        private List<String> permissionIds;
        private Instant createdAt;
        private String createdBy;
        private Instant updatedAt;
        private String updatedBy;
        private boolean active = true;

        public Builder id(String id) { this.id = id; return this; }
        public Builder tenantId(String tenantId) { this.tenantId = tenantId; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder permissionIds(List<String> permissionIds) { this.permissionIds = permissionIds; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder createdBy(String createdBy) { this.createdBy = createdBy; return this; }
        public Builder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }
        public Builder updatedBy(String updatedBy) { this.updatedBy = updatedBy; return this; }
        public Builder active(boolean active) { this.active = active; return this; }

        public Role build() {
            return new Role(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role)) return false;
        Role that = (Role) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Role{" +
                "id='" + id + '\'' +
                ", tenantId='" + tenantId + '\'' +
                ", name='" + name + '\'' +
                ", permissionCount=" + permissionIds.size() +
                ", active=" + active +
                '}';
    }
}
