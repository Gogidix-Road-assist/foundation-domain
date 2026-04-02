package com.gogidix.rapidassist.ai.recommendation.interfaces.rest.response;

import com.gogidix.rapidassist.ai.recommendation.application.dto.RecommendationResultDto.RecommendedItemDto;
import com.gogidix.rapidassist.ai.recommendation.domain.model.RecommendationStatus;
import com.gogidix.rapidassist.ai.recommendation.domain.model.RecommendationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST response for recommendation results.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationResponse {

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
    private Boolean cached;
}
