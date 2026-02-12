package com.gogidix.rapidassist.orchestration.alerting_service.shared.exception;

/**
 * Exception thrown when a conflict occurs (e.g., duplicate resource, invalid state transition)
 */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
