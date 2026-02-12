package com.gogidix.rapidassist.config.service.application.query;

/**
 * Query object for retrieving a single configuration by its natural key.
 *
 * <p>This query object encapsulates the parameters needed to retrieve
 * a specific configuration for a tenant.
 */
public record GetConfigurationQuery(
    String tenantId,
    String configKey,
    String environment,
    String namespace
) {
    /**
     * Validates the query parameters.
     *
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return tenantId != null && !tenantId.isBlank()
            && configKey != null && !configKey.isBlank()
            && environment != null && !environment.isBlank()
            && namespace != null && !namespace.isBlank();
    }
}
