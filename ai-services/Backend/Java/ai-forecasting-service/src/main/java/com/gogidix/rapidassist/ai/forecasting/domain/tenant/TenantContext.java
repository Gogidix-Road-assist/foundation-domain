package com.gogidix.rapidassist.ai.forecasting.domain.tenant;

import java.util.Optional;

/**
 * Thread-local context for managing tenant ID across the application.
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
     * @return Optional containing the tenant ID if present
     */
    public static Optional<String> getTenantId() {
        return Optional.ofNullable(CURRENT_TENANT.get());
    }

    /**
     * Get the current tenant ID or throw an exception if not set.
     *
     * @return The tenant ID
     * @throws IllegalStateException if tenant ID is not set
     */
    public static String getRequiredTenantId() {
        return getTenantId()
                .orElseThrow(() -> new IllegalStateException("Tenant ID not set in current context"));
    }

    /**
     * Clear the current tenant ID for this thread.
     */
    public static void clear() {
        CURRENT_TENANT.remove();
    }
}
