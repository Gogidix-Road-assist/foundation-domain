package com.gogidix.rapidassist.shared.request.context.library.exception;

/**
 * Exception thrown when a tenant mismatch is detected.
 *
 * <p>This exception is thrown when operations involve multiple tenant IDs that
 * don't match, indicating a potential data integrity or security issue.</p>
 *
 * <p>Common scenarios where this exception is thrown:</p>
 * <ul>
 *   <li>Attempting to access data from a different tenant than the current context</li>
 *   <li>Providing a tenant ID in a request that differs from the authenticated tenant</li>
 *   <li>Attempting to associate resources from different tenants</li>
 *   <li>Cross-tenant operations that are not explicitly allowed</li>
 * </ul>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * public void verifyTenantMatch(String resourceTenantId) {
 *     String currentTenant = TenantContext.getTenantIdOrThrow();
 *     if (!currentTenant.equals(resourceTenantId)) {
 *         throw new TenantMismatchException(
 *             "Resource tenant ID does not match current "
 *                 + "context",
 *             currentTenant,
 *             resourceTenantId
 *         );
 *     }
 * }
 * }</pre>
 */
public class TenantMismatchException extends TenantException {

    private final String expectedTenantId;
    private final String actualTenantId;

    /**
     * Constructs a new tenant mismatch exception with the specified detail message,
     * expected tenant ID, and actual tenant ID.
     *
     * @param message           the detail message
     * @param expectedTenantId  the expected tenant ID
     * @param actualTenantId    the actual tenant ID that caused the mismatch
     */
    public TenantMismatchException(String message, String expectedTenantId, String actualTenantId) {
        super(message, expectedTenantId);
        this.expectedTenantId = expectedTenantId;
        this.actualTenantId = actualTenantId;
    }

    /**
     * Constructs a new tenant mismatch exception with the specified detail message,
     * expected tenant ID, actual tenant ID, and cause.
     *
     * @param message           the detail message
     * @param expectedTenantId  the expected tenant ID
     * @param actualTenantId    the actual tenant ID that caused the mismatch
     * @param cause             the cause
     */
    public TenantMismatchException(String message, String expectedTenantId, String actualTenantId, Throwable cause) {
        super(message, expectedTenantId, cause);
        this.expectedTenantId = expectedTenantId;
        this.actualTenantId = actualTenantId;
    }

    /**
     * Returns the expected tenant ID.
     *
     * @return the expected tenant ID
     */
    public String getExpectedTenantId() {
        return expectedTenantId;
    }

    /**
     * Returns the actual tenant ID that caused the mismatch.
     *
     * @return the actual tenant ID
     */
    public String getActualTenantId() {
        return actualTenantId;
    }

    @Override
    public String getMessage() {
        return String.format("%s [Expected: %s, Actual: %s]",
                super.getMessage(),
                expectedTenantId,
                actualTenantId);
    }
}
