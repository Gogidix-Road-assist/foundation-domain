package com.gogidix.rapidassist.policy.configuration.service.application.dto.response;

import java.time.Instant;
import java.util.Set;

/**
 * Error response DTO.
 *
 * <p>This DTO is used for API error responses and contains
 * details about errors that occurred during request processing.
 */
public record ErrorResponseDto(
    int status,
    String error,
    String message,
    Set<String> errors,
    String path,
    Instant timestamp
) {
    /**
     * Creates an error response from exception details.
     *
     * @param status  the HTTP status code
     * @param error   the error type
     * @param message the error message
     * @param path    the request path
     * @return an error response DTO
     */
    public static ErrorResponseDto of(int status, String error, String message, String path) {
        return new ErrorResponseDto(
            status,
            error,
            message,
            Set.of(),
            path,
            Instant.now()
        );
    }

    /**
     * Creates an error response with validation errors.
     *
     * @param status  the HTTP status code
     * @param error   the error type
     * @param message the error message
     * @param errors  the validation errors
     * @param path    the request path
     * @return an error response DTO
     */
    public static ErrorResponseDto of(int status, String error, String message, Set<String> errors, String path) {
        return new ErrorResponseDto(
            status,
            error,
            message,
            errors,
            path,
            Instant.now()
        );
    }
}
