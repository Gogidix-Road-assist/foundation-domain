package com.gogidix.rapidassist.access.control.service.domain.port.in;

import com.gogidix.rapidassist.access.control.service.domain.model.Permission;

import java.util.List;

/**
 * Input Port (Query): GetPermissionsQuery
 *
 * Query interface for retrieving permissions.
 * This is a use case interface in the hexagonal architecture.
 *
 * Query operations are read-only and do not modify state.
 */
public interface GetPermissionsQuery {

    /**
     * Get all permissions for a subject.
     *
     * @param tenantId The tenant context
     * @param subjectId The subject to get permissions for
     * @return List of permissions
     */
    List<Permission> getBySubject(String tenantId, String subjectId);

    /**
     * Get all valid (active and not expired) permissions for a subject.
     *
     * @param tenantId The tenant context
     * @param subjectId The subject to get permissions for
     * @return List of valid permissions
     */
    List<Permission> getValidBySubject(String tenantId, String subjectId);

    /**
     * Get a permission by ID.
     *
     * @param tenantId The tenant context
     * @param permissionId The permission ID
     * @return The permission if found
     */
    java.util.Optional<Permission> getById(String tenantId, String permissionId);

    /**
     * Get all permissions for a tenant.
     *
     * @param tenantId The tenant context
     * @return List of all permissions for the tenant
     */
    List<Permission> getAllByTenant(String tenantId);

    /**
     * Get permissions matching resource and action for a subject.
     *
     * @param tenantId The tenant context
     * @param subjectId The subject to get permissions for
     * @param resource The resource path
     * @param action The action
     * @return List of matching permissions
     */
    List<Permission> getBySubjectAndResourceAndAction(String tenantId, String subjectId,
                                                      String resource, String action);
}
