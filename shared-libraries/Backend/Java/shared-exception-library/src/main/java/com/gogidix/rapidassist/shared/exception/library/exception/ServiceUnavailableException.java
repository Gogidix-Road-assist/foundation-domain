package com.gogidix.rapidassist.shared.exception.library.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;

import java.net.URI;

/**
 * Exception thrown when a service is temporarily unavailable.
 *
 * <p>Use this exception when an external service or dependency is unavailable
 * and the request cannot be processed.
 *
 * <p>Example usage:
 * <pre>{@code
 * throw new ServiceUnavailableException("Payment Gateway", "Try again later");
 * }</pre>
 */
public class ServiceUnavailableException extends ErrorResponseException {

    private static final URI TYPE = URI.create("https://gogidix.com/problems/service-unavailable");

    /**
     * Creates a new ServiceUnavailableException with service name and detail.
     *
     * @param serviceName the name of the unavailable service
     * @param detail      additional detail about the error
     */
    public ServiceUnavailableException(String serviceName, String detail) {
        super(HttpStatus.SERVICE_UNAVAILABLE);
        this.setTitle("Service unavailable");
        this.setType(TYPE);
        this.setDetail(String.format("%s: %s", serviceName, detail));
        this.getBody().setProperty("service", serviceName);
        // Ensure title is serialized in JSON response
        this.getBody().setProperty("title", "Service unavailable");
    }

    /**
     * Creates a new ServiceUnavailableException with a detail message.
     *
     * @param detail the detail message
     */
    public ServiceUnavailableException(String detail) {
        super(HttpStatus.SERVICE_UNAVAILABLE);
        this.setTitle("Service unavailable");
        this.setType(TYPE);
        this.setDetail(detail);
        // Ensure title is serialized in JSON response
        this.getBody().setProperty("title", "Service unavailable");
    }

    /**
     * Creates a new ServiceUnavailableException with retry information.
     *
     * @param serviceName   the name of the unavailable service
     * @param detail        additional detail about the error
     * @param retryAfterSeconds suggested retry time in seconds
     */
    public ServiceUnavailableException(String serviceName, String detail, int retryAfterSeconds) {
        super(HttpStatus.SERVICE_UNAVAILABLE);
        this.setTitle("Service unavailable");
        this.setType(TYPE);
        this.setDetail(String.format("%s: %s", serviceName, detail));
        this.getBody().setProperty("service", serviceName);
        this.getBody().setProperty("retryAfter", retryAfterSeconds);
        // Ensure title is serialized in JSON response
        this.getBody().setProperty("title", "Service unavailable");
        getHeaders().set("Retry-After", String.valueOf(retryAfterSeconds));
    }
}
