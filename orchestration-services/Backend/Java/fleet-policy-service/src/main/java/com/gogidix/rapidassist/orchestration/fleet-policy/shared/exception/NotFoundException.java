package com.gogidix.rapidassist.orchestration.fleet_policy.shared.exception;

/**
 * Exception thrown when a resource is not found
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
