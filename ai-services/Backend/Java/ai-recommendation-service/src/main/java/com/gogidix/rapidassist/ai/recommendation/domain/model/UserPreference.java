package com.gogidix.rapidassist.ai.recommendation.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain model representing user preferences for recommendation personalization.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPreference {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private String userId;
    private String itemType;
    private String itemId;
    private String preferenceKey;
    private String preferenceValue;
    private Double preferenceScore;
    private Integer interactionCount;
    private LocalDateTime lastInteractionAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String metadata;

    /**
     * Business logic: Update preference score
     */
    public void updateScore(Double scoreDelta) {
        this.preferenceScore = (this.preferenceScore != null ? this.preferenceScore : 0.0) + scoreDelta;
        this.interactionCount = (this.interactionCount != null ? this.interactionCount : 0) + 1;
        this.lastInteractionAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Check if preference is stale
     */
    public boolean isStale(int staleThresholdDays) {
        if (lastInteractionAt == null) {
            return true;
        }
        return lastInteractionAt.isBefore(LocalDateTime.now().minusDays(staleThresholdDays));
    }

    /**
     * Business logic: Get normalized score
     */
    public Double getNormalizedScore() {
        if (preferenceScore == null) {
            return 0.0;
        }
        return Math.min(1.0, Math.max(0.0, preferenceScore));
    }
}
