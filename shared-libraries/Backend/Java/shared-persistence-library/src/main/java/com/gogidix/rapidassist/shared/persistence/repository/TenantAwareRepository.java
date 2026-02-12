package com.gogidix.rapidassist.shared.persistence.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Base repository interface for tenant-aware repositories in a multi-tenant system.
 * <p>
 * This interface extends MongoRepository to provide tenant-specific CRUD operations
 * while maintaining data isolation between tenants. All repository interfaces
 * for tenant-aware entities should extend this interface instead of MongoRepository directly.
 * </p>
 * <p>
 * The interface provides automatic tenant filtering for common query operations,
 * ensuring that applications cannot accidentally access data from other tenants.
 * </p>
 * <p>
 * Usage example:
 * <pre>{@code
 * public interface ServiceRequestRepository
 *     extends TenantAwareRepository<ServiceRequest, UUID> {
 *
 *     // Custom tenant-scoped queries can be added here
 *     List<ServiceRequest> findByTenantIdAndStatus(UUID tenantId, RequestStatus status);
 *
 *     // Spring Data MongoDB automatically adds tenantId filtering
 *     // findByIdAndTenantId is inherited from TenantAwareRepository
 * }
 * }</pre>
 * </p>
 *
 * @param <T> the entity type managed by this repository, must extend TenantAwareEntity
 * @param <ID> the type of the entity's identifier (typically UUID)
 * @author Gogidix Platform Team
 * @version 1.0
 * @since 2026-01-13
 */
@NoRepositoryBean
public interface TenantAwareRepository<T, ID> extends MongoRepository<T, ID> {

    /**
     * Retrieves all entities belonging to the specified tenant.
     * <p>
     * This method ensures complete tenant isolation by only returning entities
     * that match the provided tenant ID. Use this method in conjunction with
     * the request context's tenant identifier.
     * </p>
     *
     * @param tenantId the unique identifier of the tenant (UUID)
     * @return a list of all entities belonging to the tenant, empty list if none found
     * @throws IllegalArgumentException if tenantId is null
     */
    @Transactional(readOnly = true)
    @Query("{ 'tenantId': ?0 }")
    List<T> findAllByTenantId(UUID tenantId);

    /**
     * Retrieves all entities belonging to the specified tenant with pagination support.
     * <p>
     * This method provides paginated access to tenant-specific data, useful for
     * implementing list views with large datasets.
     * </p>
     *
     * @param tenantId the unique identifier of the tenant (UUID)
     * @param pageable the pagination parameters
     * @return a page of entities belonging to the tenant
     * @throws IllegalArgumentException if tenantId is null
     */
    @Transactional(readOnly = true)
    @Query("{ 'tenantId': ?0 }")
    org.springframework.data.domain.Page<T> findAllByTenantId(
        UUID tenantId,
        org.springframework.data.domain.Pageable pageable
    );

    /**
     * Deletes all entities belonging to the specified tenant.
     * <p>
     * This is a potentially dangerous operation that should only be used in
     * specific scenarios such as tenant offboarding or data cleanup.
     * Consider adding additional safeguards or confirmation steps in your service layer.
     * </p>
     *
     * @param tenantId the unique identifier of the tenant whose data will be deleted (UUID)
     * @return the number of entities deleted
     * @throws IllegalArgumentException if tenantId is null
     */
    @Transactional
    @Query("{ 'tenantId': ?0 }")
    Long deleteByTenantId(UUID tenantId);

    /**
     * Counts the total number of entities belonging to the specified tenant.
     * <p>
     * Useful for quota management, billing calculations, and monitoring
     * tenant resource usage.
     * </p>
     *
     * @param tenantId the unique identifier of the tenant (UUID)
     * @return the count of entities for the tenant, 0 if none exist
     * @throws IllegalArgumentException if tenantId is null
     */
    @Transactional(readOnly = true)
    @Query("{ 'tenantId': ?0 }")
    long countByTenantId(UUID tenantId);

    /**
     * Checks if any entities exist for the specified tenant.
     * <p>
     * This is more efficient than countByTenantId() when you only need to know
     * if at least one entity exists.
     * </p>
     *
     * @param tenantId the unique identifier of the tenant (UUID)
     * @return true if at least one entity exists for the tenant
     * @throws IllegalArgumentException if tenantId is null
     */
    @Transactional(readOnly = true)
    @Query("{ 'tenantId': ?0 }")
    boolean existsByTenantId(UUID tenantId);

    /**
     * Finds an entity by its ID and tenant ID.
     * <p>
     * This method provides an additional safety layer by requiring both
     * the entity ID and tenant ID, preventing cross-tenant data access.
     * </p>
     *
     * @param id the entity's unique identifier
     * @param tenantId the tenant's unique identifier (UUID)
     * @return an Optional containing the entity if found and belongs to the tenant
     * @throws IllegalArgumentException if id or tenantId is null
     */
    @Transactional(readOnly = true)
    @Query("{ '_id': ?0, 'tenantId': ?1 }")
    Optional<T> findByIdAndTenantId(ID id, UUID tenantId);

    /**
     * Deletes an entity by its ID and tenant ID.
     * <p>
     * This method ensures that entities can only be deleted if they belong
     * to the specified tenant, preventing accidental deletion of other tenants' data.
     * </p>
     *
     * @param id the entity's unique identifier
     * @param tenantId the tenant's unique identifier (UUID)
     * @throws IllegalArgumentException if id or tenantId is null
     * @throws org.springframework.dao.EmptyResultDataAccessException if entity not found
     */
    @Transactional
    @Query("{ '_id': ?0, 'tenantId': ?1 }")
    void deleteByIdAndTenantId(ID id, UUID tenantId);
}
