package com.gogidix.rapidassist.ai.imagerecognition.infrastructure.tenant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Optional;

/**
 * ThreadLocal holder for tenant context.
 * MUST be populated by TenantInterceptor BEFORE any service logic.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantContext {

    private String tenantId;
    private String userId;
    private String correlationId;
    @Builder.Default
    private Map<String, Object> metadata = Map.of();

    public String getTenantId() {
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalStateException("TenantId is required but not set");
        }
        return tenantId;
    }

    public Optional<String> getUserId() {
        return Optional.ofNullable(userId);
    }

    public String getCorrelationId() {
        if (correlationId == null || correlationId.isBlank()) {
            throw new IllegalStateException("CorrelationId is required but not set");
        }
        return correlationId;
    }

    public static TenantContextBuilder builder() {
        return new TenantContextBuilder();
    }

    public static TenantContextBuilder builder(TenantContext existing) {
        return new TenantContextBuilder()
                .tenantId(existing.tenantId)
                .userId(existing.userId)
                .correlationId(existing.correlationId)
                .metadata(existing.metadata);
    }

    public static class TenantContextBuilder {
        private String tenantId;
        private String userId;
        private String correlationId;
        private Map<String, Object> metadata = Map.of();

        public TenantContextBuilder tenantId(String tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        public TenantContextBuilder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public TenantContextBuilder correlationId(String correlationId) {
            this.correlationId = correlationId;
            return this;
        }

        public TenantContextBuilder metadata(Map<String, Object> metadata) {
            this.metadata = metadata != null ? Map.copyOf(metadata) : Map.of();
            return this;
        }

        public TenantContext build() {
            return new TenantContext(tenantId, userId, correlationId, metadata);
        }
    }
}
