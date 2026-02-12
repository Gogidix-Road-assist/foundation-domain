package com.gogidix.rapidassist.ai.tagging.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain model representing a Tag.
 * Pure domain model without persistence annotations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tag {

    private UUID id;
    private String tenantId;
    private String name;
    private String description;
    private TagCategory category;
    private String color;
    private TagStatus status;
    private Integer usageCount;
    private Long version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    /**
     * Enum representing tag status
     */
    public enum TagStatus {
        ACTIVE,
        INACTIVE,
        ARCHIVED
    }

    /**
     * Increments the usage count of this tag
     */
    public void incrementUsage() {
        if (this.usageCount == null) {
            this.usageCount = 0;
        }
        this.usageCount++;
    }

    /**
     * Decrements the usage count of this tag
     */
    public void decrementUsage() {
        if (this.usageCount == null) {
            this.usageCount = 0;
        }
        this.usageCount = Math.max(0, this.usageCount - 1);
    }

    /**
     * Activates this tag
     */
    public void activate() {
        this.status = TagStatus.ACTIVE;
    }

    /**
     * Deactivates this tag
     */
    public void deactivate() {
        this.status = TagStatus.INACTIVE;
    }

    /**
     * Archives this tag
     */
    public void archive() {
        this.status = TagStatus.ARCHIVED;
    }

    /**
     * Checks if this tag is active
     */
    public boolean isActive() {
        return this.status == TagStatus.ACTIVE;
    }
}
