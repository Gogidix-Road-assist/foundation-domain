package com.gogidix.rapidassist.rate.limit.policy.service.application.command;

/**
 * Command object for deleting a Rate Limit Policy.
 *
 * <p>This is a CQRS command object that encapsulates all parameters
 * needed to delete a rate limit policy entity.
 *
 * <p>Commands are immutable and self-validating.
 */
public record DeleteRateLimitPolicyCommand(
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
        return policyId != null && !policyId.isBlank()
            && deletedBy != null && !deletedBy.isBlank();
    }

    /**
     * Creates a validated command with defaults applied.
     *
     * @return a new DeleteRateLimitPolicyCommand with defaults
     */
    public DeleteRateLimitPolicyCommand withDefaults() {
        return new DeleteRateLimitPolicyCommand(
            this.policyId,
            this.deletedBy,
            this.reason != null ? this.reason : "Policy deletion"
        );
    }
}
