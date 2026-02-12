package com.gogidix.rapidassist.access.control.service.domain.port.in;

import com.gogidix.rapidassist.access.control.service.domain.model.Permission;

/**
 * Input Port: GrantPermissionCommand
 *
 * Command interface for granting permissions.
 * This is a use case interface in the hexagonal architecture.
 *
 * Implementations must:
 * - Validate tenant context
 * - Check authorization of requester
 * - Create and persist the permission
 * - Publish domain events
 */
public interface GrantPermissionCommand {

    /**
     * Grant a permission to a subject.
     *
     * @param tenantId The tenant context
     * @param subjectId The subject to grant permission to
     * @param subjectType The type of subject (USER, SERVICE, ROLE)
     * @param resource The resource path
     * @param action The action allowed
     * @param effect The effect (ALLOW or DENY)
     * @param grantedBy The user/service granting the permission
     * @param validUntil Optional expiration time
     * @return The created Permission
     */
    Permission grant(String tenantId, String subjectId, String subjectType,
                    String resource, String action, String effect,
                    String grantedBy, java.time.Instant validUntil);

    /**
     * Grant a permission with additional conditions.
     *
     * @param tenantId The tenant context
     * @param request The grant permission request
     * @return The created Permission
     */
    Permission grant(GrantPermissionRequest request);
}
