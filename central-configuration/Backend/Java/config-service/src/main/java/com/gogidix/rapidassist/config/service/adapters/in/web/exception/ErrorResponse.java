package com.gogidix.rapidassist.config.service.adapters.in.web.exception;

import java.time.Instant;
import java.util.UUID;

/**
 * Standardized error response format for all API errors.
 * Provides consistent error information including correlation ID for tracing.
 */
public record ErrorResponse(
    String type,
    String message,
    int status,
    String path,
    String correlationId,
    Instant timestamp,
    ErrorResponse.Details details
) {

    public ErrorResponse {
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }
    }

    public ErrorResponse(String type, String message, int status, String path, String correlationId, Instant timestamp) {
        this(type, message, status, path, correlationId, timestamp, null);
    }

    /**
     * Additional error details for validation errors or nested exceptions.
     */
    public record Details(
        String field,
        Object rejectedValue,
        String reason
    ) {}

    /**
     * Builder pattern for ErrorResponse.
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String type;
        private String message;
        private int status;
        private String path;
        private String correlationId;
        private Instant timestamp;
        private Details details;

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder status(int status) {
            this.status = status;
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder correlationId(String correlationId) {
            this.correlationId = correlationId;
            return this;
        }

        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder details(Details details) {
            this.details = details;
            return this;
        }

        public ErrorResponse build() {
            if (timestamp == null) {
                timestamp = Instant.now();
            }
            if (correlationId == null || correlationId.isBlank()) {
                correlationId = UUID.randomUUID().toString();
            }
            return new ErrorResponse(type, message, status, path, correlationId, timestamp, details);
        }
    }
}
