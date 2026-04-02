package com.gogidix.rapidassist.ai.analytics.domain.model;

/**
 * Enum representing the status of a data query
 */
public enum QueryStatus {
    /**
     * Query is pending execution
     */
    PENDING,

    /**
     * Query is currently executing
     */
    EXECUTING,

    /**
     * Query executed successfully
     */
    COMPLETED,

    /**
     * Query execution failed
     */
    FAILED,

    /**
     * Query was cancelled
     */
    CANCELLED
}
