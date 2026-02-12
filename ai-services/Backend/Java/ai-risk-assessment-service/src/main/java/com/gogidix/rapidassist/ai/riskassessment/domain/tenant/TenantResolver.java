package com.gogidix.rapidassist.ai.riskassessment.domain.tenant;

/**
 * Tenant Resolver Interface
 * Defines contract for resolving tenant information
 */
public interface TenantResolver {

    /**
     * Resolve tenant ID from request context
     */
    String resolveTenantId();

    /**
     * Validate tenant access
     */
    boolean validateTenantAccess(String tenantId);

    /**
     * Get tenant-specific configuration
     */
    Object getTenantConfig(String tenantId, String configKey);
}
