package com.gogidix.rapidassist.ai.optimization.domain.model;

/**
 * Enumeration representing the status of an optimization job.
 */
public enum OptimizationStatus {
    /**
     * Job is pending execution.
     */
    PENDING,

    /**
     * Job is currently running.
     */
    RUNNING,

    /**
     * Job completed successfully.
     */
    COMPLETED,

    /**
     * Job failed during execution.
     */
    FAILED,

    /**
     * Job was cancelled before completion.
     */
    CANCELLED,

    /**
     * Job is paused.
     */
    PAUSED
}
