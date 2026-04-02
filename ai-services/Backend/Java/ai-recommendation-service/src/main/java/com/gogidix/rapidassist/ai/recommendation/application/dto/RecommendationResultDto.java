package com.gogidix.rapidassist.ai.recommendation.application.dto;

import com.gogidix.rapidassist.ai.recommendation.domain.model.RecommendationResult;
import com.gogidix.rapidassist.ai.recommendation.domain.model.RecommendationStatus;
import com.gogidix.rapidassist.ai.recommendation.domain.model.RecommendationType;
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
 * DTO for RecommendationResult.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationResultDto {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private UUID requestId;
    private String userId;
    private RecommendationType recommendationType;
    private String itemType;
    private RecommendationStatus status;
    private List<RecommendedItemDto> items;
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
     * Nested DTO for recommended items
     */
    @Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecommendedItemDto {
        private String itemId;
        private String itemType;
        private Double score;
        private Double confidence;
        private String reason;
        private Map<String, Object> features;
        private Map<String, Object> metadata;
    }
}
