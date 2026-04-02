package com.gogidix.rapidassist.ai.categorization.infrastructure.tenant;

import java.util.Map;
import java.util.Optional;

/**
 * ThreadLocal holder for tenant context.
 * MUST be populated by TenantInterceptor BEFORE any service logic.
 *
 * This is a CRITICAL component for multi-tenancy.
 * All database operations MUST filter by tenantId from this context.
 */
public final class TenantContext {

    private final String tenantId;
    private final String userId;
    private final String correlationId;
    private final Map<String, Object> metadata;

    private TenantContext(Builder builder) {
        this.tenantId = builder.tenantId;
        this.userId = builder.userId;
        this.correlationId = builder.correlationId != null ? builder.correlationId : java.util.UUID.randomUUID().toString();
        this.metadata = builder.metadata != null ? Map.copyOf(builder.metadata) : Map.of();
    }

    public String tenantId() {
        return tenantId;
    }

    public Optional<String> userId() {
        return Optional.ofNullable(userId);
    }

    public String correlationId() {
        return correlationId;
    }

    public Map<String, Object> metadata() {
        return metadata;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(TenantContext existing) {
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

        public TenantContext build() {
            if (tenantId == null || tenantId.isBlank()) {
                throw new IllegalArgumentException("tenantId is required");
            }
            return new TenantContext(this);
        }
    }

    @Override
    public String toString() {
        return "TenantContext{" +
                "tenantId='" + tenantId + '\'' +
                ", userId='" + userId + '\'' +
                ", correlationId='" + correlationId + '\'' +
                '}';
    }
}
