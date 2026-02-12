package com.gogidix.rapidassist.ai.gateway.domain.tenant;

import java.util.Optional;

/**
 * Thread-local context for tenant ID.
 */
public class TenantContext {

    private static final ThreadLocal<String> TENANT_ID = new ThreadLocal<>();

    /**
     * Set the tenant ID for the current thread.
     */
    public static void setTenantId(String tenantId) {
        TENANT_ID.set(tenantId);
    }

    /**
     * Get the tenant ID for the current thread.
     */
    public static String getTenantId() {
        return TENANT_ID.get();
    }

    /**
     * Get the tenant ID as an Optional.
     */
    public static Optional<String> getTenantIdOptional() {
        return Optional.ofNullable(TENANT_ID.get());
    }

    /**
     * Clear the tenant ID for the current thread.
     */
    public static void clear() {
        TENANT_ID.remove();
    }
}
