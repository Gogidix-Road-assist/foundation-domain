package com.gogidix.rapidassist.ai.recommendation.domain.tenant;

import java.util.Optional;

/**
 * Thread-local context for tenant ID.
 */
public class TenantContext {

    private static final ThreadLocal<String> TENANT_ID = new ThreadLocal<>();

    public static void setTenantId(String tenantId) {
        TENANT_ID.set(tenantId);
    }

    public static String getTenantId() {
        return TENANT_ID.get();
    }

    public static Optional<String> getTenantIdOptional() {
        return Optional.ofNullable(TENANT_ID.get());
    }

    public static void clear() {
        TENANT_ID.remove();
    }
}
