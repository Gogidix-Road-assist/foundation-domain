package com.gogidix.rapidassist.tenancy.configuration.service.application.command;

import com.gogidix.rapidassist.tenancy.configuration.service.domain.model.TenantConfig;

import java.util.Set;

/**
 * Command object for creating a new Tenant Configuration.
 *
 * <p>This is a CQRS command object that encapsulates all parameters
 * needed to create a new tenant configuration entity.
 *
 * <p>Commands are immutable and self-validating.
 */
public record CreateTenantConfigCommand(
    String tenantId,
    String name,
    String domain,
    String environment,
    TenantConfig.TenantSettings settings,
    TenantConfig.TenantLimits limits,
    TenantConfig.TenantFeatures features,
    boolean active,
    String createdBy,
    String reason
) {
    /**
     * Validates the command parameters.
     *
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return tenantId != null && !tenantId.isBlank()
            && name != null && !name.isBlank()
            && domain != null && !domain.isBlank()
            && environment != null && !environment.isBlank()
            && createdBy != null && !createdBy.isBlank();
    }

    /**
     * Creates a validated command with defaults applied.
     *
     * @return a new CreateTenantConfigCommand with defaults
     */
    public CreateTenantConfigCommand withDefaults() {
        return new CreateTenantConfigCommand(
            this.tenantId,
            this.name,
            this.domain,
            this.environment,
            this.settings != null ? this.settings : TenantConfig.TenantSettings.defaults(),
            this.limits != null ? this.limits : TenantConfig.TenantLimits.defaults(),
            this.features != null ? this.features : TenantConfig.TenantFeatures.defaults(),
            this.active,
            this.createdBy,
            this.reason != null ? this.reason : "Initial tenant configuration creation"
        );
    }
}
