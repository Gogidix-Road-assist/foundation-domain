package com.gogidix.rapidassist.tenancy.configuration.service.domain.repository;

import com.gogidix.rapidassist.tenancy.configuration.service.domain.model.TenantConfig;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Domain repository interface for TenantConfig entities.
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
public interface TenantConfigRepositoryInterface {

    // ========================================================================
    // CRUD Operations
    // ========================================================================

    /**
     * Saves a tenant configuration entity.
     *
     * @param tenantConfig the tenant configuration to save
     * @return CompletableFuture containing the saved tenant configuration
     */
    CompletableFuture<TenantConfig> save(TenantConfig tenantConfig);

    /**
     * Finds a tenant configuration by its unique ID.
     * The result is filtered by tenant context to ensure isolation.
     *
     * @param id the configuration ID
     * @return CompletableFuture containing the tenant configuration if found
     */
    CompletableFuture<Optional<TenantConfig>> findById(String id);

    /**
     * Finds a tenant configuration by its tenant ID.
     *
     * @param tenantId the tenant ID
     * @return CompletableFuture containing the tenant configuration if found
     */
    CompletableFuture<Optional<TenantConfig>> findByTenantId(String tenantId);

    /**
     * Finds a tenant configuration by its domain.
     *
     * @param domain the domain
     * @return CompletableFuture containing the tenant configuration if found
     */
    CompletableFuture<Optional<TenantConfig>> findByDomain(String domain);

    /**
     * Finds all tenant configurations.
     *
     * @return CompletableFuture containing list of all tenant configurations
     */
    CompletableFuture<List<TenantConfig>> findAll();

    // ========================================================================
    // Query Operations
    // ========================================================================

    /**
     * Finds all active tenant configurations.
     *
     * @return CompletableFuture containing list of active tenant configurations
     */
    CompletableFuture<List<TenantConfig>> findByActiveTrue();

    /**
     * Finds all inactive tenant configurations.
     *
     * @return CompletableFuture containing list of inactive tenant configurations
     */
    CompletableFuture<List<TenantConfig>> findByActiveFalse();

    /**
     * Finds tenant configurations by environment.
     *
     * @param environment the environment
     * @return CompletableFuture containing list of tenant configurations
     */
    CompletableFuture<List<TenantConfig>> findByEnvironment(String environment);

    /**
     * Finds tenant configurations that have a specific feature enabled.
     *
     * @param feature the feature name
     * @return CompletableFuture containing list of tenant configurations
     */
    CompletableFuture<List<TenantConfig>> findByFeaturesEnabledFeaturesContaining(String feature);

    /**
     * Finds tenant configurations updated after a specific timestamp.
     *
     * @param since the timestamp to search from
     * @return CompletableFuture containing list of tenant configurations
     */
    CompletableFuture<List<TenantConfig>> findByUpdatedAtAfter(Instant since);

    /**
     * Searches tenant configurations by keyword in name or domain.
     *
     * @param keyword the search keyword
     * @return CompletableFuture containing list of tenant configurations
     */
    CompletableFuture<List<TenantConfig>> searchByKeyword(String keyword);

    // ========================================================================
    // Delete Operations
    // ========================================================================

    /**
     * Deletes a tenant configuration by its ID.
     *
     * @param id the configuration ID
     * @return CompletableFuture containing true if deleted
     */
    CompletableFuture<Boolean> deleteById(String id);

    /**
     * Deletes a tenant configuration by its tenant ID.
     *
     * @param tenantId the tenant ID
     * @return CompletableFuture containing true if deleted
     */
    CompletableFuture<Boolean> deleteByTenantId(String tenantId);

    // ========================================================================
    // Batch Operations
    // ========================================================================

    /**
     * Saves multiple tenant configurations in a batch.
     *
     * @param tenantConfigs list of tenant configurations to save
     * @return CompletableFuture containing list of saved tenant configurations
     */
    CompletableFuture<List<TenantConfig>> saveAll(List<TenantConfig> tenantConfigs);

    /**
     * Finds all tenant configurations matching the given IDs.
     *
     * @param ids list of tenant configuration IDs
     * @return CompletableFuture containing list of tenant configurations
     */
    CompletableFuture<List<TenantConfig>> findAllById(List<String> ids);

    // ========================================================================
    // Count Operations
    // ========================================================================

    /**
     * Counts all tenant configurations.
     *
     * @return CompletableFuture containing the count
     */
    CompletableFuture<Long> count();

    /**
     * Counts active tenant configurations.
     *
     * @return CompletableFuture containing the count
     */
    CompletableFuture<Long> countByActiveTrue();

    /**
     * Counts inactive tenant configurations.
     *
     * @return CompletableFuture containing the count
     */
    CompletableFuture<Long> countByActiveFalse();

    /**
     * Checks if a tenant configuration exists by tenant ID.
     *
     * @param tenantId the tenant ID
     * @return CompletableFuture containing true if exists
     */
    CompletableFuture<Boolean> existsByTenantId(String tenantId);

    /**
     * Checks if a tenant configuration exists by domain.
     *
     * @param domain the domain
     * @return CompletableFuture containing true if exists
     */
    CompletableFuture<Boolean> existsByDomain(String domain);
}
