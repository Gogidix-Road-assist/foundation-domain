package com.gogidix.rapidassist.ai.personalization.domain.model;

/**
 * Enumeration representing the status of an A/B test.
 */
public enum ABTestStatus {
    /**
     * Test is being drafted
     */
    DRAFT,

    /**
     * Test is scheduled to run
     */
    SCHEDULED,

    /**
     * Test is currently running
     */
    RUNNING,

    /**
     * Test is paused
     */
    PAUSED,

    /**
     * Test is stopped
     */
    STOPPED,

    /**
     * Test is completed
     */
    COMPLETED,

    /**
     * Test failed
     */
    FAILED
}
