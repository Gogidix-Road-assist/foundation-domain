package com.gogidix.rapidassist.access.control.service.domain.port.in;

import com.gogidix.rapidassist.access.control.service.domain.aggregate.RoleAggregate;
import com.gogidix.rapidassist.access.control.service.domain.model.Role;

import java.util.List;

/**
 * Input Port: RoleManagementCommand
 *
 * Command interface for role management operations.
 * This is a use case interface in the hexagonal architecture.
 */
public interface RoleManagementCommand {

    /**
     * Create a new role.
     *
     * @param tenantId The tenant context
     * @param name The role name
     * @param description The role description
     * @param createdBy The user creating the role
     * @return The created Role
     */
    Role createRole(String tenantId, String name, String description, String createdBy);

    /**
     * Update a role.
     *
     * @param tenantId The tenant context
     * @param roleId The role to update
     * @param name The new name
     * @param description The new description
     * @param updatedBy The user updating the role
     * @return The updated Role
     */
    Role updateRole(String tenantId, String roleId, String name,
                   String description, String updatedBy);

    /**
     * Delete a role.
     *
     * @param tenantId The tenant context
     * @param roleId The role to delete
     * @return true if deleted
     */
    boolean deleteRole(String tenantId, String roleId);

    /**
     * Assign a permission to a role.
     *
     * @param tenantId The tenant context
     * @param roleId The role to assign permission to
     * @param permissionId The permission to assign
     * @return The updated RoleAggregate
     */
    RoleAggregate assignPermission(String tenantId, String roleId, String permissionId);

    /**
     * Remove a permission from a role.
     *
     * @param tenantId The tenant context
     * @param roleId The role to remove permission from
     * @param permissionId The permission to remove
     * @return The updated RoleAggregate
     */
    RoleAggregate removePermission(String tenantId, String roleId, String permissionId);

    /**
     * Assign a role to a subject.
     *
     * @param tenantId The tenant context
     * @param subjectId The subject to assign role to
     * @param roleId The role to assign
     * @return true if assigned
     */
    boolean assignRoleToSubject(String tenantId, String subjectId, String roleId);

    /**
     * Remove a role from a subject.
     *
     * @param tenantId The tenant context
     * @param subjectId The subject to remove role from
     * @param roleId The role to remove
     * @return true if removed
     */
    boolean removeRoleFromSubject(String tenantId, String subjectId, String roleId);
}
