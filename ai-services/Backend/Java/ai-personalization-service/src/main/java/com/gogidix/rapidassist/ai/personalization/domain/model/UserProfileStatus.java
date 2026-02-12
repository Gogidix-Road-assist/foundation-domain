package com.gogidix.rapidassist.ai.personalization.domain.model;

/**
 * Enumeration representing the status of a user profile.
 */
public enum UserProfileStatus {
    /**
     * Profile is active and being used for personalization
     */
    ACTIVE,

    /**
     * Profile is inactive and not being used
     */
    INACTIVE,

    /**
     * Profile is suspended due to user request or policy
     */
    SUSPENDED,

    /**
     * Profile is pending deletion
     */
    PENDING_DELETION
}
