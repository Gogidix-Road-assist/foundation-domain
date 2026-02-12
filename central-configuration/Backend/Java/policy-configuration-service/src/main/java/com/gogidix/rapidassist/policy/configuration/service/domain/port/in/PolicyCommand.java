package com.gogidix.rapidassist.policy.configuration.service.domain.port.in;

import com.gogidix.rapidassist.policy.configuration.service.domain.model.Policy;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Input port for policy command operations
 */
public interface PolicyCommand {

    CompletableFuture<Policy> createPolicy(CreatePolicyCommand command);
    CompletableFuture<Optional<Policy>> updatePolicy(String policyId, UpdatePolicyCommand command);
    CompletableFuture<Optional<Policy>> activatePolicy(String policyId, String updatedBy);
    CompletableFuture<Optional<Policy>> deactivatePolicy(String policyId, String updatedBy);
    CompletableFuture<Optional<Policy>> enforcePolicy(String policyId, boolean enforced, String updatedBy);
    CompletableFuture<Boolean> deletePolicy(String policyId);

    CompletableFuture<List<Policy>> bulkUpdatePolicies(BulkUpdateCommand command);

    record CreatePolicyCommand(
        String tenantId,
        String policyKey,
        String name,
        String description,
        Policy.PolicyType type,
        Policy.PolicyScope scope,
        java.util.Map<String, Object> rules,
        Policy.PolicyConstraints constraints,
        int priority,
        String environment,
        Set<String> tags,
        String createdBy
    ) {}

    record UpdatePolicyCommand(
        String name,
        String description,
        Policy.PolicyScope scope,
        java.util.Map<String, Object> rules,
        Policy.PolicyConstraints constraints,
        int priority,
        String updatedBy
    ) {}

    record BulkUpdateCommand(
        List<String> policyIds,
        boolean enforced,
        String updatedBy
    ) {}
}
