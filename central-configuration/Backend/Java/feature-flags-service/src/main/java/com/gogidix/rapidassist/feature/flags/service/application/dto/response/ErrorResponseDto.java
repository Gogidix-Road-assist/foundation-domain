package com.gogidix.rapidassist.feature.flags.service.application.dto.response;

import java.time.Instant;
import java.util.Set;

/**
 * Error response DTO for API errors.
 */
public record ErrorResponseDto(
    int status,
    String error,
    String message,
    Set<String> violations,
    String path,
    Instant timestamp
) {
    /**
     * Creates a basic error response.
     *
     * @param status  the HTTP status code
     * @param error   the error type
     * @param message the error message
     * @return a new ErrorResponseDto
     */
    public static ErrorResponseDto of(int status, String error, String message) {
        return new ErrorResponseDto(
            status,
            error,
            message,
            Set.of(),
            null,
            Instant.now()
        );
    }

    /**
     * Creates a validation error response with field violations.
     *
     * @param status      the HTTP status code
     * @param error       the error type
     * @param message     the error message
     * @param violations  the field violations
     * @return a new ErrorResponseDto
     */
    public static ErrorResponseDto validation(int status, String error, String message, Set<String> violations) {
        return new ErrorResponseDto(
            status,
            error,
            message,
            violations,
            null,
            Instant.now()
        );
    }

    /**
     * Creates an error response with path information.
     *
     * @param status  the HTTP status code
     * @param error   the error type
     * @param message the error message
     * @param path    the request path
     * @return a new ErrorResponseDto
     */
    public static ErrorResponseDto withPath(int status, String error, String message, String path) {
        return new ErrorResponseDto(
            status,
            error,
            message,
            Set.of(),
            path,
            Instant.now()
        );
    }
}
