package com.gogidix.rapidassist.shared.ai.contracts.domain.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Base request model for all AI service operations.
 * All AI service requests must extend this class to ensure:
 * - Tenant isolation
 * - Request tracing
 * - Consistent metadata structure
 */
public abstract class AIRequest {

    /**
     * Unique identifier for this request. If not provided, one will be generated.
     */
    protected String requestId;

    /**
     * Tenant identifier - MANDATORY for all AI operations.
     * This ensures tenant isolation and is validated before execution.
     */
    @NotBlank(message = "TenantId is required for AI operations")
    protected String tenantId;

    /**
     * Correlation ID for distributed tracing.
     * Links this request to other operations in a transaction chain.
     */
    protected String correlationId;

    /**
     * User ID of the requester, if available.
     */
    protected String userId;

    /**
     * The timestamp when this request was created.
     */
    protected Instant requestTimestamp;

    /**
     * Additional metadata that may be needed for execution.
     * Service-specific parameters can be added here.
     */
    protected Map<String, Object> metadata;

    /**
     * Priority level for request processing.
     * Lower number = higher priority.
     */
    protected Integer priority = 5;

    /**
     * Expected timeout for request execution in seconds.
     */
    protected Integer timeoutSeconds = 30;

    /**
     * Default constructor for serialization frameworks.
     */
    protected AIRequest() {
        this.requestTimestamp = Instant.now();
    }

    /**
     * Gets the request ID, generating one if not set.
     *
     * @return The request ID
     */
    public String getRequestId() {
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }
        return requestId;
    }

    /**
     * Sets the request ID.
     */
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    /**
     * Gets the tenant ID.
     *
     * @return The tenant identifier
     */
    public String getTenantId() {
        return tenantId;
    }

    /**
     * Sets the tenant ID.
     */
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * Gets the correlation ID.
     */
    public String getCorrelationId() {
        return correlationId;
    }

    /**
     * Sets the correlation ID.
     */
    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    /**
     * Gets the user ID.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the user ID.
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets the request timestamp.
     */
    public Instant getRequestTimestamp() {
        return requestTimestamp;
    }

    /**
     * Sets the request timestamp.
     */
    public void setRequestTimestamp(Instant requestTimestamp) {
        this.requestTimestamp = requestTimestamp;
    }

    /**
     * Gets additional metadata.
     */
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    /**
     * Sets additional metadata.
     */
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    /**
     * Gets the priority level.
     */
    public Integer getPriority() {
        return priority;
    }

    /**
     * Sets the priority level.
     */
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    /**
     * Gets the timeout in seconds.
     */
    public Integer getTimeoutSeconds() {
        return timeoutSeconds;
    }

    /**
     * Sets the timeout in seconds.
     */
    public void setTimeoutSeconds(Integer timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }

    /**
     * Validates the request before execution.
     * Subclasses can override to add service-specific validation.
     *
     * @throws IllegalArgumentException if validation fails
     */
    public void validate() {
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalArgumentException("TenantId is required and cannot be blank");
        }
        if (timeoutSeconds != null && timeoutSeconds <= 0) {
            throw new IllegalArgumentException("TimeoutSeconds must be positive");
        }
        if (priority != null && (priority < 1 || priority > 10)) {
            throw new IllegalArgumentException("Priority must be between 1 and 10");
        }
    }

    /**
     * Gets the input data for this request.
     * Each service type must define its specific input.
     *
     * @return The input data
     */
    public abstract Object getInputData();

    /**
     * Creates a new builder instance.
     *
     * @param <T> The concrete request type
     * @return A new builder
     */
    public static <T extends AIRequest> Builder<T> builder() {
        return new Builder<>();
    }

    /**
     * Builder for AIRequest.
     */
    public static class Builder<T extends AIRequest> {
        private String requestId;
        private String tenantId;
        private String correlationId;
        private String userId;
        private Integer priority = 5;
        private Integer timeoutSeconds = 30;
        private Map<String, Object> metadata;

        public Builder<T> requestId(String requestId) {
            this.requestId = requestId;
            return this;
        }

        public Builder<T> tenantId(String tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        public Builder<T> correlationId(String correlationId) {
            this.correlationId = correlationId;
            return this;
        }

        public Builder<T> userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder<T> priority(Integer priority) {
            this.priority = priority;
            return this;
        }

        public Builder<T> timeoutSeconds(Integer timeoutSeconds) {
            this.timeoutSeconds = timeoutSeconds;
            return this;
        }

        public Builder<T> metadata(Map<String, Object> metadata) {
            this.metadata = metadata;
            return this;
        }
    }
}
