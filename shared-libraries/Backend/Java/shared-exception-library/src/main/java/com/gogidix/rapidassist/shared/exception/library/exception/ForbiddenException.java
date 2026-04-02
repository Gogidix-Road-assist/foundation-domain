package com.gogidix.rapidassist.shared.exception.library.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;

import java.net.URI;

/**
 * Exception thrown when a client is forbidden from performing an action.
 *
 * <p>Use this exception when the user is authenticated but lacks the necessary
 * permissions to perform the requested action.
 *
 * <p>Example usage:
 * <pre>{@code
 * throw new ForbiddenException("customer-123", "UPDATE");
 * }</pre>
 */
public class ForbiddenException extends ErrorResponseException {

    private static final URI TYPE = URI.create("https://gogidix.com/problems/forbidden");

    /**
     * Creates a new ForbiddenException with resource ID and action.
     *
     * @param resourceId the ID of the resource being accessed
     * @param action     the action that was forbidden
     */
    public ForbiddenException(String resourceId, String action) {
        super(HttpStatus.FORBIDDEN);
        this.setTitle("Access forbidden");
        this.setType(TYPE);
        this.setDetail(String.format("You don't have permission to %s resource '%s'", action, resourceId));
        this.getBody().setProperty("resourceId", resourceId);
        this.getBody().setProperty("action", action);
        // Ensure title is serialized in JSON response
        this.getBody().setProperty("title", "Access forbidden");
    }

    /**
     * Creates a new ForbiddenException with a detail message.
     *
     * @param detail the detail message
     */
    public ForbiddenException(String detail) {
        super(HttpStatus.FORBIDDEN);
        this.setTitle("Access forbidden");
        this.setType(TYPE);
        this.setDetail(detail);
        // Ensure title is serialized in JSON response
        this.getBody().setProperty("title", "Access forbidden");
    }

    /**
     * Creates a new ForbiddenException with required permission.
     *
     * @param permission the required permission that was missing
     */
    public ForbiddenException(String resource, String permission, String detail) {
        super(HttpStatus.FORBIDDEN);
        this.setTitle("Access forbidden");
        this.setType(TYPE);
        this.setDetail(detail);
        this.getBody().setProperty("resource", resource);
        this.getBody().setProperty("requiredPermission", permission);
        // Ensure title is serialized in JSON response
        this.getBody().setProperty("title", "Access forbidden");
    }
}
