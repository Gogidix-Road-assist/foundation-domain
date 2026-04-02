package com.gogidix.rapidassist.ai.predictive.analytics.domain.model;

/**
 * Enumeration of predictive model types supported by the platform.
 */
public enum ModelType {
    /**
     * Time series forecasting models
     */
    TIME_SERIES_FORECAST,

    /**
     * Regression models for continuous value prediction
     */
    REGRESSION,

    /**
     * Classification models for categorical prediction
     */
    CLASSIFICATION,

    /**
     * Clustering models for unsupervised learning
     */
    CLUSTERING,

    /**
     * Anomaly detection models
     */
    ANOMALY_DETECTION,

    /**
     * Recommendation models
     */
    RECOMMENDATION,

    /**
     * Deep learning models
     */
    DEEP_LEARNING,

    /**
     * Ensemble models combining multiple algorithms
     */
    ENSEMBLE
}
