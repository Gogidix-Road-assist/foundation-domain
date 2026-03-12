package com.gogidix.rapidassist.shared.persistence.infrastructure.jpa.repository;

import com.gogidix.rapidassist.shared.request.context.library.jpa.JpaTenantContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Base repository interface for tenant-aware JPA repositories in a multi-tenant system.
 * <p>
 * This interface extends JpaRepository to provide tenant-specific CRUD operations
 * while maintaining data isolation between tenants. All repository interfaces
 * for tenant-aware JPA entities should extend this interface instead of JpaRepository directly.
 * </p>
 * <p>
 * The interface provides automatic tenant filtering for common query operations,
 * ensuring that applications cannot accidentally access data from other tenants.
 * </p>
 * <p>
 * Usage example:
 * <pre>{@code
 * public interface PolicyRepository
 *     extends TenantAwareJpaRepository<Policy, Long> {
 *
 *     // Custom tenant-scoped queries can be added here
 *     List<Policy> findByTenantIdAndStatus(Long tenantId, PolicyStatus status);
 *
 *     // Spring Data JPA automatically adds tenantId filtering
 *     // findByIdAndTenantId is inherited from TenantAwareJpaRepository
 * }
 * }</pre>
 * </p>
 *
 * @param <T> the entity type managed by this repository, must extend TenantAwareJpaEntity
 * @param <ID> the type of the entity's identifier (typically Long)
 * @author Gogidix Platform Team
 * @version 1.0
 * @since 2026-03-10
 */
@NoRepositoryBean
public interface TenantAwareJpaRepository<T, ID> extends JpaRepository<T, ID> {

    /**
     * Retrieves the current tenant ID from the request context.
     * <p>
     * This is a convenience method that uses the JpaTenantContext to get the
     * current tenant ID as a Long value.
     * </p>
     *
     * @return the current tenant ID, or default tenant ID (1L) if not present
     */
    default Long getCurrentTenantId() {
        return JpaTenantContext.getTenantIdOrDefault();
    }

    /**
     * Retrieves all entities belonging to the specified tenant.
     * <p>
     * This method ensures complete tenant isolation by only returning entities
     * that match the provided tenant ID. Use this method in conjunction with
     * the request context's tenant identifier.
     * </p>
     *
     * @param tenantId the unique identifier of the tenant
     * @return a list of all entities belonging to the tenant, empty list if none found
     * @throws IllegalArgumentException if tenantId is null
     */
    @Transactional(readOnly = true)
    @Query("select e from #{#entityName} e where e.tenantId = :tenantId")
    List<T> findAllByTenantId(@Param("tenantId") Long tenantId);

    /**
     * Retrieves all entities belonging to the specified tenant with pagination support.
     * <p>
     * This method provides paginated access to tenant-specific data, useful for
     * implementing list views with large datasets.
     * </p>
     *
     * @param tenantId the unique identifier of the tenant
     * @param pageable the pagination parameters
     * @return a page of entities belonging to the tenant
     * @throws IllegalArgumentException if tenantId is null
     */
    @Transactional(readOnly = true)
    @Query("select e from #{#entityName} e where e.tenantId = :tenantId")
    Page<T> findAllByTenantId(@Param("tenantId") Long tenantId, Pageable pageable);

    /**
     * Deletes all entities belonging to the specified tenant.
     * <p>
     * This is a potentially dangerous operation that should only be used in
     * specific scenarios such as tenant offboarding or data cleanup.
     * Consider adding additional safeguards or confirmation steps in your service layer.
     * </p>
     *
     * @param tenantId the unique identifier of the tenant whose data will be deleted
     * @return the number of entities deleted
     * @throws IllegalArgumentException if tenantId is null
     */
    @Transactional
    @Query("delete from #{#entityName} e where e.tenantId = :tenantId")
    Long deleteByTenantId(@Param("tenantId") Long tenantId);

    /**
     * Counts the total number of entities belonging to the specified tenant.
     * <p>
     * Useful for quota management, billing calculations, and monitoring
     * tenant resource usage.
     * </p>
     *
     * @param tenantId the unique identifier of the tenant
     * @return the count of entities for the tenant, 0 if none exist
     * @throws IllegalArgumentException if tenantId is null
     */
    @Transactional(readOnly = true)
    @Query("select count(e) from #{#entityName} e where e.tenantId = :tenantId")
    long countByTenantId(@Param("tenantId") Long tenantId);

    /**
     * Checks if any entities exist for the specified tenant.
     * <p>
     * This is more efficient than countByTenantId() when you only need to know
     * if at least one entity exists.
     * </p>
     *
     * @param tenantId the unique identifier of the tenant
     * @return true if at least one entity exists for the tenant
     * @throws IllegalArgumentException if tenantId is null
     */
    @Transactional(readOnly = true)
    @Query("select case when count(e) > 0 then true else false end from #{#entityName} e where e.tenantId = :tenantId")
    boolean existsByTenantId(@Param("tenantId") Long tenantId);

    /**
     * Finds an entity by its ID and tenant ID.
     * <p>
     * This method provides an additional safety layer by requiring both
     * the entity ID and tenant ID, preventing cross-tenant data access.
     * </p>
     *
     * @param id       the entity's unique identifier
     * @param tenantId the tenant's unique identifier
     * @return an Optional containing the entity if found and belongs to the tenant
     * @throws IllegalArgumentException if id or tenantId is null
     */
    @Transactional(readOnly = true)
    @Query("select e from #{#entityName} e where e.id = :id and e.tenantId = :tenantId")
    Optional<T> findByIdAndTenantId(@Param("id") ID id, @Param("tenantId") Long tenantId);

    /**
     * Deletes an entity by its ID and tenant ID.
     * <p>
     * This method ensures that entities can only be deleted if they belong
     * to the specified tenant, preventing accidental deletion of other tenants' data.
     * </p>
     *
     * @param id       the entity's unique identifier
     * @param tenantId the tenant's unique identifier
     * @throws IllegalArgumentException if id or tenantId is null
     * @throws org.springframework.dao.EmptyResultDataAccessException if entity not found
     */
    @Transactional
    @Query("delete from #{#entityName} e where e.id = :id and e.tenantId = :tenantId")
    void deleteByIdAndTenantId(@Param("id") ID id, @Param("tenantId") Long tenantId);

    /**
     * Retrieves all entities for the current tenant from the request context.
     * <p>
     * This is a convenience method that automatically uses the current tenant ID
     * from the request context.
     * </p>
     *
     * @return a list of all entities belonging to the current tenant
     */
    @Transactional(readOnly = true)
    @Query("select e from #{#entityName} e where e.tenantId = :tenantId")
    default List<T> findAllForCurrentTenant() {
        return findAllByTenantId(getCurrentTenantId());
    }

    /**
     * Finds an entity by its ID for the current tenant from the request context.
     * <p>
     * This is a convenience method that automatically uses the current tenant ID
     * from the request context.
     * </p>
     *
     * @param id the entity's unique identifier
     * @return an Optional containing the entity if found and belongs to the current tenant
     */
    @Transactional(readOnly = true)
    default Optional<T> findByIdForCurrentTenant(ID id) {
        return findByIdAndTenantId(id, getCurrentTenantId());
    }

    /**
     * Counts entities for the current tenant from the request context.
     *
     * @return the count of entities for the current tenant
     */
    @Transactional(readOnly = true)
    default long countForCurrentTenant() {
        return countByTenantId(getCurrentTenantId());
    }
}
