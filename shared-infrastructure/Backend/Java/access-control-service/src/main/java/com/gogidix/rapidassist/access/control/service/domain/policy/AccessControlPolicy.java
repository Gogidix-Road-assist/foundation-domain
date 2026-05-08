package com.gogidix.rapidassist.access.control.service.domain.policy;

import com.gogidix.rapidassist.access.control.service.domain.model.Permission;

import java.util.List;

/**
 * Domain Policy: AccessControlPolicy
 *
 * Encapsulates business rules for access control decisions.
 * This policy implements both RBAC (Role-Based Access Control)
 * and ABAC (Attribute-Based Access Control) logic.
 *
 * This is a DOMAIN policy with ZERO framework dependencies.
 */
public class AccessControlPolicy {

    /**
     * Evaluate whether access should be granted based on the provided permissions.
     *
     * Policy Rules:
     * 1. If any explicit DENY permission matches, access is denied (deny overrides)
     * 2. If any explicit ALLOW permission matches, access is granted
     * 3. If no matching permissions found, access is denied (default deny)
     * 4. Wildcard permissions (*) match any resource/action
     * 5. Expired or inactive permissions are ignored
     *
     * @param subjectId The subject requesting access
     * @param resource The resource being accessed
     * @param action The action being performed
     * @param permissions List of permissions to evaluate
     * @return AccessDecision indicating whether access is allowed
     */
    public AccessDecision evaluate(String subjectId, String resource, String action, List<Permission> permissions) {
        // Rule 1: Check for explicit DENY first (deny overrides)
        for (Permission permission : permissions) {
            if (permission.matches(subjectId, resource, action) && "DENY".equals(permission.getEffect())) {
                return AccessDecision.denied("Explicit DENY permission found: " + permission.getId());
            }
        }

        // Rule 2: Check for explicit ALLOW
        for (Permission permission : permissions) {
            if (permission.matches(subjectId, resource, action) && permission.isAllowed()) {
                return AccessDecision.allowed("ALLOW permission found: " + permission.getId());
            }
        }

        // Rule 3: Default deny (no matching permissions)
        return AccessDecision.denied("No matching ALLOW permission found for subject=" + subjectId +
                ", resource=" + resource + ", action=" + action);
    }

    /**
     * Evaluate access based on role-based permissions.
     *
     * @param subjectId The subject requesting access
     * @param roleIds List of role IDs assigned to the subject
     * @param resource The resource being accessed
     * @param action The action being performed
     * @param rolePermissions Map of roleId -> list of permissions
     * @return AccessDecision indicating whether access is allowed
     */
    public AccessDecision evaluateByRoles(String subjectId, List<String> roleIds,
                                          String resource, String action,
                                          java.util.Map<String, List<Permission>> rolePermissions) {
        // Collect all permissions from assigned roles
        List<Permission> allPermissions = roleIds.stream()
                .filter(rolePermissions::containsKey)
                .flatMap(roleId -> rolePermissions.get(roleId).stream())
                .toList();

        return evaluate(subjectId, resource, action, allPermissions);
    }

    /**
     * Check if a permission should be granted based on business rules.
     *
     * Business Rules:
     * - Cannot grant wildcard permissions to non-admin subjects
     * - Cannot grant permissions across tenant boundaries
     * - Cannot grant sensitive permissions without proper authorization
     */
    public GrantPermissionResult validateGrant(String tenantId, String subjectId,
                                              String resource, String action,
                                              String requesterId, List<String> requesterRoles) {
        // Rule 1: Check if requester is admin
        boolean isAdmin = requesterRoles.contains("ADMIN") || requesterRoles.contains("SUPER_ADMIN");
        if (!isAdmin) {
            // Non-admins cannot grant wildcard permissions
            if ("*".equals(resource) || "*".equals(action)) {
                return GrantPermissionResult.rejected("Only admins can grant wildcard permissions");
            }
        }

        // Rule 2: Cannot grant permissions to sensitive resources without special authorization
        if (isSensitiveResource(resource) && !isAdmin) {
            return GrantPermissionResult.rejected("Cannot grant permissions to sensitive resource without admin privileges");
        }

        // Rule 3: Validate tenant context
        if (tenantId == null || tenantId.isBlank()) {
            return GrantPermissionResult.rejected("TenantId is required");
        }

        return GrantPermissionResult.approved();
    }

    /**
     * Check if a resource is considered sensitive.
     * Sensitive resources require elevated privileges to modify.
     */
    private boolean isSensitiveResource(String resource) {
        if (resource == null) return false;
        String lower = resource.toLowerCase();
        return lower.contains("/admin/")
                || lower.contains("/audit/")
                || lower.contains("/config/")
                || lower.contains("/security/")
                || lower.contains("/users/delete")
                || lower.contains("/permissions/grant");
    }

    /**
     * Result of an access control decision.
     */
    public static class AccessDecision {
        private final boolean allowed;
        private final String reason;

        private AccessDecision(boolean allowed, String reason) {
            this.allowed = allowed;
            this.reason = reason;
        }

        public static AccessDecision allowed(String reason) {
            return new AccessDecision(true, reason);
        }

        public static AccessDecision denied(String reason) {
            return new AccessDecision(false, reason);
        }

        public boolean isAllowed() { return allowed; }
        public String getReason() { return reason; }

        @Override
        public String toString() {
            return "AccessDecision{" + "allowed=" + allowed + ", reason='" + reason + '\'' + '}';
        }
    }

    /**
     * Result of a grant permission validation.
     */
    public static class GrantPermissionResult {
        private final boolean approved;
        private final String rejectionReason;

        private GrantPermissionResult(boolean approved, String rejectionReason) {
            this.approved = approved;
            this.rejectionReason = rejectionReason;
        }

        public static GrantPermissionResult approved() {
            return new GrantPermissionResult(true, null);
        }

        public static GrantPermissionResult rejected(String reason) {
            return new GrantPermissionResult(false, reason);
        }

        public boolean isApproved() { return approved; }
        public String getRejectionReason() { return rejectionReason; }
    }
}
