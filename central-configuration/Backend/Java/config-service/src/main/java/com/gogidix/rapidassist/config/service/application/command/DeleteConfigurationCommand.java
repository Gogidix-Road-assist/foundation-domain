package com.gogidix.rapidassist.config.service.application.command;

/**
 * Command object for deleting a Configuration.
 *
 * <p>This command requires explicit confirmation via the reason field
 * to prevent accidental deletions.
 */
public record DeleteConfigurationCommand(
    String tenantId,
    String configKey,
    String environment,
    String namespace,
    String deletedBy,
    String reason
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
            && deletedBy != null && !deletedBy.isBlank()
            && reason != null && !reason.isBlank();
    }
}
