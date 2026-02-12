package com.gogidix.rapidassist.ai.tagging.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain model representing a TagCategory.
 * Defines categories for organizing tags.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagCategory {

    private UUID id;
    private String tenantId;
    private String name;
    private String description;
    private String icon;
    private Integer displayOrder;
    private CategoryStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private Long version;

    /**
     * Enum representing category status
     */
    public enum CategoryStatus {
        ACTIVE,
        INACTIVE,
        ARCHIVED
    }
}
