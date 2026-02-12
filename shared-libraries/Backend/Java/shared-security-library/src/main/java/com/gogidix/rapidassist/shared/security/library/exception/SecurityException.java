package com.gogidix.rapidassist.shared.security.library.exception;

/**
 * Base exception for security-related errors.
 */
public class SecurityException extends RuntimeException {

    private final String errorCode;

    public SecurityException(String message) {
        super(message);
        this.errorCode = "SECURITY_ERROR";
    }

    public SecurityException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public SecurityException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "SECURITY_ERROR";
    }

    public SecurityException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
