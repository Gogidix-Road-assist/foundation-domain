package com.gogidix.rapidassist.ai.speech.recognition.domain.model;

/**
 * Enum representing the status of a speech recognition request.
 */
public enum RecognitionStatus {

    /**
     * Request has been submitted but not yet processed
     */
    PENDING,

    /**
     * Audio file is being downloaded/prepared
     */
    PREPARING,

    /**
     * Speech recognition is in progress
     */
    PROCESSING,

    /**
     * Recognition completed successfully
     */
    COMPLETED,

    /**
     * Recognition failed with errors
     */
    FAILED,

    /**
     * Request was cancelled
     */
    CANCELLED,

    /**
     * Request is waiting for retry
     */
    RETRYING,

    /**
     * Request timed out
     */
    TIMEOUT
}
