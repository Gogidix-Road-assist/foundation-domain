package com.gogidix.rapidassist.ai.recommendation.infrastructure.persistence.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * MongoDB Document for RecommendationResult.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "recommendation_result")
public class RecommendationResultEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private UUID requestId;

    @Indexed
    private String userId;

    @Indexed
    private com.gogidix.rapidassist.ai.recommendation.domain.model.RecommendationType recommendationType;

    @Indexed
    private String itemType;

    @Indexed
    private com.gogidix.rapidassist.ai.recommendation.domain.model.RecommendationStatus status;

    private String items;
    private Integer totalResults;
    private Double confidenceScore;
    private Long processingTimeMs;
    private String algorithm;
    private String metadata;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    @Indexed
    private String cacheKey;
    private Boolean cached;
}
