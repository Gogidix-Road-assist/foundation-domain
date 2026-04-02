package com.gogidix.rapidassist.access.control.service.domain.port.in;

/**
 * Input Port: RevokePermissionCommand
 *
 * Command interface for revoking permissions.
 * This is a use case interface in the hexagonal architecture.
 */
public interface RevokePermissionCommand {

    /**
     * Revoke a permission by ID.
     *
     * @param tenantId The tenant context
     * @param permissionId The permission to revoke
     * @param revokedBy The user/service revoking the permission
     * @return true if permission was revoked, false if not found
     */
    boolean revoke(String tenantId, String permissionId, String revokedBy);

    /**
     * Revoke all permissions for a subject.
     *
     * @param tenantId The tenant context
     * @param subjectId The subject whose permissions should be revoked
     * @param revokedBy The user/service revoking the permissions
     * @return Number of permissions revoked
     */
    int revokeAllForSubject(String tenantId, String subjectId, String revokedBy);

    /**
     * Revoke a specific permission (subject, resource, action combination).
     *
     * @param tenantId The tenant context
     * @param subjectId The subject whose permission should be revoked
     * @param resource The resource path
     * @param action The action to revoke
     * @param revokedBy The user/service revoking the permission
     * @return true if permission was revoked, false if not found
     */
    boolean revokeSpecific(String tenantId, String subjectId, String resource,
                          String action, String revokedBy);
}
