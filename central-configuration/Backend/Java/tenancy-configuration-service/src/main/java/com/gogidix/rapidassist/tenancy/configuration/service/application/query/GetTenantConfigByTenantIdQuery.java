package com.gogidix.rapidassist.tenancy.configuration.service.application.query;

/**
 * Query object for retrieving a tenant configuration by tenant ID.
 *
 * <p>This query object encapsulates the parameters needed to retrieve
 * a tenant configuration by its tenant ID.
 */
public record GetTenantConfigByTenantIdQuery(
    String tenantId
) {
    /**
     * Validates the query parameters.
     *
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return tenantId != null && !tenantId.isBlank();
    }
}
