package com.gogidix.rapidassist.shared.exception.library.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;

import java.net.URI;

/**
 * Exception thrown when a client request is invalid.
 *
 * <p>Use this exception for general client-side validation errors that don't fit
 * into more specific exception types.
 *
 * <p>Example usage:
 * <pre>{@code
 * throw new BadRequestException("Invalid date format", " startDate must be in ISO-8601 format");
 * }</pre>
 */
public class BadRequestException extends ErrorResponseException {

    private static final URI TYPE = URI.create("https://gogidix.com/problems/bad-request");

    /**
     * Creates a new BadRequestException with a detail message.
     *
     * @param detail the detail message
     */
    public BadRequestException(String detail) {
        super(HttpStatus.BAD_REQUEST);
        this.setTitle("Bad request");
        this.setType(TYPE);
        this.setDetail(detail);
        // Ensure title is serialized in JSON response
        this.getBody().setProperty("title", "Bad request");
    }

    /**
     * Creates a new BadRequestException with a title and detail.
     *
     * @param title  the error title
     * @param detail the detail message
     */
    public BadRequestException(String title, String detail) {
        super(HttpStatus.BAD_REQUEST);
        this.setTitle(title);
        this.setType(TYPE);
        this.setDetail(detail);
        // Ensure title is serialized in JSON response
        this.getBody().setProperty("title", title);
    }

    /**
     * Creates a new BadRequestException with title, detail, and error code.
     *
     * @param title    the error title
     * @param detail   the detail message
     * @param errorCode the application-specific error code
     */
    public BadRequestException(String title, String detail, String errorCode) {
        super(HttpStatus.BAD_REQUEST);
        this.setTitle(title);
        this.setType(TYPE);
        this.setDetail(detail);
        this.getBody().setProperty("errorCode", errorCode);
        // Ensure title is serialized in JSON response
        this.getBody().setProperty("title", title);
    }
}
