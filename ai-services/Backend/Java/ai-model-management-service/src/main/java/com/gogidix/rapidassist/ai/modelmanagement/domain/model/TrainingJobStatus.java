package com.gogidix.rapidassist.ai.modelmanagement.domain.model;

/**
 * Enumeration representing the status of a training job.
 */
public enum TrainingJobStatus {
    PENDING,
    RUNNING,
    COMPLETED,
    FAILED,
    CANCELLED,
    TIMEOUT
}
