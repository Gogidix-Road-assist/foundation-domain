package com.gogidix.rapidassist.orchestration.fleetorganization.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Organization entity representing hierarchical organizational structure.
 * Supports multi-level hierarchy: ROOT, DIVISION, DEPARTMENT, TEAM, UNIT
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "organizations")
@CompoundIndex(name = "tenant_parent_idx", def = "{'tenantId': 1, 'parentId': 1}")
public class Organization {

    @Id
    private String id;

    @Indexed(unique = true)
    private String organizationId;

    @Indexed
    private String name;

    private String description;

    @Indexed
    private String parentId;

    @Indexed
    private OrganizationType organizationType;

    @Indexed
    private Integer level;

    @Indexed
    private String path; // e.g., "/org-root-001/org-div-001"

    @Indexed
    private String managerId;

    private String contactEmail;
    private String contactPhone;

    private Location location;

    @Indexed
    @Builder.Default
    private Boolean isActive = true;

    @Builder.Default
    private Map<String, Object> metadata = Map.of();

    @Indexed
    private String tenantId;

    @CreatedDate
    @Indexed
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Indexed
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    /**
     * Domain logic: Check if this is a root organization
     */
    public boolean isRoot() {
        return organizationType == OrganizationType.ROOT;
    }

    /**
     * Domain logic: Check if organization can have children
     */
    public boolean canHaveChildren() {
        return organizationType != OrganizationType.UNIT;
    }

    /**
     * Domain logic: Validate organization structure
     */
    public void validate() {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Organization name cannot be blank");
        }
        if (organizationType == null) {
            throw new IllegalArgumentException("Organization type is required");
        }
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalArgumentException("Tenant ID is required");
        }
        if (level == null || level < 0) {
            throw new IllegalArgumentException("Level must be non-negative");
        }
    }

    /**
     * Domain logic: Soft delete organization
     */
    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
        this.isActive = false;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Location {
        private Double latitude;
        private Double longitude;
        private String address;
    }

    public enum OrganizationType {
        ROOT,
        DIVISION,
        DEPARTMENT,
        TEAM,
        UNIT
    }
}
