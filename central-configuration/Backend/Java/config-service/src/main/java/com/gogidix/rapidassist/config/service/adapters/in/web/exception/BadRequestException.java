package com.gogidix.rapidassist.config.service.adapters.in.web.exception;

/**
 * Exception thrown when a client request is invalid.
 * Typically results in HTTP 400 response.
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
