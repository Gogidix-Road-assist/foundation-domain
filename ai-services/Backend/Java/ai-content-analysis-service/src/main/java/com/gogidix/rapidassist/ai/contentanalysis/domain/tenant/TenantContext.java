package com.gogidix.rapidassist.ai.contentanalysis.domain.tenant;

/**
 * Thread-local context for managing tenant ID throughout the request lifecycle.
 * Ensures proper tenant isolation in multi-tenant architecture.
 */
public class TenantContext {

    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();

    /**
     * Set the current tenant ID for this thread.
     *
     * @param tenantId The tenant ID to set
     */
    public static void setTenantId(String tenantId) {
        CURRENT_TENANT.set(tenantId);
    }

    /**
     * Get the current tenant ID for this thread.
     *
     * @return The tenant ID, or null if not set
     */
    public static String getTenantId() {
        return CURRENT_TENANT.get();
    }

    /**
     * Clear the tenant ID for this thread.
     * Should be called at the end of request processing.
     */
    public static void clear() {
        CURRENT_TENANT.remove();
    }
}
