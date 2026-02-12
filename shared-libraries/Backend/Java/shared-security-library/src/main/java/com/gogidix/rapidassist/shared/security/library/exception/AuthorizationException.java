package com.gogidix.rapidassist.shared.security.library.exception;

/**
 * Exception thrown when authorization fails (user lacks required permissions).
 */
public class AuthorizationException extends SecurityException {

    private final String requiredPermission;
    private final String userId;

    public AuthorizationException(String message) {
        super("ACCESS_DENIED", message);
        this.requiredPermission = null;
        this.userId = null;
    }

    public AuthorizationException(String requiredPermission, String userId) {
        super("ACCESS_DENIED", "User " + userId + " does not have required permission: " + requiredPermission);
        this.requiredPermission = requiredPermission;
        this.userId = userId;
    }

    public AuthorizationException(String requiredPermission, String userId, String message) {
        super("ACCESS_DENIED", message);
        this.requiredPermission = requiredPermission;
        this.userId = userId;
    }

    public String getRequiredPermission() {
        return requiredPermission;
    }

    public String getUserId() {
        return userId;
    }
}
