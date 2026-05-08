package com.gogidix.rapidassist.shared.request.context.library.exception;

/**
 * Base exception for all tenant-related errors.
 *
 * <p>This exception serves as the parent class for all tenant-specific exceptions
 * in the shared request context library. It provides a common base for exception
 * handling and can be used to catch any tenant-related error.</p>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * try {
 *     String tenantId = TenantContext.getTenantIdOrThrow();
 *     // Process tenant-specific logic
 * } catch (TenantException e) {
 *     // Handle any tenant-related error
 *     logger.error("Tenant error: {}", e.getMessage());
 * }
 * }</pre>
 *
 * @see TenantNotFoundException
 * @see TenantValidationException
 * @see TenantMismatchException
 */
public class TenantException extends RuntimeException {

    private final String tenantId;

    /**
     * Constructs a new tenant exception with the specified detail message.
     *
     * @param message the detail message
     */
    public TenantException(String message) {
        this(message, (String) null);
    }

    /**
     * Constructs a new tenant exception with the specified detail message and tenant ID.
     *
     * @param message  the detail message
     * @param tenantId the tenant ID associated with this exception
     */
    public TenantException(String message, String tenantId) {
        super(message);
        this.tenantId = tenantId;
    }

    /**
     * Constructs a new tenant exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause
     */
    public TenantException(String message, Throwable cause) {
        this(message, null, cause);
    }

    /**
     * Constructs a new tenant exception with the specified detail message, tenant ID, and cause.
     *
     * @param message  the detail message
     * @param tenantId the tenant ID associated with this exception
     * @param cause    the cause
     */
    public TenantException(String message, String tenantId, Throwable cause) {
        super(message, cause);
        this.tenantId = tenantId;
    }

    /**
     * Returns the tenant ID associated with this exception, if available.
     *
     * @return the tenant ID or null if not available
     */
    public String getTenantId() {
        return tenantId;
    }
}
