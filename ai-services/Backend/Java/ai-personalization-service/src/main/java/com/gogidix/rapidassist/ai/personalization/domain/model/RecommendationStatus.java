package com.gogidix.rapidassist.ai.personalization.domain.model;

/**
 * Enumeration representing the status of a recommendation.
 */
public enum RecommendationStatus {
    /**
     * Recommendation is pending review
     */
    PENDING,

    /**
     * Recommendation is approved and ready to be shown
     */
    APPROVED,

    /**
     * Recommendation has been shown to user
     */
    SHOWN,

    /**
     * Recommendation was clicked by user
     */
    CLICKED,

    /**
     * Recommendation resulted in conversion
     */
    CONVERTED,

    /**
     * Recommendation was dismissed by user
     */
    DISMISSED,

    /**
     * Recommendation was rejected
     */
    REJECTED,

    /**
     * Recommendation has expired
     */
    EXPIRED
}
