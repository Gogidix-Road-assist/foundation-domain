package com.gogidix.rapidassist.policy.configuration.service.domain.port.out;

import com.gogidix.rapidassist.policy.configuration.service.domain.model.Policy;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Output port for persisting and retrieving Policy entities.
 *
 * <p>This is a secondary port (outbound) in the hexagonal architecture.
 * The domain layer depends on this interface, and the infrastructure layer
 * provides the implementation.
 *
 * <p>All operations are tenant-scoped for multi-tenancy isolation.
 */
public interface PolicyRepository {

    // ========================================================================
    // CRUD Operations
    // ========================================================================

    /**
     * Saves a policy.
     *
     * @param policy the policy to save
     * @return CompletableFuture containing the saved policy
     */
    CompletableFuture<Policy> save(Policy policy);

    /**
     * Finds a policy by ID.
     *
     * @param policyId the policy ID
     * @return CompletableFuture containing the policy if found
     */
    CompletableFuture<Optional<Policy>> findById(String policyId);

    /**
     * Finds a policy by tenant and policy key.
     *
     * @param tenantId the tenant ID
     * @param policyKey the policy key
     * @return CompletableFuture containing the policy if found
     */
    CompletableFuture<Optional<Policy>> findByKey(String tenantId, String policyKey);

    /**
     * Finds all policies for a tenant.
     *
     * @param tenantId the tenant ID
     * @return CompletableFuture containing list of policies
     */
    CompletableFuture<List<Policy>> findByTenant(String tenantId);

    /**
     * Finds policies by tenant and type.
     *
     * @param tenantId the tenant ID
     * @param type the policy type
     * @return CompletableFuture containing list of policies
     */
    CompletableFuture<List<Policy>> findByType(String tenantId, Policy.PolicyType type);

    /**
     * Finds active policies for a tenant and environment.
     *
     * @param tenantId the tenant ID
     * @param environment the environment
     * @return CompletableFuture containing list of active policies
     */
    CompletableFuture<List<Policy>> findActive(String tenantId, String environment);

    /**
     * Finds enforced policies for a tenant.
     *
     * @param tenantId the tenant ID
     * @return CompletableFuture containing list of enforced policies
     */
    CompletableFuture<List<Policy>> findEnforced(String tenantId);

    /**
     * Finds policies by tags.
     *
     * @param tenantId the tenant ID
     * @param tags the tags to search for
     * @return CompletableFuture containing list of policies
     */
    CompletableFuture<List<Policy>> findByTags(String tenantId, Set<String> tags);

    /**
     * Deletes a policy by ID.
     *
     * @param policyId the policy ID
     * @return CompletableFuture containing true if deleted
     */
    CompletableFuture<Boolean> deleteById(String policyId);

    // ========================================================================
    // Cache Operations
    // ========================================================================

    /**
     * Caches a policy.
     *
     * @param policy the policy to cache
     * @return CompletableFuture completion signal
     */
    CompletableFuture<Void> cachePolicy(Policy policy);

    /**
     * Gets a cached policy.
     *
     * @param policyId the policy ID
     * @return CompletableFuture containing the cached policy if found
     */
    CompletableFuture<Optional<Policy>> getCachedPolicy(String policyId);

    /**
     * Evicts a policy from cache.
     *
     * @param policyId the policy ID
     * @return CompletableFuture completion signal
     */
    CompletableFuture<Void> evictPolicy(String policyId);

    /**
     * Evicts all cached policies.
     *
     * @param tenantId the tenant ID
     * @return CompletableFuture completion signal
     */
    CompletableFuture<Void> evictAll(String tenantId);
}
