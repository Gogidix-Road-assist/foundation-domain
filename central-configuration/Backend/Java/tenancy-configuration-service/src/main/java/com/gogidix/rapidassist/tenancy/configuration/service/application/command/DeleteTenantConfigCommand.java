package com.gogidix.rapidassist.tenancy.configuration.service.application.command;

/**
 * Command object for deleting a Tenant Configuration.
 *
 * <p>This is a CQRS command object that encapsulates all parameters
 * needed to delete a tenant configuration entity.
 *
 * <p>Commands are immutable and self-validating.
 */
public record DeleteTenantConfigCommand(
    String id,
    String deletedBy,
    String reason
) {
    /**
     * Validates the command parameters.
     *
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return id != null && !id.isBlank()
            && deletedBy != null && !deletedBy.isBlank()
            && reason != null && !reason.isBlank();
    }
}
