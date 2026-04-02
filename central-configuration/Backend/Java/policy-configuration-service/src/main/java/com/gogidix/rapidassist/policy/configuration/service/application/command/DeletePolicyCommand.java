package com.gogidix.rapidassist.policy.configuration.service.application.command;

/**
 * Command object for deleting a Policy.
 *
 * <p>This is a CQRS command object that encapsulates all parameters
 * needed to delete a policy entity.
 *
 * <p>Commands are immutable and self-validating.
 */
public record DeletePolicyCommand(
    String tenantId,
    String policyId,
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
            && policyId != null && !policyId.isBlank()
            && deletedBy != null && !deletedBy.isBlank()
            && reason != null && !reason.isBlank();
    }
}
