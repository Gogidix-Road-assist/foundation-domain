package com.gogidix.rapidassist.ai.pricing.domain.tenant;

import java.util.Optional;

/**
 * Thread-local context for tenant information.
 */
public class TenantContext {

    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();

    /**
     * Set the current tenant ID.
     */
    public static void setTenantId(String tenantId) {
        CURRENT_TENANT.set(tenantId);
    }

    /**
     * Get the current tenant ID.
     */
    public static String getTenantId() {
        return CURRENT_TENANT.get();
    }

    /**
     * Get the current tenant ID as Optional.
     */
    public static Optional<String> getOptionalTenantId() {
        return Optional.ofNullable(CURRENT_TENANT.get());
    }

    /**
     * Clear the current tenant ID.
     */
    public static void clear() {
        CURRENT_TENANT.remove();
    }
}
