package com.gogidix.rapidassist.dynamic.routing.config.service.application.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.Set;

/**
 * Error response DTO for API error responses.
 */
public record ErrorResponseDto(
    Instant timestamp,
    int status,
    String error,
    String message,
    String path,
    String tenantId,
    String correlationId,
    List<String> errors,
    Set<String> warnings
) {
    /**
     * Creates a basic error response.
     *
     * @param status  the HTTP status code
     * @param error   the error type
     * @param message the error message
     * @param path    the request path
     * @return an error response
     */
    public static ErrorResponseDto of(int status, String error, String message, String path) {
        return new ErrorResponseDto(
            Instant.now(),
            status,
            error,
            message,
            path,
            null,
            null,
            List.of(),
            Set.of()
        );
    }

    /**
     * Creates an error response with tenant context.
     *
     * @param status   the HTTP status code
     * @param error    the error type
     * @param message  the error message
     * @param path     the request path
     * @param tenantId the tenant ID
     * @return an error response
     */
    public static ErrorResponseDto withTenant(int status, String error, String message, String path, String tenantId) {
        return new ErrorResponseDto(
            Instant.now(),
            status,
            error,
            message,
            path,
            tenantId,
            null,
            List.of(),
            Set.of()
        );
    }

    /**
     * Creates an error response with validation errors.
     *
     * @param status  the HTTP status code
     * @param error   the error type
     * @param message the error message
     * @param path    the request path
     * @param errors  the list of validation errors
     * @return an error response
     */
    public static ErrorResponseDto withValidationErrors(
            int status, String error, String message, String path, List<String> errors) {
        return new ErrorResponseDto(
            Instant.now(),
            status,
            error,
            message,
            path,
            null,
            null,
            errors,
            Set.of()
        );
    }

    /**
     * Creates an error response with full context.
     *
     * @param status        the HTTP status code
     * @param error         the error type
     * @param message       the error message
     * @param path          the request path
     * @param tenantId      the tenant ID
     * @param correlationId the correlation ID
     * @param errors        the list of errors
     * @param warnings      the set of warnings
     * @return an error response
     */
    public static ErrorResponseDto full(
            int status, String error, String message, String path,
            String tenantId, String correlationId, List<String> errors, Set<String> warnings) {
        return new ErrorResponseDto(
            Instant.now(),
            status,
            error,
            message,
            path,
            tenantId,
            correlationId,
            errors,
            warnings
        );
    }
}
