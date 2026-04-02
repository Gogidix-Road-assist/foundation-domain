package com.gogidix.rapidassist.ai.sentiment.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Domain model representing an aspect-based sentiment
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Aspect {

    private String aspectName;
    private SentimentType sentiment;
    private Double confidence;
    private String opinionText;
    private Integer startPosition;
    private Integer endPosition;

    /**
     * Check if aspect sentiment is positive
     */
    public boolean isPositive() {
        return SentimentType.POSITIVE.equals(sentiment);
    }

    /**
     * Check if aspect sentiment is negative
     */
    public boolean isNegative() {
        return SentimentType.NEGATIVE.equals(sentiment);
    }

    /**
     * Check if aspect sentiment is neutral
     */
    public boolean isNeutral() {
        return SentimentType.NEUTRAL.equals(sentiment);
    }
}
