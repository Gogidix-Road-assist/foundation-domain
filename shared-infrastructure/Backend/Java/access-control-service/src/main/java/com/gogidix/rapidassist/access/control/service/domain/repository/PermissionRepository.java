package com.gogidix.rapidassist.access.control.service.domain.repository;

import com.gogidix.rapidassist.access.control.service.domain.model.Permission;

import java.util.List;
import java.util.Optional;

/**
 * Domain Repository: PermissionRepository
 *
 * Repository interface for Permission aggregates.
 * This is a PORT in the hexagonal architecture - defines what the domain needs,
 * without specifying how it's implemented.
 *
 * Implementations must ensure tenant isolation at all times.
 */
public interface PermissionRepository {

    /**
     * Save a permission (create or update).
     * Must preserve tenant isolation.
     */
    Permission save(Permission permission);

    /**
     * Find a permission by ID.
     * MUST filter by tenantId to ensure tenant isolation.
     */
    Optional<Permission> findById(String id, String tenantId);

    /**
     * Find all permissions for a subject within a tenant.
     */
    List<Permission> findBySubjectId(String subjectId, String tenantId);

    /**
     * Find all permissions for a tenant.
     */
    List<Permission> findByTenantId(String tenantId);

    /**
     * Find all valid (active and not expired) permissions for a subject.
     */
    List<Permission> findValidBySubjectId(String subjectId, String tenantId);

    /**
     * Find permissions matching resource and action for a subject.
     */
    List<Permission> findBySubjectAndResourceAndAction(String subjectId, String resource, String action, String tenantId);

    /**
     * Find all permissions associated with a role.
     */
    List<Permission> findByRole(String roleId, String tenantId);

    /**
     * Delete a permission by ID.
     * MUST filter by tenantId to prevent cross-tenant deletion.
     */
    boolean deleteById(String id, String tenantId);

    /**
     * Delete all permissions for a subject.
     */
    int deleteBySubjectId(String subjectId, String tenantId);

    /**
     * Check if a permission exists for the given criteria.
     */
    boolean exists(String subjectId, String resource, String action, String tenantId);
}
