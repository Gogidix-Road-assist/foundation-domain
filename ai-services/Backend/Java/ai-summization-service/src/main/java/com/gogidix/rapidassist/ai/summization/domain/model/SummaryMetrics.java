package com.gogidix.rapidassist.ai.summization.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain model representing summary quality metrics.
 * Contains measurements and quality indicators for generated summaries.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SummaryMetrics {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private UUID documentSummaryId;
    private Double compressionRatio;
    private Integer originalLength;
    private Integer summaryLength;
    private Double relevanceScore;
    private Double coherenceScore;
    private Double fluencyScore;
    private Double overallQualityScore;
    private Integer keyPointsCount;
    private Integer sentencesCount;
    private Integer wordsCount;
    private Long processingTimeMs;
    private Double confidenceScore;
    private String language;
    private Boolean passedQualityThreshold;
    private java.util.Map<String, Double> additionalScores;
    private LocalDateTime createdAt;

    /**
     * Business logic: Calculate actual compression ratio
     */
    public double calculateCompressionRatio() {
        if (originalLength == null || originalLength == 0 || summaryLength == null) {
            return 0.0;
        }
        return (double) summaryLength / originalLength;
    }

    /**
     * Business logic: Check if quality threshold was passed
     */
    public boolean hasPassedQualityThreshold(double threshold) {
        return Boolean.TRUE.equals(this.passedQualityThreshold)
                || (overallQualityScore != null && overallQualityScore >= threshold);
    }

    /**
     * Business logic: Check if metrics are acceptable
     */
    public boolean isAcceptable() {
        return overallQualityScore != null && overallQualityScore >= 0.6
                && compressionRatio != null && compressionRatio > 0.0 && compressionRatio <= 1.0;
    }

    /**
     * Business logic: Get quality rating
     */
    public String getQualityRating() {
        if (overallQualityScore == null) {
            return "UNKNOWN";
        }
        if (overallQualityScore >= 0.9) {
            return "EXCELLENT";
        } else if (overallQualityScore >= 0.75) {
            return "GOOD";
        } else if (overallQualityScore >= 0.6) {
            return "ACCEPTABLE";
        } else {
            return "POOR";
        }
    }

    /**
     * Business logic: Check if summary is too long
     */
    public boolean isSummaryTooLong(double maxCompressionRatio) {
        return compressionRatio != null && compressionRatio > maxCompressionRatio;
    }

    /**
     * Business logic: Check if summary is too short
     */
    public boolean isSummaryTooShort(double minCompressionRatio) {
        return compressionRatio != null && compressionRatio < minCompressionRatio;
    }

    /**
     * Business logic: Calculate words per sentence average
     */
    public double getAverageWordsPerSentence() {
        if (sentencesCount == null || sentencesCount == 0 || wordsCount == null) {
            return 0.0;
        }
        return (double) wordsCount / sentencesCount;
    }
}
