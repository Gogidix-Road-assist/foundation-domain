package com.gogidix.rapidassist.ai.sentiment.domain.tenant;

/**
 * Thread-local context for tenant ID
 */
public class TenantContext {

    private static final ThreadLocal<String> TENANT_ID = new ThreadLocal<>();

    /**
     * Set tenant ID for current thread
     */
    public static void setTenantId(String tenantId) {
        TENANT_ID.set(tenantId);
    }

    /**
     * Get tenant ID for current thread
     */
    public static String getTenantId() {
        return TENANT_ID.get();
    }

    /**
     * Clear tenant ID for current thread
     */
    public static void clear() {
        TENANT_ID.remove();
    }
}
