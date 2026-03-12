package com.gogidix.rapidassist.ai.translation.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain model representing translation quality metrics.
 * Used to assess the quality and confidence of translations.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TranslationQuality {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private UUID translationRequestId;

    /**
     * Quality score (0.0 to 1.0).
     */
    @Builder.Default
    private Double score = 0.0;

    /**
     * Confidence level (LOW, MEDIUM, HIGH).
     */
    private ConfidenceLevel confidence;

    /**
     * Number of errors detected.
     */
    @Builder.Default
    private Integer errorCount = 0;

    /**
     * Number of warnings.
     */
    @Builder.Default
    private Integer warningCount = 0;

    /**
     * Fluency score (0.0 to 1.0).
     */
    private Double fluencyScore;

    /**
     * Accuracy score (0.0 to 1.0).
     */
    private Double accuracyScore;

    /**
     * Consistency score (0.0 to 1.0).
     */
    private Double consistencyScore;

    /**
     * Additional quality metrics.
     */
    private java.util.Map<String, Object> metrics;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Confidence levels for translation quality.
     */
    public enum ConfidenceLevel {
        LOW,
        MEDIUM,
        HIGH
    }

    /**
     * Business logic: Calculate overall quality score.
     */
    public Double calculateOverallScore() {
        if (fluencyScore == null || accuracyScore == null || consistencyScore == null) {
            return score;
        }
        return (fluencyScore * 0.3) + (accuracyScore * 0.5) + (consistencyScore * 0.2);
    }

    /**
     * Business logic: Determine if quality is acceptable.
     */
    public boolean isAcceptable() {
        Double overallScore = calculateOverallScore();
        return overallScore >= 0.7 && errorCount == 0;
    }

    /**
     * Business logic: Get confidence level from score.
     */
    public static ConfidenceLevel getConfidenceFromScore(Double score) {
        if (score == null) {
            return ConfidenceLevel.LOW;
        }
        if (score >= 0.8) {
            return ConfidenceLevel.HIGH;
        } else if (score >= 0.5) {
            return ConfidenceLevel.MEDIUM;
        } else {
            return ConfidenceLevel.LOW;
        }
    }

    /**
     * Business logic: Check if review is needed.
     */
    public boolean needsHumanReview() {
        return errorCount > 0 || warningCount > 3 || calculateOverallScore() < 0.7;
    }

    /**
     * Business logic: Add error.
     */
    public void addError() {
        this.errorCount++;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Add warning.
     */
    public void addWarning() {
        this.warningCount++;
        this.updatedAt = LocalDateTime.now();
    }
}
