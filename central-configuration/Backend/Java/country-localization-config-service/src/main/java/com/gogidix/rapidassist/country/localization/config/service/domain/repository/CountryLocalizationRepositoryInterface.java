package com.gogidix.rapidassist.country.localization.config.service.domain.repository;

import com.gogidix.rapidassist.country.localization.config.service.domain.model.CountryLocalization;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Domain repository interface for CountryLocalization entities.
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
public interface CountryLocalizationRepositoryInterface {

    // ========================================================================
    // CRUD Operations
    // ========================================================================

    /**
     * Saves a country localization entity.
     *
     * @param localization the localization to save
     * @return CompletableFuture containing the saved localization
     */
    CompletableFuture<CountryLocalization> save(CountryLocalization localization);

    /**
     * Finds a localization by its unique ID.
     * The result is filtered by tenant context to ensure isolation.
     *
     * @param id the localization ID
     * @return CompletableFuture containing the localization if found
     */
    CompletableFuture<Optional<CountryLocalization>> findById(String id);

    /**
     * Finds a localization by its natural key (tenantId + countryCode).
     *
     * @param tenantId    the tenant ID
     * @param countryCode the ISO 3166-1 alpha-2 country code
     * @return CompletableFuture containing the localization if found
     */
    CompletableFuture<Optional<CountryLocalization>> findByNaturalKey(
        String tenantId,
        String countryCode
    );

    /**
     * Finds all localizations for a specific tenant.
     * IMPORTANT: This method MUST filter by tenantId for multi-tenancy.
     *
     * @param tenantId the tenant ID
     * @return CompletableFuture containing list of localizations
     */
    CompletableFuture<List<CountryLocalization>> findByTenantId(String tenantId);

    // ========================================================================
    // Query Operations (Tenant-Scoped)
    // ========================================================================

    /**
     * Finds all active localizations for a tenant.
     *
     * @param tenantId the tenant ID
     * @return CompletableFuture containing list of active localizations
     */
    CompletableFuture<List<CountryLocalization>> findActiveByTenantId(String tenantId);

    /**
     * Finds localizations by measurement system for a tenant.
     *
     * @param tenantId          the tenant ID
     * @param measurementSystem the measurement system
     * @return CompletableFuture containing list of localizations
     */
    CompletableFuture<List<CountryLocalization>> findByMeasurementSystem(
        String tenantId,
        CountryLocalization.MeasurementSystem measurementSystem
    );

    /**
     * Searches localizations by country name keyword for a tenant.
     *
     * @param tenantId the tenant ID
     * @param keyword  the search keyword
     * @return CompletableFuture containing list of matching localizations
     */
    CompletableFuture<List<CountryLocalization>> searchByCountryName(String tenantId, String keyword);

    /**
     * Finds localizations updated after a specific timestamp for a tenant.
     *
     * @param tenantId the tenant ID
     * @param since    the timestamp to search from
     * @return CompletableFuture containing list of localizations
     */
    CompletableFuture<List<CountryLocalization>> findUpdatedSince(String tenantId, Instant since);

    /**
     * Finds all localizations with specific locale for a tenant.
     *
     * @param tenantId     the tenant ID
     * @param languageCode the ISO 639-1 language code
     * @param regionCode   the ISO 3166-1 alpha-2 region code
     * @return CompletableFuture containing list of localizations
     */
    CompletableFuture<List<CountryLocalization>> findByLocale(
        String tenantId,
        String languageCode,
        String regionCode
    );

    // ========================================================================
    // Delete Operations
    // ========================================================================

    /**
     * Deletes a localization by its ID.
     * The tenant context is verified before deletion.
     *
     * @param id the localization ID
     * @return CompletableFuture containing true if deleted
     */
    CompletableFuture<Boolean> deleteById(String id);

    /**
     * Deletes a localization by its natural key.
     * IMPORTANT: Must verify tenantId matches before deletion.
     *
     * @param tenantId    the tenant ID
     * @param countryCode the country code
     * @return CompletableFuture containing true if deleted
     */
    CompletableFuture<Boolean> deleteByNaturalKey(
        String tenantId,
        String countryCode
    );

    // ========================================================================
    // Batch Operations
    // ========================================================================

    /**
     * Saves multiple localizations in a batch.
     *
     * @param localizations list of localizations to save
     * @return CompletableFuture containing list of saved localizations
     */
    CompletableFuture<List<CountryLocalization>> saveAll(List<CountryLocalization> localizations);

    /**
     * Finds all localizations matching the given IDs.
     * Results are filtered by tenant context.
     *
     * @param ids list of localization IDs
     * @return CompletableFuture containing list of localizations
     */
    CompletableFuture<List<CountryLocalization>> findAllById(List<String> ids);

    // ========================================================================
    // Count Operations
    // ========================================================================

    /**
     * Counts all localizations for a tenant.
     *
     * @param tenantId the tenant ID
     * @return CompletableFuture containing the count
     */
    CompletableFuture<Long> countByTenantId(String tenantId);

    /**
     * Counts active localizations for a tenant.
     *
     * @param tenantId the tenant ID
     * @return CompletableFuture containing the count
     */
    CompletableFuture<Long> countActiveByTenantId(String tenantId);

    /**
     * Checks if a localization exists by natural key.
     *
     * @param tenantId    the tenant ID
     * @param countryCode the country code
     * @return CompletableFuture containing true if exists
     */
    CompletableFuture<Boolean> existsByNaturalKey(
        String tenantId,
        String countryCode
    );
}
