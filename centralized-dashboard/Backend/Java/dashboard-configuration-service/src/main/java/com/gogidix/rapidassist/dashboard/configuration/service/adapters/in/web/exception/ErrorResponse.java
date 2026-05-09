package com.gogidix.rapidassist.dashboard.configuration.service.adapters.in.web.exception;

import java.time.Instant;
import java.util.UUID;

/**
 * Standardized error response structure for all API errors.
 *
 * <p>This class provides a consistent format for error responses across all endpoints,
 * including timestamp, status, error type, message, request path, and a unique request ID
 * for tracing.</p>
 *
 * @param timestamp   The instant when the error occurred
 * @param status      The HTTP status code
 * @param error       The error type/category
 * @param message     Human-readable error message
 * @param path        The request path that caused the error
 * @param requestId   Unique identifier for the request (useful for tracing)
 * @author Rapid Assist Team
 * @since 1.0.0
 */
public record ErrorResponse(
    Instant timestamp,
    int status,
    String error,
    String message,
    String path,
    String requestId
) {
    /**
     * Creates a new ErrorResponse with a generated request ID.
     *
     * @param status  The HTTP status code
     * @param error   The error type/category
     * @param message Human-readable error message
     * @param path    The request path that caused the error
     * @return a new ErrorResponse instance
     */
    public static ErrorResponse of(int status, String error, String message, String path) {
        return new ErrorResponse(
            Instant.now(),
            status,
            error,
            message,
            path,
            UUID.randomUUID().toString()
        );
    }

    /**
     * Creates a new ErrorResponse with a specific request ID.
     *
     * @param status    The HTTP status code
     * @param error     The error type/category
     * @param message   Human-readable error message
     * @param path      The request path that caused the error
     * @param requestId Unique identifier for the request
     * @return a new ErrorResponse instance
     */
    public static ErrorResponse of(int status, String error, String message, String path, String requestId) {
        return new ErrorResponse(
            Instant.now(),
            status,
            error,
            message,
            path,
            requestId
        );
    }
}
