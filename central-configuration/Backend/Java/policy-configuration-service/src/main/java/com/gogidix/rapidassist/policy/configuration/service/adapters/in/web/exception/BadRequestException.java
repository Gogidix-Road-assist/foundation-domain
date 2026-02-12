package com.gogidix.rapidassist.policy.configuration.service.adapters.in.web.exception;

/**
 * Exception thrown when a request is malformed or invalid
 *
 * @author Rapid Assist Team
 * @since 1.0.0
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
