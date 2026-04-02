package com.gogidix.rapidassist.shared.ai.contracts.domain.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.Map;
import java.util.TreeMap;

/**
 * Exception thrown when AI execution fails.
 * Provides structured error information for client handling and observability.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AIExecutionException extends RuntimeException {

    /**
     * Error codes for AI execution failures.
     */
    public enum ErrorCode {
        // Validation errors (400)
        INVALID_REQUEST("AI_400_INVALID_REQUEST", "The request is invalid"),
        MISSING_REQUIRED_FIELD("AI_400_MISSING_FIELD", "A required field is missing"),
        VALIDATION_FAILED("AI_400_VALIDATION_FAILED", "Request validation failed"),

        // Tenant errors (403)
        TENANT_NOT_FOUND("AI_403_TENANT_NOT_FOUND", "Tenant not found or not authorized"),
        TENANT_LIMIT_EXCEEDED("AI_403_LIMIT_EXCEEDED", "Tenant limit exceeded"),
        FEATURE_NOT_ENABLED("AI_403_FEATURE_NOT_ENABLED", "Feature not enabled for tenant"),

        // Resource errors (404)
        MODEL_NOT_FOUND("AI_404_MODEL_NOT_FOUND", "AI model not found"),
        RESOURCE_NOT_FOUND("AI_404_RESOURCE_NOT_FOUND", "Required resource not found"),

        // Rate limiting (429)
        RATE_LIMIT_EXCEEDED("AI_429_RATE_LIMIT", "Rate limit exceeded"),
        QUOTA_EXCEEDED("AI_429_QUOTA_EXCEEDED", "Quota exceeded"),

        // Server errors (500)
        INTERNAL_ERROR("AI_500_INTERNAL_ERROR", "Internal AI service error"),
        MODEL_EXECUTION_FAILED("AI_500_MODEL_FAILED", "Model execution failed"),
        TIMEOUT("AI_500_TIMEOUT", "AI operation timed out"),
        UNAVAILABLE("AI_503_UNAVAILABLE", "AI service temporarily unavailable"),

        // Configuration errors (500)
        CONFIGURATION_ERROR("AI_500_CONFIG", "Service configuration error"),
        DEPENDENCY_ERROR("AI_503_DEPENDENCY", "Required dependency unavailable");

        private final String code;
        private final String message;

        ErrorCode(String code, String message) {
            this.code = code;
            this.message = message;
        }

        public String getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }

    private final ErrorCode errorCode;
    private final String tenantId;
    private final String requestId;
    private final Instant timestamp;
    private final Map<String, Object> details;

    /**
     * Creates a new AI execution exception.
     *
     * @param errorCode  The error code
     * @param tenantId   The tenant ID
     * @param requestId  The request ID
     * @param message    The error message
     */
    public AIExecutionException(ErrorCode errorCode, String tenantId, String requestId, String message) {
        super(message);
        this.errorCode = errorCode;
        this.tenantId = tenantId;
        this.requestId = requestId;
        this.timestamp = Instant.now();
        this.details = new TreeMap<>();
    }

    /**
     * Creates a new AI execution exception with a cause.
     *
     * @param errorCode  The error code
     * @param tenantId   The tenant ID
     * @param requestId  The request ID
     * @param message    The error message
     * @param cause      The underlying cause
     */
    public AIExecutionException(ErrorCode errorCode, String tenantId, String requestId, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.tenantId = tenantId;
        this.requestId = requestId;
        this.timestamp = Instant.now();
        this.details = new TreeMap<>();
    }

    /**
     * Creates a validation error exception.
     *
     * @param tenantId The tenant ID
     * @param requestId The request ID
     * @param message  The validation message
     * @return A new AIExecutionException
     */
    public static AIExecutionException validationError(String tenantId, String requestId, String message) {
        return new AIExecutionException(ErrorCode.VALIDATION_FAILED, tenantId, requestId, message);
    }

    /**
     * Creates a tenant not found exception.
     *
     * @param tenantId The tenant ID
     * @param requestId The request ID
     * @return A new AIExecutionException
     */
    public static AIExecutionException tenantNotFound(String tenantId, String requestId) {
        return new AIExecutionException(ErrorCode.TENANT_NOT_FOUND, tenantId, requestId,
                "Tenant not found or not authorized: " + tenantId);
    }

    /**
     * Creates a rate limit exceeded exception.
     *
     * @param tenantId The tenant ID
     * @param requestId The request ID
     * @return A new AIExecutionException
     */
    public static AIExecutionException rateLimitExceeded(String tenantId, String requestId) {
        return new AIExecutionException(ErrorCode.RATE_LIMIT_EXCEEDED, tenantId, requestId,
                "Rate limit exceeded for tenant: " + tenantId);
    }

    /**
     * Creates a model execution failed exception.
     *
     * @param tenantId The tenant ID
     * @param requestId The request ID
     * @param cause     The underlying cause
     * @return A new AIExecutionException
     */
    public static AIExecutionException modelExecutionFailed(String tenantId, String requestId, Throwable cause) {
        return new AIExecutionException(ErrorCode.MODEL_EXECUTION_FAILED, tenantId, requestId,
                "AI model execution failed", cause);
    }

    /**
     * Creates a timeout exception.
     *
     * @param tenantId The tenant ID
     * @param requestId The request ID
     * @param timeoutMs The timeout in milliseconds
     * @return A new AIExecutionException
     */
    public static AIExecutionException timeout(String tenantId, String requestId, long timeoutMs) {
        return new AIExecutionException(ErrorCode.TIMEOUT, tenantId, requestId,
                "AI operation timed out after " + timeoutMs + "ms");
    }

    /**
     * Creates an internal error exception.
     *
     * @param tenantId The tenant ID
     * @param requestId The request ID
     * @param message  The error message
     * @param cause     The underlying cause
     * @return A new AIExecutionException
     */
    public static AIExecutionException internalError(String tenantId, String requestId, String message, Throwable cause) {
        return new AIExecutionException(ErrorCode.INTERNAL_ERROR, tenantId, requestId, message, cause);
    }

    /**
     * Adds a detail to this exception.
     *
     * @param key   The detail key
     * @param value The detail value
     * @return This exception for chaining
     */
    public AIExecutionException addDetail(String key, Object value) {
        this.details.put(key, value);
        return this;
    }

    // Getters

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String getErrorCodeString() {
        return errorCode != null ? errorCode.getCode() : null;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getRequestId() {
        return requestId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    /**
     * Gets the HTTP status code for this error.
     *
     * @return The HTTP status code
     */
    public int getHttpStatusCode() {
        if (errorCode == null) {
            return 500;
        }
        String code = errorCode.getCode();
        if (code.startsWith("AI_400")) {
            return 400;
        } else if (code.startsWith("AI_403")) {
            return 403;
        } else if (code.startsWith("AI_404")) {
            return 404;
        } else if (code.startsWith("AI_429")) {
            return 429;
        } else if (code.startsWith("AI_503")) {
            return 503;
        }
        return 500;
    }

    /**
     * Checks if this is a client error (4xx).
     *
     * @return true if this is a client error
     */
    public boolean isClientError() {
        int status = getHttpStatusCode();
        return status >= 400 && status < 500;
    }

    /**
     * Checks if this is a server error (5xx).
     *
     * @return true if this is a server error
     */
    public boolean isServerError() {
        int status = getHttpStatusCode();
        return status >= 500 && status < 600;
    }

    /**
     * Checks if this error is retryable.
     *
     * @return true if this error can be retried
     */
    public boolean isRetryable() {
        if (errorCode == null) {
            return false;
        }
        return errorCode == ErrorCode.TIMEOUT
                || errorCode == ErrorCode.RATE_LIMIT_EXCEEDED
                || errorCode == ErrorCode.QUOTA_EXCEEDED
                || errorCode == ErrorCode.UNAVAILABLE
                || errorCode == ErrorCode.DEPENDENCY_ERROR;
    }
}
