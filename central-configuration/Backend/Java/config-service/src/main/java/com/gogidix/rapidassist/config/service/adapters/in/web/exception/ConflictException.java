package com.gogidix.rapidassist.config.service.adapters.in.web.exception;

/**
 * Exception thrown when a request conflicts with existing data.
 * Typically results in HTTP 409 response.
 */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
