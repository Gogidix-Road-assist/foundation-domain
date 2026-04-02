package com.gogidix.rapidassist.access.control.service.domain.repository;

import com.gogidix.rapidassist.access.control.service.domain.model.Resource;

import java.util.List;
import java.util.Optional;

/**
 * Domain Repository: ResourceRepository
 *
 * Repository interface for Resource aggregates.
 * This is a PORT in the hexagonal architecture.
 *
 * Implementations must ensure tenant isolation at all times.
 */
public interface ResourceRepository {

    /**
     * Save a resource (create or update).
     * Must preserve tenant isolation.
     */
    Resource save(Resource resource);

    /**
     * Find a resource by ID.
     * MUST filter by tenantId to ensure tenant isolation.
     */
    Optional<Resource> findById(String id, String tenantId);

    /**
     * Find a resource by path within a tenant.
     */
    Optional<Resource> findByResourcePath(String resourcePath, String tenantId);

    /**
     * Find all resources for a tenant.
     */
    List<Resource> findByTenantId(String tenantId);

    /**
     * Find all active resources for a tenant.
     */
    List<Resource> findActiveByTenantId(String tenantId);

    /**
     * Find resources by type within a tenant.
     */
    List<Resource> findByResourceType(String resourceType, String tenantId);

    /**
     * Find resources owned by a specific subject.
     */
    List<Resource> findByOwner(String owner, String tenantId);

    /**
     * Delete a resource by ID.
     * MUST filter by tenantId to prevent cross-tenant deletion.
     */
    boolean deleteById(String id, String tenantId);

    /**
     * Check if a resource path exists within a tenant.
     */
    boolean existsByResourcePath(String resourcePath, String tenantId);
}
