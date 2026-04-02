package com.gogidix.rapidassist.ai.contentanalysis.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Domain model representing sentiment analysis results.
 * Contains emotional tone and sentiment metrics for content.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SentimentAnalysis {

    private Double sentimentScore;
    private SentimentType sentimentType;
    private Double confidence;
    private Double positivityScore;
    private Double negativityScore;
    private Double neutralityScore;

    private String dominantEmotion;
    private Map<String, Double> emotionBreakdown;
    private Map<String, Double> sentimentKeywords;

    private Integer positiveWordCount;
    private Integer negativeWordCount;
    private Integer neutralWordCount;

    /**
     * Business logic: Determine if sentiment is positive
     */
    public boolean isPositive() {
        return SentimentType.POSITIVE.equals(this.sentimentType);
    }

    /**
     * Business logic: Determine if sentiment is negative
     */
    public boolean isNegative() {
        return SentimentType.NEGATIVE.equals(this.sentimentType);
    }

    /**
     * Business logic: Determine if sentiment is neutral
     */
    public boolean isNeutral() {
        return SentimentType.NEUTRAL.equals(this.sentimentType);
    }

    /**
     * Business logic: Check if confidence is above threshold
     */
    public boolean hasHighConfidence(Double threshold) {
        return this.confidence != null && this.confidence >= threshold;
    }

    /**
     * Business logic: Get sentiment description
     */
    public String getSentimentDescription() {
        if (this.sentimentType == null) {
            return "Unknown";
        }

        return switch (this.sentimentType) {
            case POSITIVE -> "This content conveys a positive sentiment";
            case NEGATIVE -> "This content conveys a negative sentiment";
            case NEUTRAL -> "This content conveys a neutral sentiment";
            case MIXED -> "This content conveys mixed emotions";
        };
    }

    /**
     * Business logic: Get emotion intensity
     */
    public String getEmotionIntensity() {
        if (this.sentimentScore == null) {
            return "Unknown";
        }

        double absScore = Math.abs(this.sentimentScore);
        if (absScore >= 0.8) return "Very Strong";
        if (absScore >= 0.6) return "Strong";
        if (absScore >= 0.4) return "Moderate";
        if (absScore >= 0.2) return "Mild";
        return "Weak";
    }

    /**
     * Enum for sentiment types
     */
    public enum SentimentType {
        POSITIVE,
        NEGATIVE,
        NEUTRAL,
        MIXED
    }
}
