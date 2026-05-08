package com.gogidix.rapidassist.tenancy.configuration.service.application.command;

import com.gogidix.rapidassist.tenancy.configuration.service.domain.model.TenantConfig;

/**
 * Command object for updating an existing Tenant Configuration.
 *
 * <p>This is a CQRS command object that encapsulates all parameters
 * needed to update a tenant configuration entity.
 *
 * <p>Commands are immutable and self-validating.
 */
public record UpdateTenantConfigCommand(
    String id,
    String name,
    String domain,
    String environment,
    TenantConfig.TenantSettings settings,
    TenantConfig.TenantLimits limits,
    TenantConfig.TenantFeatures features,
    Boolean active,
    String updatedBy,
    String reason
) {
    /**
     * Validates the command parameters.
     *
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return id != null && !id.isBlank()
            && updatedBy != null && !updatedBy.isBlank();
    }
}
