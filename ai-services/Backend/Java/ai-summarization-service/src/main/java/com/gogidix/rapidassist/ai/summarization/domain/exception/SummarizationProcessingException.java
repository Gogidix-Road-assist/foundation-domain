package com.gogidix.rapidassist.ai.summarization.domain.exception;

public class SummarizationProcessingException extends RuntimeException {
    public SummarizationProcessingException(String message) {
        super(message);
    }

    public SummarizationProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
