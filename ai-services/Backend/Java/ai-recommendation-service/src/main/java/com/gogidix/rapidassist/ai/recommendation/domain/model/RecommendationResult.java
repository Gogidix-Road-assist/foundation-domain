package com.gogidix.rapidassist.ai.recommendation.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Domain model representing recommendation results.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationResult {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private UUID requestId;
    private String userId;
    private RecommendationType recommendationType;
    private String itemType;
    private RecommendationStatus status;
    private List<RecommendedItem> items;
    private Integer totalResults;
    private Double confidenceScore;
    private Long processingTimeMs;
    private String algorithm;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private String cacheKey;
    private Boolean cached;

    /**
     * Nested class for recommended items
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class RecommendedItem {
        private String itemId;
        private String itemType;
        private Double score;
        private Double confidence;
        private String reason;
        private Map<String, Object> features;
        private Map<String, Object> metadata;
    }

    /**
     * Business logic: Calculate confidence score
     */
    public void calculateConfidenceScore() {
        if (items == null || items.isEmpty()) {
            this.confidenceScore = 0.0;
            return;
        }

        double totalScore = items.stream()
                .mapToDouble(item -> item.getScore() != null ? item.getScore() : 0.0)
                .average()
                .orElse(0.0);

        this.confidenceScore = totalScore;
    }

    /**
     * Business logic: Check if expired
     */
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }

    /**
     * Business logic: Mark as cached
     */
    public void markAsCached(String cacheKey) {
        this.cached = true;
        this.cacheKey = cacheKey;
        this.status = RecommendationStatus.CACHED;
    }

    /**
     * Business logic: Get top N items
     */
    public List<RecommendedItem> getTopItems(int n) {
        if (items == null || items.isEmpty()) {
            return List.of();
        }
        return items.stream()
                .limit(n)
                .toList();
    }
}
