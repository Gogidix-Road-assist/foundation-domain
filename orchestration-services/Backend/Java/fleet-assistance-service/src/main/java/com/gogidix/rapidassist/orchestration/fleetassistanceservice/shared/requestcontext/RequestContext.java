package com.gogidix.rapidassist.orchestration.fleetassistanceservice.shared.requestcontext;

import java.util.Map;
import java.util.Objects;

/**
 * ThreadLocal holder for tenant context
 * MUST be populated by TenantInterceptor BEFORE any service logic
 */
public final class RequestContext {

    private final String tenantId;
    private final String userId;
    private final String correlationId;
    private final Map<String, Object> metadata;

    private RequestContext(Builder builder) {
        this.tenantId = Objects.requireNonNull(builder.tenantId, "tenantId is required");
        this.userId = builder.userId;
        this.correlationId = builder.correlationId != null ? builder.correlationId : java.util.UUID.randomUUID().toString();
        this.metadata = builder.metadata != null ? Map.copyOf(builder.metadata) : Map.of();
    }

    public String tenantId() { return tenantId; }
    public String userId() { return userId; }
    public String correlationId() { return correlationId; }
    public Map<String, Object> metadata() { return metadata; }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(RequestContext existing) {
        return new Builder()
            .tenantId(existing.tenantId)
            .userId(existing.userId)
            .correlationId(existing.correlationId)
            .metadata(existing.metadata);
    }

    public static class Builder {
        private String tenantId;
        private String userId;
        private String correlationId;
        private Map<String, Object> metadata;

        public Builder tenantId(String tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder correlationId(String correlationId) {
            this.correlationId = correlationId;
            return this;
        }

        public Builder metadata(Map<String, Object> metadata) {
            this.metadata = metadata;
            return this;
        }

        public RequestContext build() {
            return new RequestContext(this);
        }
    }
}
