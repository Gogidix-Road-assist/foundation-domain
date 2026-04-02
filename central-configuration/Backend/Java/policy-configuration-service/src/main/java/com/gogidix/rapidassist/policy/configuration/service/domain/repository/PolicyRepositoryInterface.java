package com.gogidix.rapidassist.policy.configuration.service.domain.repository;

import com.gogidix.rapidassist.policy.configuration.service.domain.model.Policy;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Domain repository interface for Policy entities.
 *
 * <p>This is a PORT in the hexagonal architecture pattern. It defines
 * the contract that the infrastructure layer must implement.
 *
 * <p>All operations are tenant-scoped for multi-tenancy isolation.
 * The tenantId parameter ensures queries are always filtered by tenant.
 *
 * <p>This interface is part of the DOMAIN layer and contains NO
 * infrastructure-specific details (no MongoDB, Redis annotations, etc.).
 */
public interface PolicyRepositoryInterface {

    // ========================================================================
    // CRUD Operations
    // ========================================================================

    /**
     * Saves a policy entity.
     *
     * @param policy the policy to save
     * @return CompletableFuture containing the saved policy
     */
    CompletableFuture<Policy> save(Policy policy);

    /**
     * Finds a policy by its unique ID.
     * The result is filtered by tenant context to ensure isolation.
     *
     * @param id the policy ID
     * @return CompletableFuture containing the policy if found
     */
    CompletableFuture<Optional<Policy>> findById(String id);

    /**
     * Finds a policy by its natural key (tenantId + policyKey + environment).
     *
     * @param tenantId    the tenant ID
     * @param policyKey   the policy key
     * @param environment the environment
     * @return CompletableFuture containing the policy if found
     */
    CompletableFuture<Optional<Policy>> findByNaturalKey(
        String tenantId,
        String policyKey,
        String environment
    );

    /**
     * Finds all policies for a specific tenant.
     * IMPORTANT: This method MUST filter by tenantId for multi-tenancy.
     *
     * @param tenantId the tenant ID
     * @return CompletableFuture containing list of policies
     */
    CompletableFuture<List<Policy>> findByTenantId(String tenantId);

    // ========================================================================
    // Query Operations (Tenant-Scoped)
    // ========================================================================

    /**
     * Finds all policies of a specific type.
     *
     * @param tenantId the tenant ID
     * @param type     the policy type
     * @return CompletableFuture containing list of policies
     */
    CompletableFuture<List<Policy>> findByType(
        String tenantId,
        Policy.PolicyType type
    );

    /**
     * Finds all policies with a specific status.
     *
     * @param tenantId the tenant ID
     * @param status   the policy status
     * @return CompletableFuture containing list of policies
     */
    CompletableFuture<List<Policy>> findByStatus(
        String tenantId,
        Policy.PolicyStatus status
    );

    /**
     * Finds all policies that are enforced.
     *
     * @param tenantId the tenant ID
     * @return CompletableFuture containing list of policies
     */
    CompletableFuture<List<Policy>> findEnforced(String tenantId);

    /**
     * Finds policies by tags.
     *
     * @param tenantId the tenant ID
     * @param tags     the tags to search for
     * @return CompletableFuture containing list of policies
     */
    CompletableFuture<List<Policy>> findByTags(String tenantId, Set<String> tags);

    /**
     * Finds policies updated after a specific timestamp.
     *
     * @param tenantId the tenant ID
     * @param since    the timestamp to search from
     * @return CompletableFuture containing list of policies
     */
    CompletableFuture<List<Policy>> findUpdatedSince(String tenantId, Instant since);

    /**
     * Searches policies by keyword in name or description.
     *
     * @param tenantId the tenant ID
     * @param keyword  the search keyword
     * @return CompletableFuture containing list of policies
     */
    CompletableFuture<List<Policy>> searchByKeyword(String tenantId, String keyword);

    /**
     * Finds all versions of a policy.
     *
     * @param tenantId    the tenant ID
     * @param policyKey   the policy key
     * @param environment the environment
     * @return CompletableFuture containing list of all versions
     */
    CompletableFuture<List<Policy>> findAllVersions(
        String tenantId,
        String policyKey,
        String environment
    );

    /**
     * Finds the latest version of a policy.
     *
     * @param tenantId    the tenant ID
     * @param policyKey   the policy key
     * @param environment the environment
     * @return CompletableFuture containing the latest version if found
     */
    CompletableFuture<Optional<Policy>> findLatestVersion(
        String tenantId,
        String policyKey,
        String environment
    );

    /**
     * Finds all active policies for a specific entity type.
     *
     * @param tenantId    the tenant ID
     * @param entityType  the entity type
     * @param entityId    the entity ID
     * @return CompletableFuture containing list of applicable policies
     */
    CompletableFuture<List<Policy>> findActivePoliciesForEntity(
        String tenantId,
        String entityType,
        String entityId
    );

    // ========================================================================
    // Delete Operations
    // ========================================================================

    /**
     * Deletes a policy by its ID.
     * The tenant context is verified before deletion.
     *
     * @param id the policy ID
     * @return CompletableFuture containing true if deleted
     */
    CompletableFuture<Boolean> deleteById(String id);

    /**
     * Deletes a policy by its natural key.
     * IMPORTANT: Must verify tenantId matches before deletion.
     *
     * @param tenantId    the tenant ID
     * @param policyKey   the policy key
     * @param environment the environment
     * @return CompletableFuture containing true if deleted
     */
    CompletableFuture<Boolean> deleteByNaturalKey(
        String tenantId,
        String policyKey,
        String environment
    );

    // ========================================================================
    // Batch Operations
    // ========================================================================

    /**
     * Saves multiple policies in a batch.
     *
     * @param policies list of policies to save
     * @return CompletableFuture containing list of saved policies
     */
    CompletableFuture<List<Policy>> saveAll(List<Policy> policies);

    /**
     * Finds all policies matching the given IDs.
     * Results are filtered by tenant context.
     *
     * @param ids list of policy IDs
     * @return CompletableFuture containing list of policies
     */
    CompletableFuture<List<Policy>> findAllById(List<String> ids);

    // ========================================================================
    // Count Operations
    // ========================================================================

    /**
     * Counts all policies for a tenant.
     *
     * @param tenantId the tenant ID
     * @return CompletableFuture containing the count
     */
    CompletableFuture<Long> countByTenantId(String tenantId);

    /**
     * Counts policies by status for a tenant.
     *
     * @param tenantId the tenant ID
     * @param status   the policy status
     * @return CompletableFuture containing the count
     */
    CompletableFuture<Long> countByTenantIdAndStatus(
        String tenantId,
        Policy.PolicyStatus status
    );

    /**
     * Counts enforced policies for a tenant.
     *
     * @param tenantId the tenant ID
     * @return CompletableFuture containing the count
     */
    CompletableFuture<Long> countEnforcedByTenantId(String tenantId);

    /**
     * Checks if a policy exists by natural key.
     *
     * @param tenantId    the tenant ID
     * @param policyKey   the policy key
     * @param environment the environment
     * @return CompletableFuture containing true if exists
     */
    CompletableFuture<Boolean> existsByNaturalKey(
        String tenantId,
        String policyKey,
        String environment
    );
}
