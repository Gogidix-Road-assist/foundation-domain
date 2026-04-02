package com.gogidix.rapidassist.config.service.adapters.in.web.exception;

import java.util.Set;

/**
 * Exception thrown when validation fails.
 * Typically results in HTTP 400 response with detailed validation errors.
 */
public class ValidationException extends RuntimeException {

    private final Set<String> errors;

    public ValidationException(String message) {
        super(message);
        this.errors = Set.of();
    }

    public ValidationException(String message, Set<String> errors) {
        super(message);
        this.errors = errors;
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
        this.errors = Set.of();
    }

    public Set<String> getErrors() {
        return errors;
    }
}
