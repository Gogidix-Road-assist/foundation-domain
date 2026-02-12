package com.gogidix.rapidassist.ai.analytics.infrastructure.tenant;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * Thread-local context for tenant isolation
 * Holds the current tenant ID and user information for the request scope
 */
@Getter
@Setter
public class TenantContext {

    private String tenantId;
    private String userId;
    private String correlationId;

    public TenantContext() {
    }

    public TenantContext(String tenantId, String userId, String correlationId) {
        this.tenantId = tenantId;
        this.userId = userId;
        this.correlationId = correlationId;
    }

    public void clear() {
        this.tenantId = null;
        this.userId = null;
        this.correlationId = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TenantContext that = (TenantContext) o;
        return Objects.equals(tenantId, that.tenantId) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(correlationId, that.correlationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tenantId, userId, correlationId);
    }
}
