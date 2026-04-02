package com.gogidix.rapidassist.ai.summization.domain.model;

/**
 * Enumeration representing the status of a summarization task.
 */
public enum TaskStatus {
    /**
     * Task is pending processing
     */
    PENDING,

    /**
     * Task is currently being processed
     */
    IN_PROGRESS,

    /**
     * Task completed successfully
     */
    COMPLETED,

    /**
     * Task failed
     */
    FAILED,

    /**
     * Task was cancelled
     */
    CANCELLED
}
