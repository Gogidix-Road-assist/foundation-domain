package com.gogidix.rapidassist.feature.flags.service.domain.port.out;

import com.gogidix.rapidassist.feature.flags.service.domain.model.FeatureFlag;
import com.gogidix.rapidassist.feature.flags.service.domain.model.FeatureFlagChange;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Output port for persisting and retrieving Feature Flag entities.
 *
 * <p>This is a secondary port (outbound) in the hexagonal architecture.
 * The domain layer depends on this interface, and the infrastructure layer
 * provides the implementation.
 *
 * <p>All operations are tenant-scoped for multi-tenancy isolation.
 * The tenantId parameter ensures queries are always filtered by tenant.
 */
public interface FeatureFlagRepository {

    // ========================================================================
    // CRUD Operations
    // ========================================================================

    /**
     * Saves a feature flag.
     *
     * @param flag the feature flag to save
     * @return CompletableFuture containing the saved feature flag
     */
    CompletableFuture<FeatureFlag> save(FeatureFlag flag);

    /**
     * Finds a feature flag by its ID.
     *
     * @param flagId the feature flag ID
     * @return CompletableFuture containing the feature flag if found
     */
    CompletableFuture<Optional<FeatureFlag>> findById(String flagId);

    /**
     * Finds a feature flag by its natural key (tenantId + key + environment).
     *
     * @param tenantId the tenant ID
     * @param key the feature flag key
     * @param environment the environment
     * @return CompletableFuture containing the feature flag if found
     */
    CompletableFuture<Optional<FeatureFlag>> findByKey(String tenantId, String key, String environment);

    /**
     * Finds all feature flags for a tenant.
     *
     * @param tenantId the tenant ID
     * @return CompletableFuture containing list of feature flags
     */
    CompletableFuture<List<FeatureFlag>> findByTenant(String tenantId);

    /**
     * Finds feature flags by tenant and environment.
     *
     * @param tenantId the tenant ID
     * @param environment the environment
     * @return CompletableFuture containing list of feature flags
     */
    CompletableFuture<List<FeatureFlag>> findByTenantAndEnvironment(String tenantId, String environment);

    /**
     * Finds feature flags by tenant and status.
     *
     * @param tenantId the tenant ID
     * @param status the feature flag status
     * @return CompletableFuture containing list of feature flags
     */
    CompletableFuture<List<FeatureFlag>> findByStatus(String tenantId, FeatureFlag.FlagStatus status);

    /**
     * Finds feature flags by tags.
     *
     * @param tenantId the tenant ID
     * @param tags the tags to search for
     * @return CompletableFuture containing list of feature flags
     */
    CompletableFuture<List<FeatureFlag>> findByTags(String tenantId, Set<String> tags);

    /**
     * Searches feature flags by keyword in key, name, or description.
     *
     * @param tenantId the tenant ID
     * @param keyword the search keyword
     * @return CompletableFuture containing list of feature flags
     */
    CompletableFuture<List<FeatureFlag>> searchByKeyword(String tenantId, String keyword);

    /**
     * Finds feature flags created after a specific timestamp.
     *
     * @param tenantId the tenant ID
     * @param since the timestamp to search from
     * @return CompletableFuture containing list of feature flags
     */
    CompletableFuture<List<FeatureFlag>> findCreatedSince(String tenantId, Instant since);

    /**
     * Finds feature flags expiring before a specific timestamp.
     *
     * @param tenantId the tenant ID
     * @param before the timestamp
     * @return CompletableFuture containing list of feature flags
     */
    CompletableFuture<List<FeatureFlag>> findExpiringBefore(String tenantId, Instant before);

    /**
     * Finds all active feature flags for a tenant and environment.
     *
     * @param tenantId the tenant ID
     * @param environment the environment
     * @return CompletableFuture containing list of active feature flags
     */
    CompletableFuture<List<FeatureFlag>> findAllActive(String tenantId, String environment);

    /**
     * Deletes a feature flag by ID.
     *
     * @param flagId the feature flag ID
     * @return CompletableFuture containing true if deleted
     */
    CompletableFuture<Boolean> deleteById(String flagId);

    // ========================================================================
    // Change Tracking Operations
    // ========================================================================

    /**
     * Saves a feature flag change.
     *
     * @param change the feature flag change to save
     * @return CompletableFuture containing the saved change
     */
    CompletableFuture<FeatureFlagChange> saveChange(FeatureFlagChange change);

    /**
     * Finds changes by feature flag ID.
     *
     * @param flagId the feature flag ID
     * @return CompletableFuture containing list of changes
     */
    CompletableFuture<List<FeatureFlagChange>> findChangesByFlagId(String flagId);

    /**
     * Finds changes within a time range for a tenant.
     *
     * @param tenantId the tenant ID
     * @param from the start time
     * @param to the end time
     * @return CompletableFuture containing list of changes
     */
    CompletableFuture<List<FeatureFlagChange>> findChangesByTimeRange(String tenantId, Instant from, Instant to);

    /**
     * Finds changes pending approval for a tenant.
     *
     * @param tenantId the tenant ID
     * @return CompletableFuture containing list of pending changes
     */
    CompletableFuture<List<FeatureFlagChange>> findPendingApprovals(String tenantId);

    /**
     * Finds a change by ID.
     *
     * @param changeId the change ID
     * @return CompletableFuture containing the change if found
     */
    CompletableFuture<Optional<FeatureFlagChange>> findChangeById(String changeId);

    // ========================================================================
    // Cache Operations
    // ========================================================================

    /**
     * Caches a feature flag.
     *
     * @param flag the feature flag to cache
     * @return CompletableFuture completion signal
     */
    CompletableFuture<Void> cacheFlag(FeatureFlag flag);

    /**
     * Gets a cached feature flag.
     *
     * @param tenantId the tenant ID
     * @param key the feature flag key
     * @param environment the environment
     * @return CompletableFuture containing the cached flag if found
     */
    CompletableFuture<Optional<FeatureFlag>> getCachedFlag(String tenantId, String key, String environment);

    /**
     * Evicts a feature flag from cache.
     *
     * @param tenantId the tenant ID
     * @param key the feature flag key
     * @param environment the environment
     * @return CompletableFuture completion signal
     */
    CompletableFuture<Void> evictFlag(String tenantId, String key, String environment);

    /**
     * Evicts all cached feature flags for a tenant.
     *
     * @param tenantId the tenant ID
     * @return CompletableFuture completion signal
     */
    CompletableFuture<Void> evictAll(String tenantId);
}
