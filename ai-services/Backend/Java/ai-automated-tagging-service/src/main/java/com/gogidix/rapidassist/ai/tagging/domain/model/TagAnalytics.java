package com.gogidix.rapidassist.ai.tagging.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain model representing TagAnalytics.
 * Analytics data for tag usage and performance.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagAnalytics {

    private UUID id;
    private String tenantId;
    private UUID tagId;
    private String tagName;
    private TagCategory category;
    private Long usageCount;
    private Double avgConfidenceScore;
    private Long autoAppliedCount;
    private Long manuallyAppliedCount;
    private Long verifiedCount;
    private Long rejectedCount;
    private String period;
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
    private java.util.Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
