package com.gogidix.rapidassist.shared.ai.contracts.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("AIExecutionException Tests")
class AIExecutionExceptionTest {

    private static final String TENANT_ID = "tenant-123";
    private static final String REQUEST_ID = "request-456";

    @Test
    @DisplayName("Should create exception with all properties")
    void shouldCreateExceptionWithAllProperties() {
        AIExecutionException exception = new AIExecutionException(
                AIExecutionException.ErrorCode.VALIDATION_FAILED,
                TENANT_ID,
                REQUEST_ID,
                "Validation error message"
        );

        assertThat(exception.getErrorCode()).isEqualTo(AIExecutionException.ErrorCode.VALIDATION_FAILED);
        assertThat(exception.getTenantId()).isEqualTo(TENANT_ID);
        assertThat(exception.getRequestId()).isEqualTo(REQUEST_ID);
        assertThat(exception.getMessage()).isEqualTo("Validation error message");
        assertThat(exception.getTimestamp()).isNotNull();
        assertThat(exception.getDetails()).isNotNull();
    }

    @Test
    @DisplayName("Should create exception with cause")
    void shouldCreateExceptionWithCause() {
        Throwable cause = new RuntimeException("Inner cause");
        AIExecutionException exception = new AIExecutionException(
                AIExecutionException.ErrorCode.MODEL_EXECUTION_FAILED,
                TENANT_ID,
                REQUEST_ID,
                "Execution failed",
                cause
        );

        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getErrorCode()).isEqualTo(AIExecutionException.ErrorCode.MODEL_EXECUTION_FAILED);
    }

    @Test
    @DisplayName("Should create validation error exception")
    void shouldCreateValidationError() {
        AIExecutionException exception = AIExecutionException.validationError(
                TENANT_ID, REQUEST_ID, "Field X is required");

        assertThat(exception.getErrorCode()).isEqualTo(AIExecutionException.ErrorCode.VALIDATION_FAILED);
        assertThat(exception.getTenantId()).isEqualTo(TENANT_ID);
        assertThat(exception.getMessage()).contains("Field X is required");
        assertThat(exception.isClientError()).isTrue();
    }

    @Test
    @DisplayName("Should create tenant not found exception")
    void shouldCreateTenantNotFound() {
        AIExecutionException exception = AIExecutionException.tenantNotFound(
                TENANT_ID, REQUEST_ID);

        assertThat(exception.getErrorCode()).isEqualTo(AIExecutionException.ErrorCode.TENANT_NOT_FOUND);
        assertThat(exception.isClientError()).isTrue();
        assertThat(exception.getHttpStatusCode()).isEqualTo(403);
    }

    @Test
    @DisplayName("Should create rate limit exceeded exception")
    void shouldCreateRateLimitExceeded() {
        AIExecutionException exception = AIExecutionException.rateLimitExceeded(
                TENANT_ID, REQUEST_ID);

        assertThat(exception.getErrorCode()).isEqualTo(AIExecutionException.ErrorCode.RATE_LIMIT_EXCEEDED);
        assertThat(exception.isClientError()).isTrue();
        assertThat(exception.getHttpStatusCode()).isEqualTo(429);
        assertThat(exception.isRetryable()).isTrue();
    }

    @Test
    @DisplayName("Should create model execution failed exception")
    void shouldCreateModelExecutionFailed() {
        Throwable cause = new IllegalStateException("Model error");
        AIExecutionException exception = AIExecutionException.modelExecutionFailed(
                TENANT_ID, REQUEST_ID, cause);

        assertThat(exception.getErrorCode()).isEqualTo(AIExecutionException.ErrorCode.MODEL_EXECUTION_FAILED);
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.isServerError()).isTrue();
        assertThat(exception.isRetryable()).isFalse();
    }

    @Test
    @DisplayName("Should create timeout exception")
    void shouldCreateTimeout() {
        AIExecutionException exception = AIExecutionException.timeout(
                TENANT_ID, REQUEST_ID, 5000);

        assertThat(exception.getErrorCode()).isEqualTo(AIExecutionException.ErrorCode.TIMEOUT);
        assertThat(exception.getMessage()).contains("5000ms");
        assertThat(exception.isRetryable()).isTrue();
        assertThat(exception.isServerError()).isTrue();
    }

    @Test
    @DisplayName("Should create internal error exception")
    void shouldCreateInternalError() {
        Throwable cause = new RuntimeException("Unexpected error");
        AIExecutionException exception = AIExecutionException.internalError(
                TENANT_ID, REQUEST_ID, "Something went wrong", cause);

        assertThat(exception.getErrorCode()).isEqualTo(AIExecutionException.ErrorCode.INTERNAL_ERROR);
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.isServerError()).isTrue();
        assertThat(exception.getHttpStatusCode()).isEqualTo(500);
    }

    @Test
    @DisplayName("Should add detail to exception")
    void shouldAddDetailToException() {
        AIExecutionException exception = AIExecutionException.validationError(
                TENANT_ID, REQUEST_ID, "Invalid input");

        exception.addDetail("field_name", "email");
        exception.addDetail("error_code", "INVALID_FORMAT");

        assertThat(exception.getDetails()).hasSize(2);
        assertThat(exception.getDetails().get("field_name")).isEqualTo("email");
        assertThat(exception.getDetails().get("error_code")).isEqualTo("INVALID_FORMAT");
    }

    @Test
    @DisplayName("Should return correct error code string")
    void shouldReturnErrorCodeString() {
        AIExecutionException exception = new AIExecutionException(
                AIExecutionException.ErrorCode.VALIDATION_FAILED,
                TENANT_ID,
                REQUEST_ID,
                "Error"
        );

        assertThat(exception.getErrorCodeString()).isEqualTo("AI_400_VALIDATION_FAILED");
    }

    @Test
    @DisplayName("Should identify client errors correctly")
    void shouldIdentifyClientErrors() {
        assertThat(AIExecutionException.validationError(TENANT_ID, REQUEST_ID, "error").isClientError()).isTrue();
        assertThat(AIExecutionException.tenantNotFound(TENANT_ID, REQUEST_ID).isClientError()).isTrue();
        assertThat(AIExecutionException.rateLimitExceeded(TENANT_ID, REQUEST_ID).isClientError()).isTrue();
        assertThat(AIExecutionException.internalError(TENANT_ID, REQUEST_ID, "error", null).isClientError()).isFalse();
    }

    @Test
    @DisplayName("Should identify server errors correctly")
    void shouldIdentifyServerErrors() {
        assertThat(AIExecutionException.internalError(TENANT_ID, REQUEST_ID, "error", null).isServerError()).isTrue();
        assertThat(AIExecutionException.modelExecutionFailed(TENANT_ID, REQUEST_ID, null).isServerError()).isTrue();
        assertThat(AIExecutionException.timeout(TENANT_ID, REQUEST_ID, 1000).isServerError()).isTrue();
        assertThat(AIExecutionException.validationError(TENANT_ID, REQUEST_ID, "error").isServerError()).isFalse();
    }

    @Test
    @DisplayName("Should identify retryable errors correctly")
    void shouldIdentifyRetryableErrors() {
        assertThat(AIExecutionException.timeout(TENANT_ID, REQUEST_ID, 1000).isRetryable()).isTrue();
        assertThat(AIExecutionException.rateLimitExceeded(TENANT_ID, REQUEST_ID).isRetryable()).isTrue();
        assertThat(AIExecutionException.validationError(TENANT_ID, REQUEST_ID, "error").isRetryable()).isFalse();
        assertThat(AIExecutionException.modelExecutionFailed(TENANT_ID, REQUEST_ID, null).isRetryable()).isFalse();
    }

    @Test
    @DisplayName("Should return correct HTTP status codes")
    void shouldReturnCorrectHttpStatusCodes() {
        assertThat(AIExecutionException.validationError(TENANT_ID, REQUEST_ID, "error").getHttpStatusCode()).isEqualTo(400);
        assertThat(AIExecutionException.tenantNotFound(TENANT_ID, REQUEST_ID).getHttpStatusCode()).isEqualTo(403);
        assertThat(AIExecutionException.rateLimitExceeded(TENANT_ID, REQUEST_ID).getHttpStatusCode()).isEqualTo(429);
        assertThat(AIExecutionException.internalError(TENANT_ID, REQUEST_ID, "error", null).getHttpStatusCode()).isEqualTo(500);
    }

    @Test
    @DisplayName("Should handle null error code gracefully")
    void shouldHandleNullErrorCode() {
        // Create exception without going through factory methods
        AIExecutionException exception = new AIExecutionException(null, TENANT_ID, REQUEST_ID, "Error") {
            // Anonymous subclass to bypass normal construction
        };

        assertThat(exception.getErrorCode()).isNull();
        assertThat(exception.getHttpStatusCode()).isEqualTo(500);
        assertThat(exception.getErrorCodeString()).isNull();
        assertThat(exception.isRetryable()).isFalse();
    }

    @Test
    @DisplayName("Should return correct messages for error codes")
    void shouldReturnCorrectMessagesForErrorCodes() {
        assertThat(AIExecutionException.ErrorCode.VALIDATION_FAILED.getMessage())
                .isEqualTo("Request validation failed");

        assertThat(AIExecutionException.ErrorCode.TENANT_NOT_FOUND.getMessage())
                .isEqualTo("Tenant not found or not authorized");

        assertThat(AIExecutionException.ErrorCode.TIMEOUT.getMessage())
                .isEqualTo("AI operation timed out");

        assertThat(AIExecutionException.ErrorCode.RATE_LIMIT_EXCEEDED.getMessage())
                .isEqualTo("Rate limit exceeded");
    }

    @Test
    @DisplayName("Should return correct codes for error codes")
    void shouldReturnCorrectCodesForErrorCodes() {
        assertThat(AIExecutionException.ErrorCode.VALIDATION_FAILED.getCode())
                .isEqualTo("AI_400_VALIDATION_FAILED");

        assertThat(AIExecutionException.ErrorCode.TENANT_NOT_FOUND.getCode())
                .isEqualTo("AI_403_TENANT_NOT_FOUND");

        assertThat(AIExecutionException.ErrorCode.TIMEOUT.getCode())
                .isEqualTo("AI_500_TIMEOUT");

        assertThat(AIExecutionException.ErrorCode.RATE_LIMIT_EXCEEDED.getCode())
                .isEqualTo("AI_429_RATE_LIMIT");
    }
}
