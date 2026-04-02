package com.gogidix.rapidassist.shared.security.library.permission;

import com.gogidix.rapidassist.shared.security.library.exception.AuthorizationException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utility class for checking user permissions and roles.
 */
@Component
public class PermissionChecker {

    /**
     * Check if user has a specific permission
     * @param userPermissions set of permissions the user has
     * @param requiredPermission permission to check
     * @return true if user has the permission
     */
    public Boolean hasPermission(Set<String> userPermissions, String requiredPermission) {
        if (userPermissions == null || requiredPermission == null) {
            return false;
        }
        return userPermissions.contains(requiredPermission) || userPermissions.contains("*");
    }

    /**
     * Check if user has any of the required permissions
     * @param userPermissions set of permissions the user has
     * @param requiredPermissions collection of permissions to check
     * @return true if user has any of the required permissions
     */
    public Boolean hasAnyPermission(Set<String> userPermissions, Collection<String> requiredPermissions) {
        if (userPermissions == null || requiredPermissions == null) {
            return false;
        }
        if (userPermissions.contains("*")) {
            return true;
        }
        return requiredPermissions.stream().anyMatch(userPermissions::contains);
    }

    /**
     * Check if user has all of the required permissions
     * @param userPermissions set of permissions the user has
     * @param requiredPermissions collection of permissions to check
     * @return true if user has all of the required permissions
     */
    public Boolean hasAllPermissions(Set<String> userPermissions, Collection<String> requiredPermissions) {
        if (userPermissions == null || requiredPermissions == null) {
            return false;
        }
        if (userPermissions.contains("*")) {
            return true;
        }
        return userPermissions.containsAll(requiredPermissions);
    }

    /**
     * Check if user has a specific role
     * @param userRoles set of roles the user has
     * @param requiredRole role to check
     * @return true if user has the role
     */
    public Boolean hasRole(Set<String> userRoles, String requiredRole) {
        if (userRoles == null || requiredRole == null) {
            return false;
        }
        return userRoles.contains(requiredRole) || userRoles.contains("ADMIN");
    }

    /**
     * Check if user has any of the required roles
     * @param userRoles set of roles the user has
     * @param requiredRoles collection of roles to check
     * @return true if user has any of the required roles
     */
    public Boolean hasAnyRole(Set<String> userRoles, Collection<String> requiredRoles) {
        if (userRoles == null || requiredRoles == null) {
            return false;
        }
        if (userRoles.contains("ADMIN")) {
            return true;
        }
        return requiredRoles.stream().anyMatch(userRoles::contains);
    }

    /**
     * Check if user is admin
     * @param userRoles set of roles the user has
     * @return true if user is admin
     */
    public Boolean isAdmin(Set<String> userRoles) {
        return userRoles != null && userRoles.contains("ADMIN");
    }

    /**
     * Check if user can access resource based on ownership
     * @param resourceOwnerId ID of the user who owns the resource
     * @param currentUserId ID of the current user
     * @param userRoles roles of the current user
     * @return true if user is owner or admin
     */
    public Boolean canAccessResource(String resourceOwnerId, String currentUserId, Set<String> userRoles) {
        return resourceOwnerId != null && resourceOwnerId.equals(currentUserId) || isAdmin(userRoles);
    }

    /**
     * Check if user can access resource in their organization
     * @param resourceOrganizationId ID of the organization that owns the resource
     * @param userOrganizationId ID of the user's organization
     * @param userRoles roles of the current user
     * @return true if user is in same organization or is admin
     */
    public Boolean canAccessOrganizationResource(String resourceOrganizationId, String userOrganizationId, Set<String> userRoles) {
        return resourceOrganizationId != null && resourceOrganizationId.equals(userOrganizationId) || isAdmin(userRoles);
    }

    /**
     * Throw exception if user lacks permission
     * @param userPermissions set of permissions the user has
     * @param requiredPermission permission to check
     * @param userId user ID for error reporting
     * @throws AuthorizationException if user lacks permission
     */
    public void requirePermission(Set<String> userPermissions, String requiredPermission, String userId) {
        if (!hasPermission(userPermissions, requiredPermission)) {
            throw new AuthorizationException(requiredPermission, userId);
        }
    }

    /**
     * Throw exception if user lacks any of the required permissions
     * @param userPermissions set of permissions the user has
     * @param requiredPermissions collection of permissions to check
     * @param userId user ID for error reporting
     * @throws AuthorizationException if user lacks all permissions
     */
    public void requireAnyPermission(Set<String> userPermissions, Collection<String> requiredPermissions, String userId) {
        if (!hasAnyPermission(userPermissions, requiredPermissions)) {
            throw new AuthorizationException(requiredPermissions.toString(), userId);
        }
    }

    /**
     * Throw exception if user lacks role
     * @param userRoles set of roles the user has
     * @param requiredRole role to check
     * @param userId user ID for error reporting
     * @throws AuthorizationException if user lacks role
     */
    public void requireRole(Set<String> userRoles, String requiredRole, String userId) {
        if (!hasRole(userRoles, requiredRole)) {
            throw new AuthorizationException(requiredRole, userId, "User does not have required role");
        }
    }

    /**
     * Parse permission string to get resource and action
     * @param permission permission string (e.g., "user:read", "vehicle:update")
     * @return PermissionResource object
     */
    public PermissionResource parsePermission(String permission) {
        if (permission == null || !permission.contains(":")) {
            return new PermissionResource(permission, null);
        }
        String[] parts = permission.split(":");
        return new PermissionResource(parts[0], parts.length > 1 ? parts[1] : null);
    }

    /**
     * Build permission from resource and action
     * @param resource resource type
     * @param action action type
     * @return permission string
     */
    public String buildPermission(String resource, String action) {
        return resource + ":" + action;
    }

    /**
     * Get all permissions for a resource based on role
     * @param role user role
     * @return set of permissions for the role
     */
    public Set<String> getPermissionsForRole(String role) {
        Set<String> permissions = new HashSet<>();

        switch (role) {
            case "ADMIN":
                permissions.add("*");
                break;
            case "MANAGER":
                permissions.addAll(Arrays.asList(
                    "user:read", "user:update",
                    "service:request:read", "service:request:update",
                    "vehicle:read", "vehicle:update",
                    "report:read"
                ));
                break;
            case "PROVIDER":
                permissions.addAll(Arrays.asList(
                    "service:request:read",
                    "service:request:update",
                    "location:update"
                ));
                break;
            case "USER":
                permissions.addAll(Arrays.asList(
                    "service:request:create",
                    "service:request:read",
                    "vehicle:read",
                    "vehicle:create",
                    "profile:read",
                    "profile:update"
                ));
                break;
            default:
                break;
        }

        return permissions;
    }

    /**
     * Filter permissions by resource pattern
     * @param permissions set of all permissions
     * @param resourcePattern pattern to match (e.g., "user:*")
     * @return filtered permissions
     */
    public Set<String> filterByResource(Set<String> permissions, String resourcePattern) {
        return permissions.stream()
                .filter(p -> p.startsWith(resourcePattern.replace("*", "")) || "*".equals(p))
                .collect(Collectors.toSet());
    }

    /**
     * Inner class to represent permission resource
     */
    public static class PermissionResource {
        private final String resource;
        private final String action;

        public PermissionResource(String resource, String action) {
            this.resource = resource;
            this.action = action;
        }

        public String getResource() { return resource; }
        public String getAction() { return action; }
    }
}
