package com.gogidix.rapidassist.policy.configuration.service.adapters.in.web.exception;

/**
 * Exception thrown when a requested resource is not found
 *
 * @author Rapid Assist Team
 * @since 1.0.0
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String resourceName, String resourceId) {
        super(String.format("%s with id '%s' not found", resourceName, resourceId));
    }

    public NotFoundException(String resourceName, String field, String value) {
        super(String.format("%s not found with %s: '%s'", resourceName, field, value));
    }
}
