package com.gogidix.rapidassist.user.profile.service.interfaces.dto;

import java.time.Instant;

/**
 * DTO: ApiErrorResponse
 *
 * Standard API error response format for the user profile service.
 * Used across all REST endpoints for consistent error reporting.
 *
 * @author Rapid Assist
 * @version 1.0.0
 */
public record ApiErrorResponse(
        int status,
        String error,
        String message,
        String path,
        Instant timestamp,
        String tenantId,
        String correlationId
) {
    /**
     * Creates a basic error response.
     *
     * @param status the HTTP status code
     * @param error the error type
     * @param message the error message
     * @param path the request path
     * @return a new ApiErrorResponse instance
     */
    public static ApiErrorResponse of(int status, String error, String message, String path) {
        return new ApiErrorResponse(status, error, message, path, Instant.now(), null, null);
    }

    /**
     * Returns a new error response with tenant ID set.
     *
     * @param tenantId the tenant ID
     * @return a new ApiErrorResponse with tenant ID
     */
    public ApiErrorResponse withTenantId(String tenantId) {
        return new ApiErrorResponse(status, error, message, path, timestamp, tenantId, correlationId);
    }

    /**
     * Returns a new error response with correlation ID set.
     *
     * @param correlationId the correlation ID
     * @return a new ApiErrorResponse with correlation ID
     */
    public ApiErrorResponse withCorrelationId(String correlationId) {
        return new ApiErrorResponse(status, error, message, path, timestamp, tenantId, correlationId);
    }
}
