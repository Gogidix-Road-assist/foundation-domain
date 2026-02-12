package com.gogidix.rapidassist.ai.anomaly.infrastructure.tenant;

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
public class RequestContext {

    private String tenantId;
    private String userId;
    private String correlationId;
    private Map<String, Object> metadata;

    public Optional<String> getUserId() {
        return Optional.ofNullable(userId);
    }

    public Optional<String> getCorrelationId() {
        return Optional.ofNullable(correlationId);
    }

    public static RequestContextBuilder builder() {
        return new RequestContextBuilder();
    }

    public static RequestContextBuilder builder(RequestContext existing) {
        return new RequestContextBuilder()
            .tenantId(existing.getTenantId())
            .userId(existing.getUserId().orElse(null))
            .correlationId(existing.getCorrelationId().orElse(null))
            .metadata(existing.getMetadata());
    }
}
