package com.gogidix.rapidassist.ai.recommendation.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Domain model representing a recommendation request.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationRequest {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private String userId;
    private RecommendationType recommendationType;
    private String itemType;
    private String contextType;
    private String contextId;
    private Integer limit;
    private Map<String, Object> filters;
    private Map<String, Object> parameters;
    private RecommendationStatus status;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
    private LocalDateTime expiresAt;
    private String metadata;

    /**
     * Business logic: Mark as processing
     */
    public void markAsProcessing() {
        this.status = RecommendationStatus.PROCESSING;
        this.processedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Mark as completed
     */
    public void markAsCompleted() {
        this.status = RecommendationStatus.COMPLETED;
        this.processedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Mark as failed
     */
    public void markAsFailed(String errorMessage) {
        this.status = RecommendationStatus.FAILED;
        this.errorMessage = errorMessage;
        this.processedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Check if expired
     */
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }

    /**
     * Business logic: Check if can be processed
     */
    public boolean canProcess() {
        return status == RecommendationStatus.PENDING && !isExpired();
    }
}
