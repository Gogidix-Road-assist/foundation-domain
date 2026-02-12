package com.gogidix.rapidassist.shared.ai.contracts.application;

import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContext;

import java.util.Optional;

/**
 * Extended execution context for AI operations.
 * Builds on the base RequestContext to add AI-specific information
 * including model version, confidence thresholds, and execution parameters.
 *
 * This context is propagated throughout the AI execution chain to ensure
 * consistent execution parameters and observability.
 */
public class AIExecutionContext {

    private final RequestContext requestContext;
    private String modelVersion;
    private Double confidenceThreshold;
    private Integer timeoutSeconds;
    private Integer maxRetries;
    private Boolean enableLogging;
    private Boolean enableMetrics;
    private String traceId;
    private String spanId;

    /**
     * Creates a new AI execution context from a base request context.
     *
     * @param requestContext The base request context
     */
    private AIExecutionContext(RequestContext requestContext) {
        this.requestContext = requestContext;
        this.timeoutSeconds = 30;
        this.maxRetries = 3;
        this.enableLogging = true;
        this.enableMetrics = true;
    }

    /**
     * Creates an AI execution context from a request context.
     *
     * @param requestContext The base request context
     * @return A new AI execution context
     */
    public static AIExecutionContext from(RequestContext requestContext) {
        if (requestContext == null) {
            throw new IllegalArgumentException("RequestContext cannot be null");
        }
        requestContext.validate();
        return new AIExecutionContext(requestContext);
    }

    /**
     * Creates a builder for AI execution context.
     *
     * @param requestContext The base request context
     * @return A new builder
     */
    public static Builder builder(RequestContext requestContext) {
        return new Builder(requestContext);
    }

    /**
     * Gets the tenant ID from the underlying request context.
     *
     * @return The tenant ID
     */
    public String getTenantId() {
        return requestContext.tenantId();
    }

    /**
     * Gets the request ID from the underlying request context.
     *
     * @return The request ID
     */
    public String getRequestId() {
        return requestContext.requestId();
    }

    /**
     * Gets the correlation ID from the underlying request context.
     *
     * @return The correlation ID
     */
    public String getCorrelationId() {
        return requestContext.correlationId();
    }

    /**
     * Gets the user ID from the underlying request context.
     *
     * @return The user ID
     */
    public String getUserId() {
        return requestContext.userId();
    }

    /**
     * Gets the underlying request context.
     *
     * @return The request context
     */
    public RequestContext getRequestContext() {
        return requestContext;
    }

    // AI-specific properties

    public String getModelVersion() {
        return modelVersion;
    }

    public Double getConfidenceThreshold() {
        return confidenceThreshold;
    }

    public Integer getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public Integer getMaxRetries() {
        return maxRetries;
    }

    public Boolean isLoggingEnabled() {
        return enableLogging;
    }

    public Boolean isMetricsEnabled() {
        return enableMetrics;
    }

    public String getTraceId() {
        return traceId;
    }

    public String getSpanId() {
        return spanId;
    }

    /**
     * Creates a new builder with this context's values.
     *
     * @return A new builder
     */
    public Builder toBuilder() {
        return new Builder(requestContext)
                .modelVersion(this.modelVersion)
                .confidenceThreshold(this.confidenceThreshold)
                .timeoutSeconds(this.timeoutSeconds)
                .maxRetries(this.maxRetries)
                .enableLogging(this.enableLogging)
                .enableMetrics(this.enableMetrics)
                .traceId(this.traceId)
                .spanId(this.spanId);
    }

    /**
     * Builder for AIExecutionContext.
     */
    public static class Builder {
        private final AIExecutionContext context;

        private Builder(RequestContext requestContext) {
            this.context = new AIExecutionContext(requestContext);
        }

        public Builder modelVersion(String modelVersion) {
            context.modelVersion = modelVersion;
            return this;
        }

        public Builder confidenceThreshold(Double confidenceThreshold) {
            context.confidenceThreshold = confidenceThreshold;
            return this;
        }

        public Builder timeoutSeconds(Integer timeoutSeconds) {
            context.timeoutSeconds = timeoutSeconds;
            return this;
        }

        public Builder maxRetries(Integer maxRetries) {
            context.maxRetries = maxRetries;
            return this;
        }

        public Builder enableLogging(Boolean enableLogging) {
            context.enableLogging = enableLogging;
            return this;
        }

        public Builder enableMetrics(Boolean enableMetrics) {
            context.enableMetrics = enableMetrics;
            return this;
        }

        public Builder traceId(String traceId) {
            context.traceId = traceId;
            return this;
        }

        public Builder spanId(String spanId) {
            context.spanId = spanId;
            return this;
        }

        public AIExecutionContext build() {
            return context;
        }
    }
}
