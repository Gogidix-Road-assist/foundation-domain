package com.gogidix.rapidassist.shared.exception.library.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;

import java.net.URI;

/**
 * Exception thrown when a requested resource is not found.
 *
 * <p>Use this exception when a requested entity cannot be found in the system.
 *
 * <p>Example usage:
 * <pre>{@code
 * throw new NotFoundException("Customer", "customer-123");
 * }</pre>
 */
public class NotFoundException extends ErrorResponseException {

    private static final URI TYPE = URI.create("https://gogidix.com/problems/not-found");

    /**
     * Creates a new NotFoundException with the given entity type and ID.
     *
     * @param entityType the type of entity that was not found
     * @param entityId   the ID of the entity that was not found
     */
    public NotFoundException(String entityType, String entityId) {
        super(HttpStatus.NOT_FOUND);
        this.setTitle("Resource not found");
        this.setType(TYPE);
        this.setDetail(String.format("%s with id '%s' was not found", entityType, entityId));
        this.getBody().setProperty("entityType", entityType);
        this.getBody().setProperty("entityId", entityId);
        // Ensure title is serialized in JSON response
        this.getBody().setProperty("title", "Resource not found");
    }

    /**
     * Creates a new NotFoundException with a custom message.
     *
     * @param message the detail message
     */
    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND);
        this.setTitle("Resource not found");
        this.setType(TYPE);
        this.setDetail(message);
        // Ensure title is serialized in JSON response
        this.getBody().setProperty("title", "Resource not found");
    }

    /**
     * Creates a new NotFoundException with entity type, ID, and custom detail.
     *
     * @param entityType the type of entity that was not found
     * @param entityId   the ID of the entity that was not found
     * @param detail     additional detail about the error
     */
    public NotFoundException(String entityType, String entityId, String detail) {
        super(HttpStatus.NOT_FOUND);
        this.setTitle("Resource not found");
        this.setType(TYPE);
        this.setDetail(detail);
        this.getBody().setProperty("entityType", entityType);
        this.getBody().setProperty("entityId", entityId);
        // Ensure title is serialized in JSON response
        this.getBody().setProperty("title", "Resource not found");
    }
}
