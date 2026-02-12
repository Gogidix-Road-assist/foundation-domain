package com.gogidix.rapidassist.policy.configuration.service.adapters.in.web.exception;

/**
 * Exception thrown when a request conflicts with existing data
 *
 * @author Rapid Assist Team
 * @since 1.0.0
 */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(String resourceName, String field, String value) {
        super(String.format("%s already exists with %s: '%s'", resourceName, field, value));
    }
}
