package com.gogidix.rapidassist.rate.limit.policy.service.application.command;

import com.gogidix.rapidassist.rate.limit.policy.service.domain.model.RateLimitPolicy;

/**
 * Command object for updating an existing Rate Limit Policy.
 *
 * <p>This is a CQRS command object that encapsulates all parameters
 * needed to update a rate limit policy entity.
 *
 * <p>Commands are immutable and self-validating.
 */
public record UpdateRateLimitPolicyCommand(
    String policyId,
    String name,
    String description,
    RateLimitPolicy.RateLimitConfig config,
    RateLimitPolicy.Scope scope,
    String updatedBy,
    String reason
) {
    /**
     * Validates the command parameters.
     *
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return policyId != null && !policyId.isBlank()
            && updatedBy != null && !updatedBy.isBlank();
    }

    /**
     * Creates a validated command with defaults applied.
     *
     * @return a new UpdateRateLimitPolicyCommand with defaults
     */
    public UpdateRateLimitPolicyCommand withDefaults() {
        return new UpdateRateLimitPolicyCommand(
            this.policyId,
            this.name,
            this.description,
            this.config,
            this.scope,
            this.updatedBy,
            this.reason != null ? this.reason : "Policy update"
        );
    }
}
