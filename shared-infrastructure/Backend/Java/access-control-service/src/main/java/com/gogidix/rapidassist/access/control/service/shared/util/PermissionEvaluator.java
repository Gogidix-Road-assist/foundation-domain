package com.gogidix.rapidassist.access.control.service.shared.util;

import com.gogidix.rapidassist.access.control.service.domain.model.Permission;

import java.util.List;

/**
 * Utility: PermissionEvaluator
 *
 * Helper class for evaluating permissions.
 */
public class PermissionEvaluator {

    /**
     * Check if any permission in the list allows the given action on the resource.
     */
    public static boolean hasPermission(List<Permission> permissions, String resource, String action) {
        return permissions.stream()
                .filter(Permission::isValid)
                .filter(p -> p.isAllowed())
                .anyMatch(p -> p.matches("subject", resource, action));
    }

    /**
     * Check if any permission in the list denies the given action on the resource.
     * Deny overrides allow.
     */
    public static boolean isDenied(List<Permission> permissions, String resource, String action) {
        return permissions.stream()
                .filter(Permission::isValid)
                .filter(p -> "DENY".equals(p.getEffect()))
                .anyMatch(p -> p.matches("subject", resource, action));
    }

    /**
     * Evaluate permissions and return the effective decision.
     */
    public static boolean evaluate(List<Permission> permissions, String resource, String action) {
        // First check for explicit DENY
        if (isDenied(permissions, resource, action)) {
            return false;
        }
        // Then check for ALLOW
        return hasPermission(permissions, resource, action);
    }
}
