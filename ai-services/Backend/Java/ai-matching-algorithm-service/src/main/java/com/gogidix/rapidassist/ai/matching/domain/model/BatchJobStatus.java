package com.gogidix.rapidassist.ai.matching.domain.model;

/**
 * Enum representing the status of a batch matching job.
 */
public enum BatchJobStatus {
    PENDING,
    RUNNING,
    PAUSED,
    COMPLETED,
    FAILED,
    CANCELLED,
    PARTIALLY_COMPLETED
}
