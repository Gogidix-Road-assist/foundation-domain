package com.gogidix.rapidassist.ai.predictive.analytics.domain.model;

/**
 * Enumeration of predictive model lifecycle statuses.
 */
public enum ModelStatus {
    /**
     * Model is being created and configured
     */
    DRAFT,

    /**
     * Model is undergoing training
     */
    TRAINING,

    /**
     * Model training completed successfully
     */
    TRAINED,

    /**
     * Model is being validated
     */
    VALIDATING,

    /**
     * Model validated and ready for predictions
     */
    ACTIVE,

    /**
     * Model is temporarily disabled
     */
    PAUSED,

    /**
     * Model is being retrained
     */
    RETRAINING,

    /**
     * Model is deprecated
     */
    DEPRECATED,

    /**
     * Model training or validation failed
     */
    FAILED
}
