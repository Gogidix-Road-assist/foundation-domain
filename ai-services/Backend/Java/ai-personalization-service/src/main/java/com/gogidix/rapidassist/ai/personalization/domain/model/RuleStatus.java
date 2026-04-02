package com.gogidix.rapidassist.ai.personalization.domain.model;

/**
 * Enumeration representing the status of a personalization rule.
 */
public enum RuleStatus {
    /**
     * Rule is being drafted
     */
    DRAFT,

    /**
     * Rule is active and being executed
     */
    ACTIVE,

    /**
     * Rule is inactive
     */
    INACTIVE,

    /**
     * Rule is archived
     */
    ARCHIVED,

    /**
     * Rule is scheduled for future execution
     */
    SCHEDULED
}
