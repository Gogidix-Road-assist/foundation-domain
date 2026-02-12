package com.gogidix.rapidassist.ai.chatbot.interfaces.rest.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

/**
 * Standard error response structure
 * Used by GlobalExceptionHandler for consistent error responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    /**
     * Timestamp when error occurred
     */
    private Instant timestamp;

    /**
     * HTTP status code
     */
    private Integer status;

    /**
     * Error type/category
     */
    private String error;

    /**
     * Human-readable error message
     */
    private String message;

    /**
     * Request path that caused the error
     */
    private String path;

    /**
     * Field-level validation errors (optional)
     */
    private Map<String, String> fieldErrors;

    /**
     * Tenant ID for tracing
     */
    private String tenantId;

    /**
     * Correlation ID for tracing
     */
    private String correlationId;

    /**
     * Create error response with tenant context
     */
    public static ErrorResponseBuilder builder() {
        return new ErrorResponseBuilder();
    }

    /**
     * Builder with tenant context
     */
    public static class ErrorResponseBuilder {
        private Instant timestamp;
        private Integer status;
        private String error;
        private String message;
        private String path;
        private Map<String, String> fieldErrors;

        public ErrorResponseBuilder tenantId(String tenantId) {
            // Tenant ID can be added from RequestContext if needed
            return this;
        }

        public ErrorResponseBuilder correlationId(String correlationId) {
            // Correlation ID can be added from RequestContext if needed
            return this;
        }

        public ErrorResponse build() {
            if (this.timestamp == null) {
                this.timestamp = Instant.now();
            }
            return new ErrorResponse(
                timestamp,
                status,
                error,
                message,
                path,
                fieldErrors,
                null, // tenantId set separately
                null  // correlationId set separately
            );
        }
    }
}
