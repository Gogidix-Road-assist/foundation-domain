package com.gogidix.rapidassist.country.localization.config.service.application.dto.response;

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
    List<String> errors,
    String path,
    Instant timestamp
) {
    /**
     * Creates an error response.
     *
     * @param status the HTTP status code
     * @param error the error type
     * @param message the error message
     * @param path the request path
     * @return a new ErrorResponseDto
     */
    public static ErrorResponseDto of(int status, String error, String message, String path) {
        return new ErrorResponseDto(
            status,
            error,
            message,
            null,
            path,
            Instant.now()
        );
    }

    /**
     * Creates an error response with validation errors.
     *
     * @param status the HTTP status code
     * @param error the error type
     * @param message the error message
     * @param errors the list of validation errors
     * @param path the request path
     * @return a new ErrorResponseDto
     */
    public static ErrorResponseDto withValidationErrors(int status, String error, String message, Set<String> errors, String path) {
        return new ErrorResponseDto(
            status,
            error,
            message,
            List.copyOf(errors),
            path,
            Instant.now()
        );
    }
}
