package com.gogidix.rapidassist.ai.search.optimization.infrastructure.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * MongoDB Document for SearchAnalytics.
 * Maps to search_analytics collection with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "search_analytics")
public class SearchAnalyticsEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private UUID queryId;

    @Indexed
    private String analyticsType;

    private Long totalQueries;
    private Long uniqueQueries;
    private Double averageRelevanceScore;
    private Long clickThroughCount;
    private Double clickThroughRate;

    private Map<String, Object> metrics;
    private Map<String, Object> metadata;

    @Indexed
    private String status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private Long version;
}
