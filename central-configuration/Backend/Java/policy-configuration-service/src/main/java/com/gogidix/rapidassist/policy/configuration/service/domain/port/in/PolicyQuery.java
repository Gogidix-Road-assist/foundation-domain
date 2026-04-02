package com.gogidix.rapidassist.policy.configuration.service.domain.port.in;

import com.gogidix.rapidassist.policy.configuration.service.domain.model.Policy;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Input port for policy query operations
 */
public interface PolicyQuery {

    CompletableFuture<Optional<Policy>> getPolicyById(String policyId);
    CompletableFuture<Optional<Policy>> getPolicyByKey(String tenantId, String policyKey);
    CompletableFuture<List<Policy>> getPoliciesByTenant(String tenantId);
    CompletableFuture<List<Policy>> getPoliciesByType(String tenantId, Policy.PolicyType type);
    CompletableFuture<List<Policy>> getActivePolicies(String tenantId, String environment);
    CompletableFuture<List<Policy>> getEnforcedPolicies(String tenantId);
    CompletableFuture<List<Policy>> getPoliciesByTags(String tenantId, Set<String> tags);

    CompletableFuture<List<Policy>> findApplicablePolicies(String tenantId, String entityType,
                                                           String entityId, String environment);
    CompletableFuture<Optional<Policy>> findHighestPriorityPolicy(String tenantId, String entityType,
                                                                   String entityId, String environment);
}
