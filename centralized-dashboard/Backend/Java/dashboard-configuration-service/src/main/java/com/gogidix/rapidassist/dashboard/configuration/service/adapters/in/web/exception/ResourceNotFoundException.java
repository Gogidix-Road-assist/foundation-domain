package com.gogidix.rapidassist.dashboard.configuration.service.adapters.in.web.exception;

/**
 * Exception thrown when a requested resource is not found.
 *
 * <p>This exception is typically thrown when attempting to retrieve, update,
 * or delete a dashboard or related resource that does not exist.</p>
 *
 * @author Rapid Assist Team
 * @since 1.0.0
 */
public class ResourceNotFoundException extends RuntimeException {

    private final String resourceType;
    private final String resourceId;

    /**
     * Constructs a new ResourceNotFoundException.
     *
     * @param resourceType the type of resource that was not found (e.g., "Dashboard", "Widget")
     * @param resourceId   the ID of the resource that was not found
     */
    public ResourceNotFoundException(String resourceType, String resourceId) {
        super(String.format("%s not found with ID: %s", resourceType, resourceId));
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

    /**
     * Constructs a new ResourceNotFoundException with a custom message.
     *
     * @param resourceType the type of resource that was not found
     * @param resourceId   the ID of the resource that was not found
     * @param message      custom detail message
     */
    public ResourceNotFoundException(String resourceType, String resourceId, String message) {
        super(message);
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public String getResourceId() {
        return resourceId;
    }
}
