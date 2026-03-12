package com.gogidix.rapidassist.ai.recommendation.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain model representing similarity between items for recommendations.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemSimilarity {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private String itemType;
    private String item1Id;
    private String item2Id;
    private Double similarityScore;
    private String similarityAlgorithm;
    private Integer coOccurrenceCount;
    private LocalDateTime lastCalculatedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String metadata;

    /**
     * Business logic: Update similarity score
     */
    public void updateScore(Double newScore, String algorithm) {
        this.similarityScore = newScore;
        this.similarityAlgorithm = algorithm;
        this.lastCalculatedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Increment co-occurrence count
     */
    public void incrementCoOccurrence() {
        this.coOccurrenceCount = (this.coOccurrenceCount != null ? this.coOccurrenceCount : 0) + 1;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Check if similarity needs recalculation
     */
    public boolean needsRecalculation(int recalcThresholdDays) {
        if (lastCalculatedAt == null) {
            return true;
        }
        return lastCalculatedAt.isBefore(LocalDateTime.now().minusDays(recalcThresholdDays));
    }

    /**
     * Business logic: Check if similarity is significant
     */
    public boolean isSignificant(Double threshold) {
        return similarityScore != null && similarityScore >= threshold;
    }
}
