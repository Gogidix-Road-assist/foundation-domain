package com.gogidix.rapidassist.ai.summization.domain.tenant;

/**
 * Thread-local context for storing tenant information.
 * Used for multi-tenancy support across the application.
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
     * Clear the tenant ID for the current thread.
     */
    public static void clear() {
        TENANT_ID.remove();
    }
}
