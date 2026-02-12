package com.gogidix.rapidassist.access.control.service.infrastructure.persistence.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * MongoDB Document: ResourceDocument
 *
 * Document representation of Resource for MongoDB persistence.
 */
@Document(collection = "resources")
@CompoundIndex(name = "tenant_path_idx", def = "{'tenantId': 1, 'resourcePath': 1}", unique = true)
public class ResourceDocument {

    @Id
    private String id;

    @Indexed
    private String tenantId;

    @Indexed
    private String resourceType; // API, DATA, FILE

    @Indexed
    private String resourcePath;

    private String description;
    private String owner;
    private String resourceGroup;
    private Instant createdAt;
    private boolean active;

    public ResourceDocument() {
    }

    public ResourceDocument(String id, String tenantId, String resourceType, String resourcePath,
                           String description, String owner, String resourceGroup,
                           Instant createdAt, boolean active) {
        this.id = id;
        this.tenantId = tenantId;
        this.resourceType = resourceType;
        this.resourcePath = resourcePath;
        this.description = description;
        this.owner = owner;
        this.resourceGroup = resourceGroup;
        this.createdAt = createdAt;
        this.active = active;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    public String getResourceType() { return resourceType; }
    public void setResourceType(String resourceType) { this.resourceType = resourceType; }

    public String getResourcePath() { return resourcePath; }
    public void setResourcePath(String resourcePath) { this.resourcePath = resourcePath; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }

    public String getResourceGroup() { return resourceGroup; }
    public void setResourceGroup(String resourceGroup) { this.resourceGroup = resourceGroup; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
