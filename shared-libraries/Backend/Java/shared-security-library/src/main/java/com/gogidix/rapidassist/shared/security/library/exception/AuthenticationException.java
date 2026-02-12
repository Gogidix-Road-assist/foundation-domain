package com.gogidix.rapidassist.shared.security.library.exception;

/**
 * Exception thrown when authentication fails.
 */
public class AuthenticationException extends SecurityException {

    private final String authenticationType;

    public AuthenticationException(String message) {
        super("AUTH_FAILED", message);
        this.authenticationType = "UNKNOWN";
    }

    public AuthenticationException(String authenticationType, String message) {
        super("AUTH_FAILED", message);
        this.authenticationType = authenticationType;
    }

    public AuthenticationException(String authenticationType, String message, Throwable cause) {
        super("AUTH_FAILED", message, cause);
        this.authenticationType = authenticationType;
    }

    public String getAuthenticationType() {
        return authenticationType;
    }
}
