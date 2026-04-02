package com.gogidix.rapidassist.ai.summization.domain.exception;

/**
 * Exception thrown when summarization processing fails.
 */
public class SummarizationProcessingException extends RuntimeException {

    public SummarizationProcessingException(String message) {
        super(message);
    }

    public SummarizationProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
