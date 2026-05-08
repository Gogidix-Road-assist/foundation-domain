package com.gogidix.rapidassist.access.control.service.shared.exception;

/**
 * Exception: AccessControlException
 *
 * Base exception for access control related errors.
 */
public class AccessControlException extends RuntimeException {

    private final String errorCode;

    public AccessControlException(String message) {
        super(message);
        this.errorCode = "ACCESS_CONTROL_ERROR";
    }

    public AccessControlException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public AccessControlException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "ACCESS_CONTROL_ERROR";
    }

    public String getErrorCode() {
        return errorCode;
    }
}
