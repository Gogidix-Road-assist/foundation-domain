package com.gogidix.rapidassist.ai.personalization.infrastructure.persistence.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * MongoDB Document for UserSegment.
 * Maps to user_segment collection with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "user_segment")
public class UserSegmentEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed(unique = true)
    private String segmentCode;

    private String segmentName;
    private String description;

    // Segment definition
    @Indexed
    private String segmentType;
    private Map<String, Object> criteria;
    private List<String> includedUserIds;
    private List<String> excludedUserIds;

    // Size and scope
    private Integer size;
    private Integer maxCapacity;

    // Priority and display
    private Integer priority;
    private String color;
    private String icon;
    private Boolean isPublic;

    // Status
    @Indexed
    private String status;

    // Auto-update settings
    private Boolean autoUpdate;
    private String updateFrequency;
    private LocalDateTime lastCalculatedAt;
    private LocalDateTime nextCalculationAt;

    // Metadata
    private String category;
    private List<String> tags;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private Long version;
}
