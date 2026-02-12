package com.gogidix.rapidassist.config.service.domain.repository;

import com.gogidix.rapidassist.config.service.domain.model.Configuration;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Domain repository interface for Configuration entities.
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
public interface ConfigurationRepositoryInterface {

    // ========================================================================
    // CRUD Operations
    // ========================================================================

    /**
     * Saves a configuration entity.
     *
     * @param configuration the configuration to save
     * @return CompletableFuture containing the saved configuration
     */
    CompletableFuture<Configuration> save(Configuration configuration);

    /**
     * Finds a configuration by its unique ID.
     * The result is filtered by tenant context to ensure isolation.
     *
     * @param id the configuration ID
     * @return CompletableFuture containing the configuration if found
     */
    CompletableFuture<Optional<Configuration>> findById(String id);

    /**
     * Finds a configuration by its natural key (tenantId + configKey + environment + namespace).
     *
     * @param tenantId    the tenant ID
     * @param configKey   the configuration key
     * @param environment the environment
     * @param namespace   the namespace
     * @return CompletableFuture containing the configuration if found
     */
    CompletableFuture<Optional<Configuration>> findByNaturalKey(
        String tenantId,
        String configKey,
        String environment,
        String namespace
    );

    /**
     * Finds all configurations for a specific tenant.
     * IMPORTANT: This method MUST filter by tenantId for multi-tenancy.
     *
     * @param tenantId the tenant ID
     * @return CompletableFuture containing list of configurations
     */
    CompletableFuture<List<Configuration>> findByTenantId(String tenantId);

    // ========================================================================
    // Query Operations (Tenant-Scoped)
    // ========================================================================

    /**
     * Finds all configurations in a namespace.
     *
     * @param tenantId    the tenant ID
     * @param environment the environment
     * @param namespace   the namespace
     * @return CompletableFuture containing list of configurations
     */
    CompletableFuture<List<Configuration>> findByNamespace(
        String tenantId,
        String environment,
        String namespace
    );

    /**
     * Finds all configurations with a specific status.
     *
     * @param tenantId the tenant ID
     * @param status   the configuration status
     * @return CompletableFuture containing list of configurations
     */
    CompletableFuture<List<Configuration>> findByStatus(
        String tenantId,
        Configuration.ConfigurationStatus status
    );

    /**
     * Finds configurations by tags.
     *
     * @param tenantId the tenant ID
     * @param tags     the tags to search for
     * @return CompletableFuture containing list of configurations
     */
    CompletableFuture<List<Configuration>> findByTags(String tenantId, Set<String> tags);

    /**
     * Finds configurations updated after a specific timestamp.
     *
     * @param tenantId the tenant ID
     * @param since    the timestamp to search from
     * @return CompletableFuture containing list of configurations
     */
    CompletableFuture<List<Configuration>> findUpdatedSince(String tenantId, Instant since);

    /**
     * Searches configurations by keyword in key or description.
     *
     * @param tenantId the tenant ID
     * @param keyword  the search keyword
     * @return CompletableFuture containing list of configurations
     */
    CompletableFuture<List<Configuration>> searchByKeyword(String tenantId, String keyword);

    /**
     * Finds all versions of a configuration.
     *
     * @param tenantId    the tenant ID
     * @param configKey   the configuration key
     * @param environment the environment
     * @return CompletableFuture containing list of all versions
     */
    CompletableFuture<List<Configuration>> findAllVersions(
        String tenantId,
        String configKey,
        String environment
    );

    /**
     * Finds the latest version of a configuration.
     *
     * @param tenantId    the tenant ID
     * @param configKey   the configuration key
     * @param environment the environment
     * @return CompletableFuture containing the latest version if found
     */
    CompletableFuture<Optional<Configuration>> findLatestVersion(
        String tenantId,
        String configKey,
        String environment
    );

    // ========================================================================
    // Delete Operations
    // ========================================================================

    /**
     * Deletes a configuration by its ID.
     * The tenant context is verified before deletion.
     *
     * @param id the configuration ID
     * @return CompletableFuture containing true if deleted
     */
    CompletableFuture<Boolean> deleteById(String id);

    /**
     * Deletes a configuration by its natural key.
     * IMPORTANT: Must verify tenantId matches before deletion.
     *
     * @param tenantId    the tenant ID
     * @param configKey   the configuration key
     * @param environment the environment
     * @param namespace   the namespace
     * @return CompletableFuture containing true if deleted
     */
    CompletableFuture<Boolean> deleteByNaturalKey(
        String tenantId,
        String configKey,
        String environment,
        String namespace
    );

    // ========================================================================
    // Batch Operations
    // ========================================================================

    /**
     * Saves multiple configurations in a batch.
     *
     * @param configurations list of configurations to save
     * @return CompletableFuture containing list of saved configurations
     */
    CompletableFuture<List<Configuration>> saveAll(List<Configuration> configurations);

    /**
     * Finds all configurations matching the given IDs.
     * Results are filtered by tenant context.
     *
     * @param ids list of configuration IDs
     * @return CompletableFuture containing list of configurations
     */
    CompletableFuture<List<Configuration>> findAllById(List<String> ids);

    // ========================================================================
    // Count Operations
    // ========================================================================

    /**
     * Counts all configurations for a tenant.
     *
     * @param tenantId the tenant ID
     * @return CompletableFuture containing the count
     */
    CompletableFuture<Long> countByTenantId(String tenantId);

    /**
     * Counts configurations by status for a tenant.
     *
     * @param tenantId the tenant ID
     * @param status   the configuration status
     * @return CompletableFuture containing the count
     */
    CompletableFuture<Long> countByTenantIdAndStatus(
        String tenantId,
        Configuration.ConfigurationStatus status
    );

    /**
     * Checks if a configuration exists by natural key.
     *
     * @param tenantId    the tenant ID
     * @param configKey   the configuration key
     * @param environment the environment
     * @param namespace   the namespace
     * @return CompletableFuture containing true if exists
     */
    CompletableFuture<Boolean> existsByNaturalKey(
        String tenantId,
        String configKey,
        String environment,
        String namespace
    );
}
