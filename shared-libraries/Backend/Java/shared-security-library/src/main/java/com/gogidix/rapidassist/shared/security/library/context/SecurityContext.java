package com.gogidix.rapidassist.shared.security.library.context;

import java.util.Set;

/**
 * Thread-local holder for security context information.
 * Provides access to current user, roles, and permissions throughout the request.
 */
public class SecurityContext {

    private static final ThreadLocal<UserSecurityContext> CONTEXT = new ThreadLocal<>();

    /**
     * Set the security context for the current thread
     * @param context security context
     */
    public static void setContext(UserSecurityContext context) {
        CONTEXT.set(context);
    }

    /**
     * Get the security context for the current thread
     * @return security context or null if not set
     */
    public static UserSecurityContext getContext() {
        return CONTEXT.get();
    }

    /**
     * Clear the security context for the current thread
     */
    public static void clearContext() {
        CONTEXT.remove();
    }

    /**
     * Get current user ID
     * @return user ID or null if not authenticated
     */
    public static String getCurrentUserId() {
        UserSecurityContext context = CONTEXT.get();
        return context != null ? context.getUserId() : null;
    }

    /**
     * Get current username
     * @return username or null if not authenticated
     */
    public static String getCurrentUsername() {
        UserSecurityContext context = CONTEXT.get();
        return context != null ? context.getUsername() : null;
    }

    /**
     * Get current tenant ID
     * @return tenant ID or null if not set
     */
    public static String getCurrentTenantId() {
        UserSecurityContext context = CONTEXT.get();
        return context != null ? context.getTenantId() : null;
    }

    /**
     * Get current organization ID
     * @return organization ID or null if not set
     */
    public static String getCurrentOrganizationId() {
        UserSecurityContext context = CONTEXT.get();
        return context != null ? context.getOrganizationId() : null;
    }

    /**
     * Get current user's roles
     * @return set of roles or empty set
     */
    public static Set<String> getCurrentRoles() {
        UserSecurityContext context = CONTEXT.get();
        return context != null ? context.getRoles() : Set.of();
    }

    /**
     * Get current user's permissions
     * @return set of permissions or empty set
     */
    public static Set<String> getCurrentPermissions() {
        UserSecurityContext context = CONTEXT.get();
        return context != null ? context.getPermissions() : Set.of();
    }

    /**
     * Check if current user has a specific role
     * @param role role to check
     * @return true if user has the role
     */
    public static Boolean hasRole(String role) {
        Set<String> roles = getCurrentRoles();
        return roles.contains(role) || roles.contains("ADMIN");
    }

    /**
     * Check if current user has a specific permission
     * @param permission permission to check
     * @return true if user has the permission
     */
    public static Boolean hasPermission(String permission) {
        Set<String> permissions = getCurrentPermissions();
        return permissions.contains(permission) || permissions.contains("*");
    }

    /**
     * Check if current user is authenticated
     * @return true if authenticated
     */
    public static Boolean isAuthenticated() {
        return CONTEXT.get() != null;
    }

    /**
     * Check if current user is admin
     * @return true if user has admin role
     */
    public static Boolean isAdmin() {
        return hasRole("ADMIN");
    }

    /**
     * Initialize anonymous context
     */
    public static void setAnonymous() {
        UserSecurityContext context = new UserSecurityContext();
        context.setAuthenticated(false);
        CONTEXT.set(context);
    }

    /**
     * User security context information
     */
    public static class UserSecurityContext {
        private String userId;
        private String username;
        private String tenantId;
        private String organizationId;
        private Set<String> roles;
        private Set<String> permissions;
        private String authenticationType;
        private Boolean authenticated;
        private String requestId;
        private String ipAddress;

        public UserSecurityContext() {
            this.roles = Set.of();
            this.permissions = Set.of();
            this.authenticated = false;
        }

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getTenantId() { return tenantId; }
        public void setTenantId(String tenantId) { this.tenantId = tenantId; }

        public String getOrganizationId() { return organizationId; }
        public void setOrganizationId(String organizationId) { this.organizationId = organizationId; }

        public Set<String> getRoles() { return roles; }
        public void setRoles(Set<String> roles) { this.roles = roles; }

        public Set<String> getPermissions() { return permissions; }
        public void setPermissions(Set<String> permissions) { this.permissions = permissions; }

        public String getAuthenticationType() { return authenticationType; }
        public void setAuthenticationType(String authenticationType) { this.authenticationType = authenticationType; }

        public Boolean getAuthenticated() { return authenticated; }
        public void setAuthenticated(Boolean authenticated) { this.authenticated = authenticated; }

        public String getRequestId() { return requestId; }
        public void setRequestId(String requestId) { this.requestId = requestId; }

        public String getIpAddress() { return ipAddress; }
        public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    }
}
