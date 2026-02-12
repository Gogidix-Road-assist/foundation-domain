package com.gogidix.rapidassist.access.control.service.domain.repository;

import com.gogidix.rapidassist.access.control.service.domain.model.Role;

import java.util.List;
import java.util.Optional;

/**
 * Domain Repository: RoleRepository
 *
 * Repository interface for Role aggregates.
 * This is a PORT in the hexagonal architecture.
 *
 * Implementations must ensure tenant isolation at all times.
 */
public interface RoleRepository {

    /**
     * Save a role (create or update).
     * Must preserve tenant isolation.
     */
    Role save(Role role);

    /**
     * Find a role by ID.
     * MUST filter by tenantId to ensure tenant isolation.
     */
    Optional<Role> findById(String id, String tenantId);

    /**
     * Find a role by name within a tenant.
     */
    Optional<Role> findByName(String name, String tenantId);

    /**
     * Find all roles for a tenant.
     */
    List<Role> findByTenantId(String tenantId);

    /**
     * Find all active roles for a tenant.
     */
    List<Role> findActiveByTenantId(String tenantId);

    /**
     * Find roles for a subject (user/service).
     */
    List<Role> findBySubjectId(String subjectId, String tenantId);

    /**
     * Delete a role by ID.
     * MUST filter by tenantId to prevent cross-tenant deletion.
     */
    boolean deleteById(String id, String tenantId);

    /**
     * Check if a role name exists within a tenant.
     */
    boolean existsByName(String name, String tenantId);
}
