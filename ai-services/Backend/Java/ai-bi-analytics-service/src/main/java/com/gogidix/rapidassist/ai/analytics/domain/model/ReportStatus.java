package com.gogidix.rapidassist.ai.analytics.domain.model;

/**
 * Enum representing the status of an analytics report
 */
public enum ReportStatus {
    /**
     * Report is pending generation
     */
    PENDING,

    /**
     * Report is currently being generated
     */
    GENERATING,

    /**
     * Report has been generated successfully
     */
    COMPLETED,

    /**
     * Report generation failed
     */
    FAILED,

    /**
     * Report is scheduled for future generation
     */
    SCHEDULED
}
