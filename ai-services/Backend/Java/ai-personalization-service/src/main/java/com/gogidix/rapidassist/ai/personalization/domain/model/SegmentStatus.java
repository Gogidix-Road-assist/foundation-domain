package com.gogidix.rapidassist.ai.personalization.domain.model;

/**
 * Enumeration representing the status of a user segment.
 */
public enum SegmentStatus {
    /**
     * Segment is active
     */
    ACTIVE,

    /**
     * Segment is inactive
     */
    INACTIVE,

    /**
     * Segment is archived
     */
    ARCHIVED,

    /**
     * Segment is being calculated
     */
    CALCULATING,

    /**
     * Segment calculation failed
     */
    ERROR
}
