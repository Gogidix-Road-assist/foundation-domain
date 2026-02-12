package com.gogidix.rapidassist.config.service.application.command;

/**
 * Command object for updating an existing Configuration.
 *
 * <p>This command encapsulates the parameters needed to update
 * a configuration's value while maintaining audit trail.
 */
public record UpdateConfigurationCommand(
    String tenantId,
    String configKey,
    String environment,
    String namespace,
    Object newValue,
    String updatedBy,
    String reason,
    boolean forceUpdate
) {
    /**
     * Validates the command parameters.
     *
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return tenantId != null && !tenantId.isBlank()
            && configKey != null && !configKey.isBlank()
            && environment != null && !environment.isBlank()
            && namespace != null && !namespace.isBlank()
            && updatedBy != null && !updatedBy.isBlank();
    }
}
