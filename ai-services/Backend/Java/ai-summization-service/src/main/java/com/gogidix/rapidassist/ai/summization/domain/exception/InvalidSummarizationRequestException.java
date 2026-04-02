package com.gogidix.rapidassist.ai.summization.domain.exception;

/**
 * Exception thrown when a summarization request is invalid.
 */
public class InvalidSummarizationRequestException extends RuntimeException {

    public InvalidSummarizationRequestException(String message) {
        super(message);
    }

    public InvalidSummarizationRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
