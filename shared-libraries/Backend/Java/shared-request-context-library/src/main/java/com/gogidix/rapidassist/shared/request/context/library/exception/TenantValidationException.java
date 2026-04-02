package com.gogidix.rapidassist.shared.request.context.library.exception;

/**
 * Exception thrown when tenant validation fails.
 *
 * <p>This exception is thrown when a tenant ID is present but fails validation checks.
 * Validation failures can include:</p>
 * <ul>
 *   <li>Invalid format (e.g., not matching expected pattern)</li>
 *   <li>Blacklisted tenant ID</li>
 *   <li>Tenant not found in registry</li>
 *   <li>Tenant is inactive or suspended</li>
 *   <li>Any other business rule validation failure</li>
 * </ul>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * public void validateTenant(String tenantId) {
 *     if (!tenantId.matches("^[A-Z0-9_-]+$")) {
 *         throw new TenantValidationException(
 *             "Tenant ID must contain only uppercase letters, numbers, underscore, and hyphen",
 *             tenantId
 *         );
 *     }
 *     // Additional validation logic
 * }
 * }</pre>
 */
public class TenantValidationException extends TenantException {

    private final String validationError;

    /**
     * Constructs a new tenant validation exception with the specified detail message and tenant ID.
     *
     * @param message  the detail message
     * @param tenantId the tenant ID that failed validation
     */
    public TenantValidationException(String message, String tenantId) {
        this(message, tenantId, null);
    }

    /**
     * Constructs a new tenant validation exception with the specified detail message,
     * tenant ID, and validation error code.
     *
     * @param message         the detail message
     * @param tenantId        the tenant ID that failed validation
     * @param validationError the validation error code or category
     */
    public TenantValidationException(String message, String tenantId, String validationError) {
        super(message, tenantId);
        this.validationError = validationError;
    }

    /**
     * Constructs a new tenant validation exception with the specified detail message,
     * tenant ID, validation error code, and cause.
     *
     * @param message         the detail message
     * @param tenantId        the tenant ID that failed validation
     * @param validationError the validation error code or category
     * @param cause           the cause
     */
    public TenantValidationException(String message, String tenantId, String validationError, Throwable cause) {
        super(message, tenantId, cause);
        this.validationError = validationError;
    }

    /**
     * Returns the validation error code or category.
     *
     * @return the validation error or null if not specified
     */
    public String getValidationError() {
        return validationError;
    }
}
