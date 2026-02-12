package com.gogidix.rapidassist.ai.analytics.domain.repository;

import com.gogidix.rapidassist.ai.analytics.domain.model.DataQuery;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for DataQuery
 */
public interface DataQueryRepositoryPort {

    /**
     * Save a data query
     */
    DataQuery save(DataQuery query);

    /**
     * Find query by ID
     */
    Optional<DataQuery> findById(UUID id);

    /**
     * Find query by ID and tenant ID
     */
    Optional<DataQuery> findByIdAndTenantId(UUID id, String tenantId);

    /**
     * Find all queries for a tenant
     */
    List<DataQuery> findByTenantId(String tenantId);

    /**
     * Find active queries for a tenant
     */
    List<DataQuery> findByTenantIdAndIsActive(String tenantId, Boolean isActive);

    /**
     * Find queries by category
     */
    List<DataQuery> findByTenantIdAndCategory(String tenantId, String category);

    /**
     * Delete a query
     */
    void deleteById(UUID id);

    /**
     * Check if query exists
     */
    boolean existsById(UUID id);
}
