package com.gogidix.rapidassist.shared.security.library.rbac;

import com.gogidix.rapidassist.shared.security.library.exception.AuthorizationException;
import com.gogidix.rapidassist.shared.security.library.permission.PermissionChecker;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Role-Based Access Control (RBAC) service implementation.
 * Provides centralized authorization logic.
 */
@Component
public class RBACService {

    private final PermissionChecker permissionChecker;
    private final Map<String, RoleDefinition> roleDefinitions;
    private final Map<String, Set<String>> userRolesCache;
    private final Map<String, Set<String>> rolePermissionsCache;

    public RBACService(PermissionChecker permissionChecker) {
        this.permissionChecker = permissionChecker;
        this.roleDefinitions = new HashMap<>();
        this.userRolesCache = new HashMap<>();
        this.rolePermissionsCache = new HashMap<>();
        initializeDefaultRoles();
    }

    /**
     * Initialize default system roles
     */
    private void initializeDefaultRoles() {
        // Super Admin - full access
        RoleDefinition superAdmin = new RoleDefinition("SUPER_ADMIN", "Super Administrator", Set.of("*"));
        roleDefinitions.put("SUPER_ADMIN", superAdmin);

        // Admin - full access except system configuration
        RoleDefinition admin = new RoleDefinition("ADMIN", "Administrator", Set.of(
            "user:*", "role:*", "permission:*",
            "service:request:*", "vehicle:*", "customer:*",
            "provider:*", "report:*", "configuration:read"
        ));
        roleDefinitions.put("ADMIN", admin);

        // Manager - manage users and view reports
        RoleDefinition manager = new RoleDefinition("MANAGER", "Manager", Set.of(
            "user:read", "user:update", "user:create",
            "service:request:read", "service:request:update",
            "vehicle:read", "customer:read", "customer:update",
            "report:read", "dashboard:read"
        ));
        roleDefinitions.put("MANAGER", manager);

        // Provider - service provider access
        RoleDefinition provider = new RoleDefinition("PROVIDER", "Service Provider", Set.of(
            "service:request:read", "service:request:update",
            "location:update", "schedule:read", "schedule:update",
            "job:read", "job:update"
        ));
        roleDefinitions.put("PROVIDER", provider);

        // Dispatcher - dispatch operations
        RoleDefinition dispatcher = new RoleDefinition("DISPATCHER", "Dispatcher", Set.of(
            "service:request:read", "service:request:update",
            "service:request:assign", "provider:read",
            "location:read", "map:read"
        ));
        roleDefinitions.put("DISPATCHER", dispatcher);

        // Customer - basic customer access
        RoleDefinition customer = new RoleDefinition("CUSTOMER", "Customer", Set.of(
            "service:request:create", "service:request:read", "service:request:cancel",
            "vehicle:read", "vehicle:create", "vehicle:update",
            "profile:read", "profile:update",
            "payment:create", "payment:read"
        ));
        roleDefinitions.put("CUSTOMER", customer);

        // Viewer - read-only access
        RoleDefinition viewer = new RoleDefinition("VIEWER", "Viewer", Set.of(
            "service:request:read", "vehicle:read",
            "customer:read", "report:read"
        ));
        roleDefinitions.put("VIEWER", viewer);
    }

    /**
     * Get all permissions for a role
     * @param roleName role name
     * @return set of permissions
     */
    public Set<String> getPermissionsForRole(String roleName) {
        RoleDefinition role = roleDefinitions.get(roleName);
        return role != null ? role.getPermissions() : Set.of();
    }

    /**
     * Get all permissions for a user's roles
     * @param userRoles set of user's roles
     * @return combined set of all permissions
     */
    public Set<String> getPermissionsForUserRoles(Set<String> userRoles) {
        Set<String> allPermissions = new HashSet<>();
        for (String role : userRoles) {
            allPermissions.addAll(getPermissionsForRole(role));
        }
        return allPermissions;
    }

    /**
     * Check if user has permission
     * @param userRoles user's roles
     * @param requiredPermission permission to check
     * @return true if user has permission
     */
    public Boolean hasPermission(Set<String> userRoles, String requiredPermission) {
        return permissionChecker.hasAnyPermission(
            getPermissionsForUserRoles(userRoles),
            Set.of(requiredPermission)
        );
    }

    /**
     * Check if user has permission with custom permissions
     * @param userRoles user's roles
     * @param customPermissions user's custom permissions
     * @param requiredPermission permission to check
     * @return true if user has permission
     */
    public Boolean hasPermission(Set<String> userRoles, Set<String> customPermissions, String requiredPermission) {
        Set<String> allPermissions = new HashSet<>(getPermissionsForUserRoles(userRoles));
        if (customPermissions != null) {
            allPermissions.addAll(customPermissions);
        }
        return permissionChecker.hasPermission(allPermissions, requiredPermission);
    }

    /**
     * Require permission or throw exception
     * @param userRoles user's roles
     * @param customPermissions user's custom permissions
     * @param requiredPermission permission to check
     * @param userId user ID for error
     * @throws AuthorizationException if user lacks permission
     */
    public void requirePermission(Set<String> userRoles, Set<String> customPermissions, String requiredPermission, String userId) {
        if (!hasPermission(userRoles, customPermissions, requiredPermission)) {
            throw new AuthorizationException(requiredPermission, userId);
        }
    }

    /**
     * Add custom role
     * @param roleName role name
     * @param description role description
     * @param permissions role permissions
     */
    public void addRole(String roleName, String description, Set<String> permissions) {
        RoleDefinition role = new RoleDefinition(roleName, description, permissions);
        roleDefinitions.put(roleName, role);
    }

    /**
     * Get role definition
     * @param roleName role name
     * @return role definition or null
     */
    public RoleDefinition getRole(String roleName) {
        return roleDefinitions.get(roleName);
    }

    /**
     * Get all available roles
     * @return map of role definitions
     */
    public Map<String, RoleDefinition> getAllRoles() {
        return new HashMap<>(roleDefinitions);
    }

    /**
     * Check if role exists
     * @param roleName role name
     * @return true if role exists
     */
    public Boolean roleExists(String roleName) {
        return roleDefinitions.containsKey(roleName);
    }

    /**
     * Expand wildcard permission to all matching permissions
     * @param wildcardPermission wildcard permission (e.g., "user:*")
     * @return set of matching specific permissions
     */
    public Set<String> expandWildcardPermission(String wildcardPermission) {
        Set<String> expanded = new HashSet<>();
        if ("*".equals(wildcardPermission)) {
            // All permissions - return as is
            expanded.add("*");
        } else if (wildcardPermission.endsWith(":*")) {
            String resource = wildcardPermission.substring(0, wildcardPermission.length() - 2);
            for (RoleDefinition role : roleDefinitions.values()) {
                for (String perm : role.getPermissions()) {
                    if (perm.startsWith(resource + ":") || perm.equals("*")) {
                        expanded.add(perm);
                    }
                }
            }
        } else {
            expanded.add(wildcardPermission);
        }
        return expanded;
    }

    /**
     * Role definition inner class
     */
    public static class RoleDefinition {
        private final String name;
        private final String description;
        private final Set<String> permissions;

        public RoleDefinition(String name, String description, Set<String> permissions) {
            this.name = name;
            this.description = description;
            this.permissions = permissions != null ? permissions : Set.of();
        }

        public String getName() { return name; }
        public String getDescription() { return description; }
        public Set<String> getPermissions() { return permissions; }
    }
}
