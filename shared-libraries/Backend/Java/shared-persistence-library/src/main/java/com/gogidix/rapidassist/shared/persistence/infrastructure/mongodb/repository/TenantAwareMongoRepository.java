package com.gogidix.rapidassist.shared.persistence.infrastructure.mongodb.repository;

import com.gogidix.rapidassist.shared.persistence.infrastructure.mongodb.context.MongoTenantContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * MongoDB Repository base interface for tenant-aware data access.
 * <p>
 * This interface extends {@link MongoRepository} with automatic tenant filtering
 * for multi-tenant SaaS applications using MongoDB. All queries automatically
 * include tenant_id filtering to ensure data isolation between tenants.
 * </p>
 * <p>
 * Repositories extending this interface will have their queries automatically
 * scoped to the current tenant from {@link MongoTenantContext}. This prevents
 * cross-tenant data access at the repository layer.
 * </p>
 * <p>
 * <strong>Important:</strong> This repository uses MongoDB query syntax
 * ({@code { 'field': value }}) rather than JPQL. All queries are executed
 * against MongoDB collections.
 * </p>
 * <p>
 * Usage example:
 * <pre>{@code
 * public interface InsurancePolicyRepository extends TenantAwareMongoRepository<InsurancePolicy, String> {
 *     // Custom tenant-scoped queries can be added here
 *     // All will automatically filter by tenant_id
 *
 *     @Query("{ 'tenantId': ?0, 'policyNumber': ?1, 'deleted': false }")
 *     Optional<InsurancePolicy> findByTenantIdAndPolicyNumber(Long tenantId, String policyNumber);
 *
 *     @Query("{ 'tenantId': ?0, 'deleted': false, 'coverageType': ?1 }")
 *     List<InsurancePolicy> findByTenantIdAndCoverageType(Long tenantId, String coverageType);
 * }
 * }</pre>
 * </p>
 * <p>
 * <strong>Note:</strong> The default methods in this interface automatically
 * use the current tenant from {@link MongoTenantContext}. For operations that
 * need to query across all tenants (admin operations), use the explicit
 * {@code findAllIncludingAllTenants()} methods.
 * </p>
 *
 * @param <T>  the domain type the repository manages
 *               (typically a {@link TenantAwareDocument})
 * @param <ID> the type of the id of the entity the repository manages
 * @see MongoTenantContext
 * @see com.gogidix.rapidassist.shared.persistence.infrastructure.mongodb.document.TenantAwareDocument
 */
@NoRepositoryBean
public interface TenantAwareMongoRepository<T, ID> extends MongoRepository<T, ID> {

    /**
     * Retrieves the current tenant ID from the thread context.
     * <p>
     * This method is used internally by all tenant-scoped queries.
     * Returns the current tenant or throws an exception if no tenant is set.
     * </p>
     *
     * @return the current tenant ID
     * @throws IllegalStateException if no tenant ID is set in the context
     */
    default Long getCurrentTenantId() {
        return MongoTenantContext.getTenantId();
    }

    /**
     * Finds an entity by its ID, ensuring it belongs to the current tenant.
     * <p>
     * This method overrides the default findById to include tenant filtering,
     * preventing access to entities from other tenants.
     * </p>
     *
     * @param id the entity ID to find
     * @return Optional containing the entity if found and belongs to current tenant
     */
    @Override
    @Query("{ 'id': ?0, 'tenantId': ?1, 'deleted': false }")
    Optional<T> findById(ID id);

    /**
     * Finds an entity by its ID for a specific tenant.
     * <p>
     * Use this method when you need to explicitly specify the tenant ID,
     * such as in administrative operations or background jobs.
     * </p>
     *
     * @param tenantId the tenant ID to query
     * @param id       the entity ID to find
     * @return Optional containing the entity if found and belongs to specified tenant
     */
    @Query("{ 'tenantId': ?0, 'id': ?1, 'deleted': false }")
    Optional<T> findByIdAndTenantId(@Param("tenantId") Long tenantId, @Param("id") ID id);

    /**
     * Finds all entities for the current tenant.
     * <p>
     * This method filters by the current tenant ID and excludes soft-deleted documents.
     * Use {@link #findAllIncludingAllTenants()} for queries that need all tenants.
     * </p>
     *
     * @return list of all non-deleted entities for the current tenant
     */
    @Query("{ 'tenantId': ?0, 'deleted': false }")
    default List<T> findAll() {
        return findAllByTenantId(getCurrentTenantId());
    }

    /**
     * Finds all entities for a specific tenant.
     * <p>
     * Use this method when you need to explicitly specify the tenant ID.
     * </p>
     *
     * @param tenantId the tenant ID to query
     * @return list of all non-deleted entities for the specified tenant
     */
    @Query("{ 'tenantId': ?0, 'deleted': false }")
    List<T> findAllByTenantId(@Param("tenantId") Long tenantId);

    /**
     * Finds all entities for a specific tenant with pagination.
     * <p>
     * This method is useful for listing entities with pagination support.
     * </p>
     *
     * @param tenantId the tenant ID to query
     * @param pageable the pagination parameters
     * @return page of non-deleted entities for the specified tenant
     */
    @Query("{ 'tenantId': ?0, 'deleted': false }")
    Page<T> findAllByTenantId(@Param("tenantId") Long tenantId, Pageable pageable);

    /**
     * Finds all entities for the current tenant with pagination.
     * <p>
     * This method filters by the current tenant ID and excludes soft-deleted documents.
     * </p>
     *
     * @param pageable the pagination parameters
     * @return page of non-deleted entities for the current tenant
     */
    default Page<T> findAll(Pageable pageable) {
        return findAllByTenantId(getCurrentTenantId(), pageable);
    }

    /**
     * Finds all entities across all tenants, excluding soft-deleted documents.
     * <p>
     * <strong>Warning:</strong> This method bypasses tenant isolation and should
     * only be used for administrative operations. Use with caution.
     * </p>
     *
     * @return list of all non-deleted entities across all tenants
     */
    @Query("{ 'deleted': false }")
    List<T> findAllIncludingAllTenants();

    /**
     * Counts all entities for the current tenant.
     * <p>
     * Only counts non-deleted entities belonging to the current tenant.
     * </p>
     *
     * @return the count of entities for the current tenant
     */
    @Query(value = "{ 'tenantId': ?0, 'deleted': false }", count = true)
    default long count() {
        return countByTenantId(getCurrentTenantId());
    }

    /**
     * Counts all entities for a specific tenant.
     * <p>
     * Only counts non-deleted entities belonging to the specified tenant.
     * </p>
     *
     * @param tenantId the tenant ID to count entities for
     * @return the count of entities for the specified tenant
     */
    @Query(value = "{ 'tenantId': ?0, 'deleted': false }", count = true)
    long countByTenantId(@Param("tenantId") Long tenantId);

    /**
     * Checks if an entity with the given ID exists for the current tenant.
     * <p>
     * Only considers non-deleted entities.
     * </p>
     *
     * @param id the entity ID to check
     * @return true if the entity exists and belongs to the current tenant
     */
    @Query(value = "{ 'id': ?0, 'tenantId': ?1, 'deleted': false }", exists = true)
    default boolean existsById(ID id) {
        return existsByIdAndTenantId(id, getCurrentTenantId());
    }

    /**
     * Checks if an entity with the given ID exists for a specific tenant.
     *
     * @param id       the entity ID to check
     * @param tenantId the tenant ID to check
     * @return true if the entity exists and belongs to the specified tenant
     */
    @Query(value = "{ 'id': ?0, 'tenantId': ?1, 'deleted': false }", exists = true)
    boolean existsByIdAndTenantId(@Param("id") ID id, @Param("tenantId") Long tenantId);

    /**
     * Finds all soft-deleted entities for the current tenant.
     * <p>
     * This method returns only entities marked as deleted.
     * Use for audit trail or recycle bin functionality.
     * </p>
     *
     * @return list of soft-deleted entities for the current tenant
     */
    @Query("{ 'tenantId': ?0, 'deleted': true }")
    List<T> findAllDeletedByTenantId(@Param("tenantId") Long tenantId);

    /**
     * Finds all soft-deleted entities for the current tenant.
     *
     * @return list of soft-deleted entities for the current tenant
     */
    default List<T> findDeleted() {
        return findAllDeletedByTenantId(getCurrentTenantId());
    }

    /**
     * Finds entities created after a specific timestamp for the current tenant.
     * <p>
     * Useful for incremental data synchronization and change tracking.
     * </p>
     *
     * @param timestamp the threshold timestamp
     * @return list of entities created after the timestamp
     */
    @Query("{ 'tenantId': ?0, 'deleted': false, 'createdAt': { $gt: ?1 } }")
    List<T> findByTenantIdAndCreatedAtAfter(@Param("tenantId") Long tenantId,
                                            @Param("timestamp") java.time.LocalDateTime timestamp);

    /**
     * Finds entities updated after a specific timestamp for the current tenant.
     * <p>
     * Useful for incremental data synchronization.
     * </p>
     *
     * @param timestamp the threshold timestamp
     * @return list of entities updated after the timestamp
     */
    @Query("{ 'tenantId': ?0, 'deleted': false, 'updatedAt': { $gt: ?1 } }")
    List<T> findByTenantIdAndUpdatedAtAfter(@Param("tenantId") Long tenantId,
                                            @Param("timestamp") java.time.LocalDateTime timestamp);

    /**
     * Deletes an entity by ID for the current tenant.
     * <p>
     * This performs a soft delete by setting the deleted flag to true.
     * For hard delete, use {@link #deleteById(ID)} with caution.
     * </p>
     *
     * @param id the entity ID to soft delete
     */
    default void softDeleteById(ID id) {
        findById(id).ifPresent(entity -> {
            if (entity instanceof com.gogidix.rapidassist.shared.persistence.infrastructure.mongodb.document.TenantAwareDocument doc) {
                doc.markAsDeleted();
                save(entity);
            }
        });
    }

    /**
     * Restores a soft-deleted entity by ID for the current tenant.
     * <p>
     * Sets the deleted flag back to false, making the entity visible in queries.
     * </p>
     *
     * @param id the entity ID to restore
     */
    default void restoreById(ID id) {
        findById(id).ifPresent(entity -> {
            if (entity instanceof com.gogidix.rapidassist.shared.persistence.infrastructure.mongodb.document.TenantAwareDocument doc) {
                doc.restore();
                save(entity);
            }
        });
    }
}
