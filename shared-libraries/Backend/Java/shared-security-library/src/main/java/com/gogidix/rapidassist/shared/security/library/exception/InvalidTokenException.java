package com.gogidix.rapidassist.shared.security.library.exception;

/**
 * Exception thrown when a token is invalid or expired.
 */
public class InvalidTokenException extends SecurityException {

    private final String tokenType;

    public InvalidTokenException(String message) {
        super("INVALID_TOKEN", message);
        this.tokenType = "UNKNOWN";
    }

    public InvalidTokenException(String tokenType, String message) {
        super("INVALID_TOKEN", message);
        this.tokenType = tokenType;
    }

    public InvalidTokenException(String tokenType, String message, Throwable cause) {
        super("INVALID_TOKEN", message, cause);
        this.tokenType = tokenType;
    }

    public String getTokenType() {
        return tokenType;
    }
}
