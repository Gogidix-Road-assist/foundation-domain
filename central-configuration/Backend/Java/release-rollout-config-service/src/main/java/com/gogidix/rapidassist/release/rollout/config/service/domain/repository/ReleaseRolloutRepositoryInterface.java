package com.gogidix.rapidassist.release.rollout.config.service.domain.repository;

import com.gogidix.rapidassist.release.rollout.config.service.domain.model.ReleaseRollout;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Domain repository interface for ReleaseRollout entities.
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
public interface ReleaseRolloutRepositoryInterface {

    // ========================================================================
    // CRUD Operations
    // ========================================================================

    /**
     * Saves a rollout entity.
     *
     * @param rollout the rollout to save
     * @return CompletableFuture containing the saved rollout
     */
    CompletableFuture<ReleaseRollout> save(ReleaseRollout rollout);

    /**
     * Finds a rollout by its unique ID.
     * The result is filtered by tenant context to ensure isolation.
     *
     * @param id the rollout ID
     * @return CompletableFuture containing the rollout if found
     */
    CompletableFuture<Optional<ReleaseRollout>> findById(String id);

    /**
     * Finds a rollout by its natural key (tenantId + releaseId + version + environment).
     *
     * @param tenantId    the tenant ID
     * @param releaseId   the release ID
     * @param version     the version
     * @param environment the environment
     * @return CompletableFuture containing the rollout if found
     */
    CompletableFuture<Optional<ReleaseRollout>> findByNaturalKey(
        String tenantId,
        String releaseId,
        String version,
        String environment
    );

    /**
     * Finds all rollouts for a specific tenant.
     * IMPORTANT: This method MUST filter by tenantId for multi-tenancy.
     *
     * @param tenantId the tenant ID
     * @return CompletableFuture containing list of rollouts
     */
    CompletableFuture<List<ReleaseRollout>> findByTenantId(String tenantId);

    // ========================================================================
    // Query Operations (Tenant-Scoped)
    // ========================================================================

    /**
     * Finds all rollouts for a specific release.
     *
     * @param tenantId  the tenant ID
     * @param releaseId the release ID
     * @return CompletableFuture containing list of rollouts
     */
    CompletableFuture<List<ReleaseRollout>> findByReleaseId(String tenantId, String releaseId);

    /**
     * Finds all rollouts with a specific status.
     *
     * @param tenantId the tenant ID
     * @param status   the rollout status
     * @return CompletableFuture containing list of rollouts
     */
    CompletableFuture<List<ReleaseRollout>> findByStatus(
        String tenantId,
        ReleaseRollout.RolloutStatus status
    );

    /**
     * Finds all rollouts in an environment.
     *
     * @param tenantId    the tenant ID
     * @param environment the environment
     * @return CompletableFuture containing list of rollouts
     */
    CompletableFuture<List<ReleaseRollout>> findByEnvironment(String tenantId, String environment);

    /**
     * Finds all rollouts with a specific strategy.
     *
     * @param tenantId the tenant ID
     * @param strategy the rollout strategy
     * @return CompletableFuture containing list of rollouts
     */
    CompletableFuture<List<ReleaseRollout>> findByStrategy(
        String tenantId,
        ReleaseRollout.RolloutStrategy strategy
    );

    /**
     * Finds rollouts created after a specific timestamp.
     *
     * @param tenantId the tenant ID
     * @param since    the timestamp to search from
     * @return CompletableFuture containing list of rollouts
     */
    CompletableFuture<List<ReleaseRollout>> findCreatedSince(String tenantId, Instant since);

    /**
     * Finds rollouts updated after a specific timestamp.
     *
     * @param tenantId the tenant ID
     * @param since    the timestamp to search from
     * @return CompletableFuture containing list of rollouts
     */
    CompletableFuture<List<ReleaseRollout>> findUpdatedSince(String tenantId, Instant since);

    /**
     * Finds the latest rollout for a release in an environment.
     *
     * @param tenantId    the tenant ID
     * @param releaseId   the release ID
     * @param environment the environment
     * @return CompletableFuture containing the latest rollout if found
     */
    CompletableFuture<Optional<ReleaseRollout>> findLatestByReleaseAndEnvironment(
        String tenantId,
        String releaseId,
        String environment
    );

    /**
     * Finds all versions of a rollout across all environments.
     *
     * @param tenantId  the tenant ID
     * @param releaseId the release ID
     * @return CompletableFuture containing list of all versions
     */
    CompletableFuture<List<ReleaseRollout>> findAllVersions(String tenantId, String releaseId);

    // ========================================================================
    // Delete Operations
    // ========================================================================

    /**
     * Deletes a rollout by its ID.
     * The tenant context is verified before deletion.
     *
     * @param id the rollout ID
     * @return CompletableFuture containing true if deleted
     */
    CompletableFuture<Boolean> deleteById(String id);

    /**
     * Deletes a rollout by its natural key.
     * IMPORTANT: Must verify tenantId matches before deletion.
     *
     * @param tenantId    the tenant ID
     * @param releaseId   the release ID
     * @param version     the version
     * @param environment the environment
     * @return CompletableFuture containing true if deleted
     */
    CompletableFuture<Boolean> deleteByNaturalKey(
        String tenantId,
        String releaseId,
        String version,
        String environment
    );

    // ========================================================================
    // Batch Operations
    // ========================================================================

    /**
     * Saves multiple rollouts in a batch.
     *
     * @param rollouts list of rollouts to save
     * @return CompletableFuture containing list of saved rollouts
     */
    CompletableFuture<List<ReleaseRollout>> saveAll(List<ReleaseRollout> rollouts);

    /**
     * Finds all rollouts matching the given IDs.
     * Results are filtered by tenant context.
     *
     * @param ids list of rollout IDs
     * @return CompletableFuture containing list of rollouts
     */
    CompletableFuture<List<ReleaseRollout>> findAllById(List<String> ids);

    // ========================================================================
    // Count Operations
    // ========================================================================

    /**
     * Counts all rollouts for a tenant.
     *
     * @param tenantId the tenant ID
     * @return CompletableFuture containing the count
     */
    CompletableFuture<Long> countByTenantId(String tenantId);

    /**
     * Counts rollouts by status for a tenant.
     *
     * @param tenantId the tenant ID
     * @param status   the rollout status
     * @return CompletableFuture containing the count
     */
    CompletableFuture<Long> countByTenantIdAndStatus(
        String tenantId,
        ReleaseRollout.RolloutStatus status
    );

    /**
     * Counts rollouts by environment for a tenant.
     *
     * @param tenantId    the tenant ID
     * @param environment the environment
     * @return CompletableFuture containing the count
     */
    CompletableFuture<Long> countByTenantIdAndEnvironment(String tenantId, String environment);

    /**
     * Checks if a rollout exists by natural key.
     *
     * @param tenantId    the tenant ID
     * @param releaseId   the release ID
     * @param version     the version
     * @param environment the environment
     * @return CompletableFuture containing true if exists
     */
    CompletableFuture<Boolean> existsByNaturalKey(
        String tenantId,
        String releaseId,
        String version,
        String environment
    );

    /**
     * Finds all in-progress rollouts that should be auto-promoted.
     *
     * @param tenantId the tenant ID
     * @return CompletableFuture containing list of rollouts
     */
    CompletableFuture<List<ReleaseRollout>> findAutoPromotableRollouts(String tenantId);
}
