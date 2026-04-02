package com.gogidix.rapidassist.rate.limit.policy.service.application.command;

import com.gogidix.rapidassist.rate.limit.policy.service.domain.model.RateLimitPolicy;

/**
 * Command object for creating a new Rate Limit Policy.
 *
 * <p>This is a CQRS command object that encapsulates all parameters
 * needed to create a new rate limit policy entity.
 *
 * <p>Commands are immutable and self-validating.
 */
public record CreateRateLimitPolicyCommand(
    String tenantId,
    String policyKey,
    String name,
    String description,
    RateLimitPolicy.LimitType limitType,
    RateLimitPolicy.RateLimitConfig config,
    RateLimitPolicy.Scope scope,
    boolean enabled,
    String environment,
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
            && policyKey != null && !policyKey.isBlank()
            && name != null && !name.isBlank()
            && limitType != null
            && config != null
            && scope != null
            && environment != null && !environment.isBlank()
            && createdBy != null && !createdBy.isBlank();
    }

    /**
     * Creates a validated command with defaults applied.
     *
     * @return a new CreateRateLimitPolicyCommand with defaults
     */
    public CreateRateLimitPolicyCommand withDefaults() {
        return new CreateRateLimitPolicyCommand(
            this.tenantId,
            this.policyKey,
            this.name,
            this.description != null ? this.description : "",
            this.limitType,
            this.config,
            this.scope,
            this.enabled,
            this.environment,
            this.createdBy,
            this.reason != null ? this.reason : "Initial policy creation"
        );
    }
}
