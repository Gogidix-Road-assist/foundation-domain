package com.gogidix.rapidassist.policy.configuration.service.adapters.in.web.exception;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Standard error response format for API errors
 * Provides consistent structure across all error responses
 *
 * @author Rapid Assist Team
 * @since 1.0.0
 */
public record ErrorResponse(
        String timestamp,
        int status,
        String error,
        String message,
        Map<String, String> validationErrors,
        String path,
        String correlationId
) {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter
            .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .withZone(ZoneId.of("UTC"));

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Instant timestamp;
        private int status;
        private String error;
        private String message;
        private Map<String, String> validationErrors;
        private String path;
        private String correlationId;

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

        public Builder validationErrors(Map<String, String> validationErrors) {
            this.validationErrors = validationErrors;
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

        public ErrorResponse build() {
            return new ErrorResponse(
                    timestamp != null ? FORMATTER.format(timestamp) : FORMATTER.format(Instant.now()),
                    status,
                    error,
                    message,
                    validationErrors,
                    path,
                    correlationId
            );
        }
    }
}
