package com.gogidix.rapidassist.feature.flags.service.domain.repository;

import com.gogidix.rapidassist.feature.flags.service.domain.model.FeatureFlag;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Domain repository interface for FeatureFlag entities.
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
public interface FeatureFlagRepositoryInterface {

    // ========================================================================
    // CRUD Operations
    // ========================================================================

    /**
     * Saves a feature flag entity.
     *
     * @param featureFlag the feature flag to save
     * @return CompletableFuture containing the saved feature flag
     */
    CompletableFuture<FeatureFlag> save(FeatureFlag featureFlag);

    /**
     * Finds a feature flag by its unique ID.
     * The result is filtered by tenant context to ensure isolation.
     *
     * @param id the feature flag ID
     * @return CompletableFuture containing the feature flag if found
     */
    CompletableFuture<Optional<FeatureFlag>> findById(String id);

    /**
     * Finds a feature flag by its natural key (tenantId + key + environment).
     *
     * @param tenantId    the tenant ID
     * @param key         the feature flag key
     * @param environment the environment
     * @return CompletableFuture containing the feature flag if found
     */
    CompletableFuture<Optional<FeatureFlag>> findByNaturalKey(
        String tenantId,
        String key,
        String environment
    );

    /**
     * Finds all feature flags for a specific tenant.
     * IMPORTANT: This method MUST filter by tenantId for multi-tenancy.
     *
     * @param tenantId the tenant ID
     * @return CompletableFuture containing list of feature flags
     */
    CompletableFuture<List<FeatureFlag>> findByTenantId(String tenantId);

    // ========================================================================
    // Query Operations (Tenant-Scoped)
    // ========================================================================

    /**
     * Finds all feature flags in an environment.
     *
     * @param tenantId    the tenant ID
     * @param environment the environment
     * @return CompletableFuture containing list of feature flags
     */
    CompletableFuture<List<FeatureFlag>> findByEnvironment(
        String tenantId,
        String environment
    );

    /**
     * Finds all feature flags with a specific status.
     *
     * @param tenantId the tenant ID
     * @param status   the feature flag status
     * @return CompletableFuture containing list of feature flags
     */
    CompletableFuture<List<FeatureFlag>> findByStatus(
        String tenantId,
        FeatureFlag.FlagStatus status
    );

    /**
     * Finds all enabled feature flags for a tenant.
     *
     * @param tenantId the tenant ID
     * @return CompletableFuture containing list of enabled feature flags
     */
    CompletableFuture<List<FeatureFlag>> findEnabled(String tenantId);

    /**
     * Finds feature flags by tags.
     *
     * @param tenantId the tenant ID
     * @param tags     the tags to search for
     * @return CompletableFuture containing list of feature flags
     */
    CompletableFuture<List<FeatureFlag>> findByTags(String tenantId, Set<String> tags);

    /**
     * Finds feature flags updated after a specific timestamp.
     *
     * @param tenantId the tenant ID
     * @param since    the timestamp to search from
     * @return CompletableFuture containing list of feature flags
     */
    CompletableFuture<List<FeatureFlag>> findUpdatedSince(String tenantId, Instant since);

    /**
     * Searches feature flags by keyword in key or name.
     *
     * @param tenantId the tenant ID
     * @param keyword  the search keyword
     * @return CompletableFuture containing list of feature flags
     */
    CompletableFuture<List<FeatureFlag>> searchByKeyword(String tenantId, String keyword);

    /**
     * Finds feature flags by type.
     *
     * @param tenantId the tenant ID
     * @param type     the flag type
     * @return CompletableFuture containing list of feature flags
     */
    CompletableFuture<List<FeatureFlag>> findByType(
        String tenantId,
        FeatureFlag.FlagType type
    );

    /**
     * Finds feature flags by rollout strategy.
     *
     * @param tenantId        the tenant ID
     * @param rolloutStrategy the rollout strategy
     * @return CompletableFuture containing list of feature flags
     */
    CompletableFuture<List<FeatureFlag>> findByRolloutStrategy(
        String tenantId,
        FeatureFlag.RolloutStrategy rolloutStrategy
    );

    /**
     * Finds expired feature flags.
     *
     * @param tenantId the tenant ID
     * @return CompletableFuture containing list of expired feature flags
     */
    CompletableFuture<List<FeatureFlag>> findExpired(String tenantId);

    /**
     * Finds feature flags that are active and enabled.
     *
     * @param tenantId    the tenant ID
     * @param environment the environment
     * @return CompletableFuture containing list of active and enabled flags
     */
    CompletableFuture<List<FeatureFlag>> findActiveAndEnabled(
        String tenantId,
        String environment
    );

    // ========================================================================
    // Delete Operations
    // ========================================================================

    /**
     * Deletes a feature flag by its ID.
     * The tenant context is verified before deletion.
     *
     * @param id the feature flag ID
     * @return CompletableFuture containing true if deleted
     */
    CompletableFuture<Boolean> deleteById(String id);

    /**
     * Deletes a feature flag by its natural key.
     * IMPORTANT: Must verify tenantId matches before deletion.
     *
     * @param tenantId    the tenant ID
     * @param key         the feature flag key
     * @param environment the environment
     * @return CompletableFuture containing true if deleted
     */
    CompletableFuture<Boolean> deleteByNaturalKey(
        String tenantId,
        String key,
        String environment
    );

    // ========================================================================
    // Batch Operations
    // ========================================================================

    /**
     * Saves multiple feature flags in a batch.
     *
     * @param featureFlags list of feature flags to save
     * @return CompletableFuture containing list of saved feature flags
     */
    CompletableFuture<List<FeatureFlag>> saveAll(List<FeatureFlag> featureFlags);

    /**
     * Finds all feature flags matching the given IDs.
     * Results are filtered by tenant context.
     *
     * @param ids list of feature flag IDs
     * @return CompletableFuture containing list of feature flags
     */
    CompletableFuture<List<FeatureFlag>> findAllById(List<String> ids);

    // ========================================================================
    // Count Operations
    // ========================================================================

    /**
     * Counts all feature flags for a tenant.
     *
     * @param tenantId the tenant ID
     * @return CompletableFuture containing the count
     */
    CompletableFuture<Long> countByTenantId(String tenantId);

    /**
     * Counts feature flags by status for a tenant.
     *
     * @param tenantId the tenant ID
     * @param status   the feature flag status
     * @return CompletableFuture containing the count
     */
    CompletableFuture<Long> countByTenantIdAndStatus(
        String tenantId,
        FeatureFlag.FlagStatus status
    );

    /**
     * Counts enabled feature flags for a tenant.
     *
     * @param tenantId the tenant ID
     * @return CompletableFuture containing the count
     */
    CompletableFuture<Long> countEnabled(String tenantId);

    /**
     * Checks if a feature flag exists by natural key.
     *
     * @param tenantId    the tenant ID
     * @param key         the feature flag key
     * @param environment the environment
     * @return CompletableFuture containing true if exists
     */
    CompletableFuture<Boolean> existsByNaturalKey(
        String tenantId,
        String key,
        String environment
    );
}
