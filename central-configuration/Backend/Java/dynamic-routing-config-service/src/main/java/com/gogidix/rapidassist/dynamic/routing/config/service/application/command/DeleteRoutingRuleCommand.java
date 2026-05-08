package com.gogidix.rapidassist.dynamic.routing.config.service.application.command;

/**
 * Command object for deleting an existing Routing Rule.
 *
 * <p>This is a CQRS command object that encapsulates all parameters
 * needed to delete an existing routing rule entity.
 *
 * <p>Commands are immutable and self-validating.
 */
public record DeleteRoutingRuleCommand(
    String id,
    String tenantId,
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
            && tenantId != null && !tenantId.isBlank()
            && deletedBy != null && !deletedBy.isBlank()
            && reason != null && !reason.isBlank();
    }
}
