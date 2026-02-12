package com.gogidix.rapidassist.ai.search.optimization.domain.exception;

/**
 * Base exception for Search Optimization Service.
 */
public class SearchOptimizationException extends RuntimeException {

    public SearchOptimizationException(String message) {
        super(message);
    }

    public SearchOptimizationException(String message, Throwable cause) {
        super(message, cause);
    }
}
