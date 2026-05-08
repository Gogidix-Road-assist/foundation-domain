package com.gogidix.rapidassist.shared.request.context.library.exception;

/**
 * Exception thrown when a tenant ID is required but not present in the current context.
 *
 * <p>This exception is typically thrown when attempting to access tenant information
 * from {@link com.gogidix.rapidassist.shared.request.context.library.domain.TenantContext}
 * when no tenant ID has been set in the current request context.</p>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * try {
 *     String tenantId = TenantContext.getTenantIdOrThrow();
 *     // Process tenant-specific logic
 * } catch (TenantNotFoundException e) {
 *     logger.error("No tenant context found: {}", e.getMessage());
 *     throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tenant ID is required");
 * }
 * }</pre>
 *
 * @see com.gogidix.rapidassist.shared.request.context.library.domain
 *     .TenantContext#getTenantIdOrThrow()
 */
public class TenantNotFoundException extends TenantException {

    /**
     * Constructs a new tenant not found exception with the specified detail message.
     *
     * @param message the detail message
     */
    public TenantNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new tenant not found exception with a default message.
     */
    public TenantNotFoundException() {
        super("Tenant ID is required but not present in the current request context");
    }

    /**
     * Constructs a new tenant not found exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause
     */
    public TenantNotFoundException(String message, Throwable cause) {
        super(message, (String) null, cause);
    }
}
