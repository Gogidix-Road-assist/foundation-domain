package com.gogidix.rapidassist.ai.search.optimization.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Domain model representing Search Analytics.
 * Pure domain model without MongoDB annotations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchAnalytics {

    private UUID id;
    private String tenantId;
    private UUID queryId;
    private String analyticsType;
    private Long totalQueries;
    private Long uniqueQueries;
    private Double averageRelevanceScore;
    private Long clickThroughCount;
    private Double clickThroughRate;
    private Map<String, Object> metrics;
    private Map<String, Object> metadata;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private Long version;
}
