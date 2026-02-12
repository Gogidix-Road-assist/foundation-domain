package com.gogidix.rapidassist.ai.chatbot.infrastructure.tenant;

import lombok.Builder;

import java.util.Map;

/**
 * ThreadLocal holder for tenant context
 * MUST be populated by TenantInterceptor BEFORE any service logic
 */
@Builder
public record RequestContext(

    /**
     * Tenant ID - MANDATORY for multi-tenancy
     */
    String tenantId,

    /**
     * User ID from JWT token
     */
    String userId,

    /**
     * Correlation ID for distributed tracing
     */
    String correlationId,

    /**
     * Additional metadata for the request
     */
    Map<String, Object> metadata
) {

    public RequestContext {
        // Validation in compact constructor
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalArgumentException("tenantId is required");
        }
    }

    /**
     * Builder with default values
     */
    public static RequestContextBuilder builder() {
        return new RequestContextBuilder();
    }

    /**
     * Builder with defaults
     */
    public static class RequestContextBuilder {
        private Map<String, Object> metadata = Map.of();

        public RequestContextBuilder metadata(Map<String, Object> metadata) {
            this.metadata = metadata != null ? Map.copyOf(metadata) : Map.of();
            return this;
        }
    }
}
