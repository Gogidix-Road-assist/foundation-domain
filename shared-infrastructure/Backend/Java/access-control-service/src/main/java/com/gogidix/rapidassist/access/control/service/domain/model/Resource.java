package com.gogidix.rapidassist.access.control.service.domain.model;

import java.time.Instant;
import java.util.Objects;

/**
 * Domain Model: Resource
 *
 * Represents a protected resource in the system that requires
 * access control (e.g., API endpoints, data entities, files).
 *
 * This is a DOMAIN entity with ZERO framework dependencies.
 */
public class Resource {

    private final String id;
    private final String tenantId;
    private final String resourceType; // API, DATA, FILE
    private final String resourcePath;
    private final String description;
    private final String owner;
    private final String resourceGroup;
    private final Instant createdAt;
    private boolean active;

    private Resource(Builder builder) {
        this.id = Objects.requireNonNull(builder.id, "id is required");
        this.tenantId = Objects.requireNonNull(builder.tenantId, "tenantId is required");
        this.resourceType = Objects.requireNonNull(builder.resourceType, "resourceType is required");
        this.resourcePath = Objects.requireNonNull(builder.resourcePath, "resourcePath is required");
        this.description = builder.description;
        this.owner = builder.owner;
        this.resourceGroup = builder.resourceGroup;
        this.createdAt = Objects.requireNonNullElse(builder.createdAt, Instant.now());
        this.active = builder.active;
    }

    /**
     * Check if this resource matches the given path pattern.
     * Supports wildcard matching.
     */
    public boolean matches(String requestedPath) {
        if (!active) {
            return false;
        }
        if (this.resourcePath.equals("*")) {
            return true;
        }
        if (this.resourcePath.endsWith("/*")) {
            String prefix = this.resourcePath.substring(0, this.resourcePath.length() - 2);
            return requestedPath.startsWith(prefix);
        }
        return this.resourcePath.equals(requestedPath);
    }

    /**
     * Check if this resource is owned by the given subject.
     */
    public boolean isOwnedBy(String subjectId) {
        return Objects.equals(this.owner, subjectId);
    }

    // Getters
    public String getId() { return id; }
    public String getTenantId() { return tenantId; }
    public String getResourceType() { return resourceType; }
    public String getResourcePath() { return resourcePath; }
    public String getDescription() { return description; }
    public String getOwner() { return owner; }
    public String getResourceGroup() { return resourceGroup; }
    public Instant getCreatedAt() { return createdAt; }
    public boolean isActive() { return active; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String tenantId;
        private String resourceType;
        private String resourcePath;
        private String description;
        private String owner;
        private String resourceGroup;
        private Instant createdAt;
        private boolean active = true;

        public Builder id(String id) { this.id = id; return this; }
        public Builder tenantId(String tenantId) { this.tenantId = tenantId; return this; }
        public Builder resourceType(String resourceType) { this.resourceType = resourceType; return this; }
        public Builder resourcePath(String resourcePath) { this.resourcePath = resourcePath; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder owner(String owner) { this.owner = owner; return this; }
        public Builder resourceGroup(String resourceGroup) { this.resourceGroup = resourceGroup; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder active(boolean active) { this.active = active; return this; }

        public Resource build() {
            return new Resource(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Resource)) return false;
        Resource that = (Resource) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Resource{" +
                "id='" + id + '\'' +
                ", tenantId='" + tenantId + '\'' +
                ", resourceType='" + resourceType + '\'' +
                ", resourcePath='" + resourcePath + '\'' +
                ", active=" + active +
                '}';
    }
}
