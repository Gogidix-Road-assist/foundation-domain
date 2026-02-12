package com.gogidix.rapidassist.policy.configuration.service.application.command;

import com.gogidix.rapidassist.policy.configuration.service.domain.model.Policy;

import java.util.Map;
import java.util.Set;

/**
 * Command object for creating a new Policy.
 *
 * <p>This is a CQRS command object that encapsulates all parameters
 * needed to create a new policy entity.
 *
 * <p>Commands are immutable and self-validating.
 */
public record CreatePolicyCommand(
    String tenantId,
    String policyKey,
    String name,
    String description,
    Policy.PolicyType type,
    Map<String, Object> rules,
    Policy.PolicyScope scope,
    Policy.PolicyConstraints constraints,
    boolean enforced,
    int priority,
    String environment,
    Set<String> tags,
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
            && type != null
            && rules != null && !rules.isEmpty()
            && scope != null
            && environment != null && !environment.isBlank()
            && createdBy != null && !createdBy.isBlank();
    }

    /**
     * Creates a validated command with defaults applied.
     *
     * @return a new CreatePolicyCommand with defaults
     */
    public CreatePolicyCommand withDefaults() {
        return new CreatePolicyCommand(
            this.tenantId,
            this.policyKey,
            this.name,
            this.description != null ? this.description : "",
            this.type,
            this.rules,
            this.scope,
            this.constraints != null ? this.constraints : Policy.PolicyConstraints.defaults(),
            this.enforced,
            this.priority,
            this.environment,
            this.tags != null ? this.tags : Set.of(),
            this.createdBy,
            this.reason != null ? this.reason : "Initial policy creation"
        );
    }
}
