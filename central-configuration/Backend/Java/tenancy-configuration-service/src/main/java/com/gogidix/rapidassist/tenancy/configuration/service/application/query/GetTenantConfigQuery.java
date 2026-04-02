package com.gogidix.rapidassist.tenancy.configuration.service.application.query;

/**
 * Query object for retrieving a single tenant configuration by its ID.
 *
 * <p>This query object encapsulates the parameters needed to retrieve
 * a specific tenant configuration.
 */
public record GetTenantConfigQuery(
    String id
) {
    /**
     * Validates the query parameters.
     *
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return id != null && !id.isBlank();
    }
}
