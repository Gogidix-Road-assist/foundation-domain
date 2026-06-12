package com.gogidix.rapidassist.common.domain.models.repository;

import com.gogidix.rapidassist.common.domain.models.business.Provider;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data MongoDB repository for Provider entity.
 * Provides CRUD operations and custom query methods.
 */
@Repository
public interface ProviderRepository extends MongoRepository<Provider, String> {

    /**
     * Finds providers by tenant ID.
     * @param tenantId the tenant ID
     * @return list of providers belonging to the tenant
     */
    List<Provider> findByTenantId(String tenantId);

    /**
     * Finds a provider by user ID.
     * @param userId the user ID
     * @return Optional containing the provider if found
     */
    Optional<Provider> findByUserId(String userId);

    /**
     * Finds a provider by provider number.
     * @param providerNumber the provider number
     * @return Optional containing the provider if found
     */
    Optional<Provider> findByProviderNumber(String providerNumber);

    /**
     * Finds a provider by company name.
     * @param companyName the company name
     * @return list of providers with the matching company name
     */
    List<Provider> findByCompanyName(String companyName);

    /**
     * Finds providers by email address.
     * @param email the email address
     * @return Optional containing the provider if found
     */
    Optional<Provider> findByEmail(String email);

    /**
     * Finds providers by status.
     * @param status the status (ACTIVE, INACTIVE, SUSPENDED, etc.)
     * @return list of providers with the specified status
     */
    List<Provider> findByStatus(String status);

    /**
     * Finds providers by tenant ID and status.
     * @param tenantId the tenant ID
     * @param status the status
     * @return list of providers matching both criteria
     */
    List<Provider> findByTenantIdAndStatus(String tenantId, String status);

    /**
     * Finds providers by provider type.
     * @param providerType the provider type (TOWING, MECHANIC, LOCKSMITH, FUEL_DELIVERY, MULTI_SERVICE)
     * @return list of providers with the specified provider type
     */
    List<Provider> findByProviderType(String providerType);

    /**
     * Searches for providers by company name (supports partial matching).
     * @param companyName the company name to search for
     * @return list of providers with company name containing the search term
     */
    @Query("{'companyName': {$regex: ?0, $options: 'i'}}")
    List<Provider> searchByCompanyName(String companyName);

    /**
     * Finds providers by business license.
     * @param businessLicense the business license
     * @return Optional containing the provider if found
     */
    Optional<Provider> findByBusinessLicense(String businessLicense);

    /**
     * Finds providers by organization ID.
     * @param organizationId the organization ID
     * @return list of providers belonging to the organization
     */
    List<Provider> findByOrganizationId(String organizationId);

    /**
     * Finds active providers with high ratings.
     * @param minRating the minimum rating threshold
     * @return list of providers with rating above the threshold
     */
    @Query("{'status': 'ACTIVE', 'rating': {$gte: ?0}}")
    List<Provider> findActiveProvidersWithMinRating(Double minRating);

    /**
     * Counts providers by tenant ID.
     * @param tenantId the tenant ID
     * @return the count of providers
     */
    long countByTenantId(String tenantId);

    /**
     * Counts providers by status.
     * @param status the status
     * @return the count of providers
     */
    long countByStatus(String status);

    /**
     * Checks if a provider exists by email.
     * @param email the email address
     * @return true if a provider with the email exists
     */
    boolean existsByEmail(String email);

    /**
     * Checks if a provider exists by business license.
     * @param businessLicense the business license
     * @return true if a provider with the business license exists
     */
    boolean existsByBusinessLicense(String businessLicense);
}
