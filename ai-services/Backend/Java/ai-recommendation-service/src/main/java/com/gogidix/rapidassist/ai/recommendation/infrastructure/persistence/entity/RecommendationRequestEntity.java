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
 * MongoDB Document for RecommendationRequest.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "recommendation_request")
public class RecommendationRequestEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private String userId;

    @Indexed
    private com.gogidix.rapidassist.ai.recommendation.domain.model.RecommendationType recommendationType;

    @Indexed
    private String itemType;

    private String contextType;
    private String contextId;
    private Integer limit;
    private String filters;
    private String parameters;
    @Indexed
    private com.gogidix.rapidassist.ai.recommendation.domain.model.RecommendationStatus status;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
    private LocalDateTime expiresAt;
    private String metadata;
}
