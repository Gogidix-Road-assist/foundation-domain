package com.gogidix.rapidassist.access.control.service.shared.exception;

/**
 * Exception: RoleNotFoundException
 *
 * Thrown when a requested role is not found.
 */
public class RoleNotFoundException extends AccessControlException {

    private final String roleId;
    private final String tenantId;

    public RoleNotFoundException(String roleId, String tenantId) {
        super(String.format("Role not found: id=%s, tenantId=%s", roleId, tenantId), "ROLE_NOT_FOUND");
        this.roleId = roleId;
        this.tenantId = tenantId;
    }

    public String getRoleId() { return roleId; }
    public String getTenantId() { return tenantId; }
}
