package com.gogidix.rapidassist.shared.exception.library.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;

import java.net.URI;

/**
 * Exception thrown when a request conflicts with the current state of the server.
 *
 * <p>Use this exception for scenarios such as:
 * <ul>
 *   <li>Creating a resource that already exists</li>
 *   <li>Updating a resource with a conflicting version</li>
 *   <li>Concurrent modification conflicts</li>
 * </ul>
 *
 * <p>Example usage:
 * <pre>{@code
 * throw new ConflictException("Customer", "email", "john@example.com");
 * }</pre>
 */
public class ConflictException extends ErrorResponseException {

    private static final URI TYPE = URI.create("https://gogidix.com/problems/conflict");

    /**
     * Creates a new ConflictException with entity type, field, and value.
     *
     * @param entityType the type of entity
     * @param field      the field that caused the conflict
     * @param value      the conflicting value
     */
    public ConflictException(String entityType, String field, String value) {
        super(HttpStatus.CONFLICT);
        this.setTitle("Resource conflict");
        this.setType(TYPE);
        this.setDetail(String.format("%s with %s '%s' already exists", entityType, field, value));
        this.getBody().setProperty("entityType", entityType);
        this.getBody().setProperty("field", field);
        this.getBody().setProperty("value", value);
        // Ensure title is serialized in JSON response
        this.getBody().setProperty("title", "Resource conflict");
    }

    /**
     * Creates a new ConflictException with a detail message.
     *
     * @param detail the detail message
     */
    public ConflictException(String detail) {
        super(HttpStatus.CONFLICT);
        this.setTitle("Resource conflict");
        this.setType(TYPE);
        this.setDetail(detail);
        // Ensure title is serialized in JSON response
        this.getBody().setProperty("title", "Resource conflict");
    }

    /**
     * Creates a new ConflictException with a title and detail.
     *
     * @param title  the error title
     * @param detail the detail message
     */
    public ConflictException(String title, String detail) {
        super(HttpStatus.CONFLICT);
        this.setTitle(title);
        this.setType(TYPE);
        this.setDetail(detail);
        // Ensure title is serialized in JSON response
        this.getBody().setProperty("title", title);
    }
}
