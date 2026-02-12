package com.gogidix.rapidassist.ai.search.optimization.domain.exception;

/**
 * Exception thrown when a search query is invalid.
 */
public class InvalidSearchQueryException extends SearchOptimizationException {

    public InvalidSearchQueryException(String message) {
        super(message);
    }

    public InvalidSearchQueryException(String message, Throwable cause) {
        super(message, cause);
    }
}
