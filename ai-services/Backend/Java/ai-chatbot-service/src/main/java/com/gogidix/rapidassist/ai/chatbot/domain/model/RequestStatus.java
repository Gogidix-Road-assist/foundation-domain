package com.gogidix.rapidassist.ai.chatbot.domain.model;

/**
 * Enumeration representing the status of a chatbot request.
 */
public enum RequestStatus {

    /**
     * Request has been created but not yet processed
     */
    PENDING,

    /**
     * Request is currently being processed
     */
    PROCESSING,

    /**
     * Request has been completed successfully
     */
    COMPLETED,

    /**
     * Request failed during processing
     */
    FAILED,

    /**
     * Request was cancelled
     */
    CANCELLED,

    /**
     * Request has been queued for processing
     */
    QUEUED
}
