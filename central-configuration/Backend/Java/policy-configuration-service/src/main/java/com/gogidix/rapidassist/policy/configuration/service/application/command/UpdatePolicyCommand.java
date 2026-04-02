package com.gogidix.rapidassist.policy.configuration.service.application.command;

import com.gogidix.rapidassist.policy.configuration.service.domain.model.Policy;

import java.util.Map;
import java.util.Set;

/**
 * Command object for updating an existing Policy.
 *
 * <p>This is a CQRS command object that encapsulates all parameters
 * needed to update a policy entity.
 *
 * <p>Commands are immutable and self-validating.
 */
public record UpdatePolicyCommand(
    String tenantId,
    String policyId,
    String policyKey,
    String name,
    String description,
    Map<String, Object> rules,
    Policy.PolicyScope scope,
    Policy.PolicyConstraints constraints,
    Boolean enforced,
    Integer priority,
    Set<String> tags,
    String updatedBy,
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
            && updatedBy != null && !updatedBy.isBlank();
    }
}
