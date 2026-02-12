package com.gogidix.rapidassist.ai.personalization.domain.model;

/**
 * Enumeration representing the status of a campaign.
 */
public enum CampaignStatus {
    /**
     * Campaign is being drafted
     */
    DRAFT,

    /**
     * Campaign is awaiting approval
     */
    PENDING_APPROVAL,

    /**
     * Campaign is approved but not yet started
     */
    APPROVED,

    /**
     * Campaign is currently active
     */
    ACTIVE,

    /**
     * Campaign is paused
     */
    PAUSED,

    /**
     * Campaign is completed
     */
    COMPLETED,

    /**
     * Campaign is cancelled
     */
    CANCELLED,

    /**
     * Campaign is rejected
     */
    REJECTED
}
