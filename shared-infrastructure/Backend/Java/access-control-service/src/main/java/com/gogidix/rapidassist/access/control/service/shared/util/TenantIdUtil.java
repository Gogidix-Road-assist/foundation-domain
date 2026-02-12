package com.gogidix.rapidassist.access.control.service.shared.util;

import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;

/**
 * Utility: TenantIdUtil
 *
 * Helper class for tenant ID operations.
 */
public class TenantIdUtil {

    /**
     * Get the current tenant ID from RequestContext.
     * Throws exception if context is not set.
     */
    public static String getCurrentTenantId() {
        return RequestContextHolder.get()
                .orElseThrow(() -> new IllegalStateException("RequestContext not set"))
                .tenantId();
    }

    /**
     * Get the current tenant ID from RequestContext.
     * Returns default value if context is not set.
     */
    public static String getCurrentTenantIdOrDefault(String defaultValue) {
        return RequestContextHolder.get()
                .map(ctx -> ctx.tenantId())
                .orElse(defaultValue);
    }

    /**
     * Validate that two tenant IDs match.
     * Throws exception if they don't match.
     */
    public static void validateTenantMatch(String tenantId1, String tenantId2) {
        if (tenantId1 == null || tenantId2 == null || !tenantId1.equals(tenantId2)) {
            throw new IllegalStateException("Tenant ID mismatch: " + tenantId1 + " != " + tenantId2);
        }
    }

    /**
     * Check if a tenant ID is valid (not null or empty).
     */
    public static boolean isValidTenantId(String tenantId) {
        return tenantId != null && !tenantId.isBlank();
    }
}
