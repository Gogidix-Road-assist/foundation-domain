package com.gogidix.rapidassist.ai.translation.domain.model;

/**
 * Status of a translation request.
 */
public enum TranslationStatus {

    /**
     * Translation is pending processing.
     */
    PENDING,

    /**
     * Translation is currently being processed.
     */
    IN_PROGRESS,

    /**
     * Translation completed successfully.
     */
    COMPLETED,

    /**
     * Translation failed.
     */
    FAILED,

    /**
     * Translation was cancelled.
     */
    CANCELLED,

    /**
     * Translation is cached (retrieved from cache).
     */
    CACHED
}
