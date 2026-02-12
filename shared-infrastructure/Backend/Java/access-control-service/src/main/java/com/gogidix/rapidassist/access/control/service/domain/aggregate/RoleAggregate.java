package com.gogidix.rapidassist.access.control.service.domain.aggregate;

import com.gogidix.rapidassist.access.control.service.domain.model.Permission;
import com.gogidix.rapidassist.access.control.service.domain.model.Role;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Domain Aggregate: RoleAggregate
 *
 * A DDD Aggregate that manages Role and its associated Permissions.
 * This is the consistency boundary for role-related operations.
 *
 * This is a DOMAIN aggregate with ZERO framework dependencies.
 * All business rules for role management are encapsulated here.
 */
public class RoleAggregate {

    private final Role role;
    private final List<Permission> permissions;

    private RoleAggregate(Builder builder) {
        this.role = Objects.requireNonNull(builder.role, "role is required");
        this.permissions = builder.permissions != null
                ? List.copyOf(builder.permissions)
                : List.of();
    }

    /**
     * Grant a permission to this role.
     *
     * Business Rules:
     * - Role must be active
     * - Permission must be valid
     * - Permission must belong to the same tenant
     * - Duplicate permissions are ignored
     */
    public void grantPermission(Permission permission) {
        if (!role.isActive()) {
            throw new IllegalStateException("Cannot grant permission to inactive role: " + role.getName());
        }

        if (!permission.isValid()) {
            throw new IllegalArgumentException("Cannot grant invalid permission: " + permission.getId());
        }

        if (!permission.getTenantId().equals(role.getTenantId())) {
            throw new IllegalArgumentException(
                "Permission tenant mismatch. Role tenant: " + role.getTenantId() +
                ", Permission tenant: " + permission.getTenantId()
            );
        }

        if (this.role.hasPermission(permission.getId())) {
            return; // Already has permission, ignore
        }

        // In a real implementation, this would trigger an event
        // and the repository would handle the persistence
    }

    /**
     * Revoke a permission from this role.
     *
     * Business Rules:
     * - Role must be active
     * - Permission must be currently granted
     */
    public void revokePermission(String permissionId) {
        if (!role.isActive()) {
            throw new IllegalStateException("Cannot revoke permission from inactive role: " + role.getName());
        }

        if (!this.role.hasPermission(permissionId)) {
            throw new IllegalArgumentException("Role does not have permission: " + permissionId);
        }

        // In a real implementation, this would trigger an event
        // and the repository would handle the persistence
    }

    /**
     * Check if this role has a permission matching the given criteria.
     */
    public boolean hasPermissionMatching(String resource, String action) {
        return permissions.stream()
                .filter(Permission::isValid)
                .anyMatch(p -> p.matches(role.getId(), resource, action));
    }

    /**
     * Get all valid permissions for this role.
     */
    public List<Permission> getValidPermissions() {
        return permissions.stream()
                .filter(Permission::isValid)
                .toList();
    }

    /**
     * Deactivate this role and all its permissions.
     */
    public void deactivate() {
        this.role.deactivate();
    }

    /**
     * Activate this role.
     */
    public void activate() {
        this.role.activate();
    }

    /**
     * Update role metadata.
     */
    public void updateMetadata(String name, String description, String updatedBy) {
        this.role.update(name, description, updatedBy);
    }

    // Getters
    public Role getRole() { return role; }
    public List<Permission> getPermissions() { return Collections.unmodifiableList(permissions); }
    public String getId() { return role.getId(); }
    public String getTenantId() { return role.getTenantId(); }
    public String getName() { return role.getName(); }
    public boolean isActive() { return role.isActive(); }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Role role;
        private List<Permission> permissions;

        public Builder role(Role role) { this.role = role; return this; }
        public Builder permissions(List<Permission> permissions) { this.permissions = permissions; return this; }

        public RoleAggregate build() {
            return new RoleAggregate(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RoleAggregate)) return false;
        RoleAggregate that = (RoleAggregate) o;
        return Objects.equals(role.getId(), that.role.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(role.getId());
    }

    @Override
    public String toString() {
        return "RoleAggregate{" +
                "role=" + role +
                ", permissionCount=" + permissions.size() +
                '}';
    }
}
