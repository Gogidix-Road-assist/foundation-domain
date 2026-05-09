package com.gogidix.rapidassist.dashboard.configuration.service.adapters.in.web.exception;

/**
 * Exception thrown when a tenant is not found or access is denied.
 *
 * <p>This exception is thrown when a specified tenant does not exist or when
 * the current context does not have permission to access the tenant's data.</p>
 *
 * @author Rapid Assist Team
 * @since 1.0.0
 */
public class TenantNotFoundException extends RuntimeException {

    private final String tenantId;

    /**
     * Constructs a new TenantNotFoundException.
     *
     * @param tenantId the ID of the tenant that was not found
     */
    public TenantNotFoundException(String tenantId) {
        super(String.format("Tenant not found or access denied: %s", tenantId));
        this.tenantId = tenantId;
    }

    /**
     * Constructs a new TenantNotFoundException with a custom message.
     *
     * @param tenantId the ID of the tenant that was not found
     * @param message  custom detail message
     */
    public TenantNotFoundException(String tenantId, String message) {
        super(message);
        this.tenantId = tenantId;
    }

    public String getTenantId() {
        return tenantId;
    }
}
