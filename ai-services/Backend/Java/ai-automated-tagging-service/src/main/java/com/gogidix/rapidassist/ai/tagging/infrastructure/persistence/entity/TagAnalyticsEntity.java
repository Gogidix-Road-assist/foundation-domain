package com.gogidix.rapidassist.ai.tagging.infrastructure.persistence.entity;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * MongoDB Document for TagAnalytics.
 * Maps to tag_analytics collection with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "tag_analytics")
public class TagAnalyticsEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private UUID tagId;

    private String tagName;

    private String category;

    private Long usageCount;

    private Double avgConfidenceScore;

    private Long autoAppliedCount;

    private Long manuallyAppliedCount;

    private Long verifiedCount;

    private Long rejectedCount;

    @Indexed
    private String period;

    private LocalDateTime periodStart;

    private LocalDateTime periodEnd;

    private java.util.Map<String, Object> metadata;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
