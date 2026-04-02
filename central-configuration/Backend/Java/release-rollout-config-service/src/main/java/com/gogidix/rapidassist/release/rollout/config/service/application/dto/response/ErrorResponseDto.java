package com.gogidix.rapidassist.release.rollout.config.service.application.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.Set;

/**
 * Error response DTO for API errors.
 */
public record ErrorResponseDto(
    int status,
    String error,
    String message,
    List<String> details,
    Set<String> warnings,
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
            List.of(),
            Set.of(),
            null,
            Instant.now()
        );
    }

    /**
     * Creates an error response with details.
     *
     * @param status  the HTTP status code
     * @param error   the error type
     * @param message the error message
     * @param details list of error details
     * @return a new ErrorResponseDto
     */
    public static ErrorResponseDto withDetails(
            int status,
            String error,
            String message,
            List<String> details) {
        return new ErrorResponseDto(
            status,
            error,
            message,
            details,
            Set.of(),
            null,
            Instant.now()
        );
    }

    /**
     * Creates an error response with path.
     *
     * @param status  the HTTP status code
     * @param error   the error type
     * @param message the error message
     * @param path    the request path
     * @return a new ErrorResponseDto
     */
    public static ErrorResponseDto withPath(
            int status,
            String error,
            String message,
            String path) {
        return new ErrorResponseDto(
            status,
            error,
            message,
            List.of(),
            Set.of(),
            path,
            Instant.now()
        );
    }
}
