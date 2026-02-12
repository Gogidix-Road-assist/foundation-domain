package com.gogidix.rapidassist.config.service.application.dto.response;

import java.time.Instant;
import java.util.Set;

/**
 * Standard error response DTO for API error responses.
 *
 * <p>This DTO provides consistent error information across all API endpoints.
 */
public record ErrorResponseDto(
    Instant timestamp,
    int status,
    String error,
    String message,
    String path,
    String tenantId,
    String correlationId,
    Set<String> validationErrors
) {
    /**
     * Creates an error response without validation errors.
     *
     * @param status  the HTTP status code
     * @param error   the error type
     * @param message the error message
     * @param path    the request path
     * @return a new error response
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
            Set.of()
        );
    }

    /**
     * Creates an error response with tenant context.
     *
     * @param status  the HTTP status code
     * @param error   the error type
     * @param message the error message
     * @param path    the request path
     * @param tenantId the tenant ID
     * @return a new error response with tenant context
     */
    public static ErrorResponseDto of(
            int status,
            String error,
            String message,
            String path,
            String tenantId) {
        return new ErrorResponseDto(
            Instant.now(),
            status,
            error,
            message,
            path,
            tenantId,
            null,
            Set.of()
        );
    }

    /**
     * Creates a validation error response.
     *
     * @param message          the error message
     * @param path             the request path
     * @param validationErrors the set of validation errors
     * @return a new validation error response
     */
    public static ErrorResponseDto validationError(
            String message,
            String path,
            Set<String> validationErrors) {
        return new ErrorResponseDto(
            Instant.now(),
            400,
            "Validation Failed",
            message,
            path,
            null,
            null,
            validationErrors
        );
    }

    /**
     * Builder pattern for creating error responses with all fields.
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Instant timestamp = Instant.now();
        private int status;
        private String error;
        private String message;
        private String path;
        private String tenantId;
        private String correlationId;
        private Set<String> validationErrors = Set.of();

        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder status(int status) {
            this.status = status;
            return this;
        }

        public Builder error(String error) {
            this.error = error;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder tenantId(String tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        public Builder correlationId(String correlationId) {
            this.correlationId = correlationId;
            return this;
        }

        public Builder validationErrors(Set<String> validationErrors) {
            this.validationErrors = validationErrors;
            return this;
        }

        public ErrorResponseDto build() {
            return new ErrorResponseDto(
                timestamp, status, error, message, path,
                tenantId, correlationId, validationErrors
            );
        }
    }
}
