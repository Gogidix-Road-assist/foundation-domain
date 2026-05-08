package com.gogidix.rapidassist.shared.exception.library.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;

import java.net.URI;

/**
 * Exception thrown when authentication is required but has failed or not been provided.
 *
 * <p>Use this exception when:
 * <ul>
 *   <li>No authentication credentials were provided</li>
 *   <li>Authentication credentials are invalid</li>
 *   <li>Authentication token has expired</li>
 * </ul>
 *
 * <p>Example usage:
 * <pre>{@code
 * throw new UnauthorizedException("Invalid or expired token");
 * }</pre>
 */
public class UnauthorizedException extends ErrorResponseException {

    private static final URI TYPE = URI.create("https://gogidix.com/problems/unauthorized");

    /**
     * Creates a new UnauthorizedException with a detail message.
     *
     * @param detail the detail message
     */
    public UnauthorizedException(String detail) {
        super(HttpStatus.UNAUTHORIZED);
        this.setTitle("Unauthorized");
        this.setType(TYPE);
        this.setDetail(detail);
        // Ensure title is serialized in JSON response
        this.getBody().setProperty("title", "Unauthorized");
    }

    /**
     * Creates a new UnauthorizedException with a detail message and error code.
     *
     * @param detail    the detail message
     * @param errorCode the application-specific error code
     */
    public UnauthorizedException(String detail, String errorCode) {
        super(HttpStatus.UNAUTHORIZED);
        this.setTitle("Unauthorized");
        this.setType(TYPE);
        this.setDetail(detail);
        this.getBody().setProperty("errorCode", errorCode);
        // Ensure title is serialized in JSON response
        this.getBody().setProperty("title", "Unauthorized");
    }

    /**
     * Creates a new UnauthorizedException for expired token.
     *
     * @return a new UnauthorizedException for expired token
     */
    public static UnauthorizedException expiredToken() {
        UnauthorizedException ex = new UnauthorizedException("Authentication token has expired", "TOKEN_EXPIRED");
        ex.getBody().setProperty("title", "Unauthorized");
        return ex;
    }

    /**
     * Creates a new UnauthorizedException for invalid token.
     *
     * @return a new UnauthorizedException for invalid token
     */
    public static UnauthorizedException invalidToken() {
        UnauthorizedException ex = new UnauthorizedException("Invalid authentication token", "INVALID_TOKEN");
        ex.getBody().setProperty("title", "Unauthorized");
        return ex;
    }

    /**
     * Creates a new UnauthorizedException for missing credentials.
     *
     * @return a new UnauthorizedException for missing credentials
     */
    public static UnauthorizedException missingCredentials() {
        UnauthorizedException ex = new UnauthorizedException("Authentication credentials are required", "MISSING_CREDENTIALS");
        ex.getBody().setProperty("title", "Unauthorized");
        return ex;
    }
}
