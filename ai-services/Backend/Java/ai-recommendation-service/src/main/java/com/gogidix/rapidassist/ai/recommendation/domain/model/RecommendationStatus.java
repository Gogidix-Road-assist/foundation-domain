package com.gogidix.rapidassist.ai.recommendation.domain.model;

/**
 * Enumeration of recommendation request and result statuses.
 */
public enum RecommendationStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED,
    EXPIRED,
    CACHED
}
