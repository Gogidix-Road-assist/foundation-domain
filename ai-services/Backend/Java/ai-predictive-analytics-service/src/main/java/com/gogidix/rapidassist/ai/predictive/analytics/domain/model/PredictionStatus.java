package com.gogidix.rapidassist.ai.predictive.analytics.domain.model;

/**
 * Enumeration of prediction execution statuses.
 */
public enum PredictionStatus {
    /**
     * Prediction is queued for execution
     */
    PENDING,

    /**
     * Prediction is currently being processed
     */
    PROCESSING,

    /**
     * Prediction completed successfully
     */
    COMPLETED,

    /**
     * Prediction failed
     */
    FAILED,

    /**
     * Prediction was cancelled
     */
    CANCELLED,

    /**
     * Prediction result is cached
     */
    CACHED
}
