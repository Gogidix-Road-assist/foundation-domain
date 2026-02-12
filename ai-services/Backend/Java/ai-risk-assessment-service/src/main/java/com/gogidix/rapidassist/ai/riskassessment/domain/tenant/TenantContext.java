package com.gogidix.rapidassist.ai.riskassessment.domain.tenant;

/**
 * Tenant Context Holder
 * Manages tenant context for multi-tenancy support using ThreadLocal
 */
public class TenantContext {

    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();

    private TenantContext() {
        // Private constructor to prevent instantiation
    }

    /**
     * Set the current tenant ID
     */
    public static void setTenantId(String tenantId) {
        CURRENT_TENANT.set(tenantId);
    }

    /**
     * Get the current tenant ID
     */
    public static String getTenantId() {
        String tenantId = CURRENT_TENANT.get();
        if (tenantId == null) {
            throw new IllegalStateException("Tenant ID not set in current context");
        }
        return tenantId;
    }

    /**
     * Check if tenant ID is set
     */
    public static boolean isTenantSet() {
        return CURRENT_TENANT.get() != null;
    }

    /**
     * Clear the current tenant context
     */
    public static void clear() {
        CURRENT_TENANT.remove();
    }
}
