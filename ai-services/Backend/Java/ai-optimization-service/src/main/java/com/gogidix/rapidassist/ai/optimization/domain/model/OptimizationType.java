package com.gogidix.rapidassist.ai.optimization.domain.model;

/**
 * Enumeration representing the type of optimization.
 */
public enum OptimizationType {
    /**
     * Hyperparameter optimization for ML models.
     */
    HYPERPARAMETER_TUNING,

    /**
     * Resource allocation optimization.
     */
    RESOURCE_ALLOCATION,

    /**
     * Performance optimization.
     */
    PERFORMANCE_TUNING,

    /**
     * Cost optimization.
     */
    COST_OPTIMIZATION,

    /**
     * Neural network architecture search.
     */
    NEURAL_ARCHITECTURE_SEARCH,

    /**
     * Feature selection optimization.
     */
    FEATURE_SELECTION,

    /**
     * Model compression/quantization.
     */
    MODEL_COMPRESSION,

    /**
     * Batch size optimization.
     */
    BATCH_OPTIMIZATION,

    /**
     * Custom optimization type.
     */
    CUSTOM
}
