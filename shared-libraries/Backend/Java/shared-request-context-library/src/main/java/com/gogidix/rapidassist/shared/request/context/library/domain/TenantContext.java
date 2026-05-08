package com.gogidix.rapidassist.shared.request.context.library.domain;

import java.util.Optional;

/**
 * TenantContext provides a focused wrapper around RequestContext for tenant-specific operations.
 * This class serves as a convenience facade for accessing tenant information from the current
 * request context in a type-safe manner.
 *
 * <p>All methods in this class are thread-safe as they delegate to the thread-local
 * RequestContextHolder.</p>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * // Get current tenant ID
 * String tenantId = TenantContext.getTenantId().orElse("default");
 *
 * // Require tenant ID or throw exception
 * String currentTenant = TenantContext.getTenantIdOrThrow();
 *
 * // Check if tenant is present
 * if (TenantContext.isTenantPresent()) {
 *     // Process tenant-specific logic
 * }
 * }</pre>
 */
public final class TenantContext {

    private TenantContext() {
        // Utility class - prevent instantiation
    }

    /**
     * Retrieves the current tenant ID from the request context.
     *
     * @return Optional containing the tenant ID if present, empty otherwise
     */
    public static Optional<String> getTenantId() {
        return RequestContextHolder.get()
                .map(RequestContext::tenantId);
    }

    /**
     * Retrieves the current tenant ID from the request context.
     *
     * @return the tenant ID
     * @throws com.gogidix.rapidassist.shared.request.context.library.exception.TenantNotFoundException
     *         if no tenant ID is present in the current context
     */
    public static String getTenantIdOrThrow() {
        return getTenantId()
                .orElseThrow(() -> new com.gogidix.rapidassist.shared
                        .request.context.library.exception
                        .TenantNotFoundException(
                        "Tenant ID is required but not present in "
                                + "the current request context"
                ));
    }

    /**
     * Checks if a tenant ID is present in the current request context.
     *
     * @return true if a tenant ID is present, false otherwise
     */
    public static boolean isTenantPresent() {
        return getTenantId().isPresent();
    }

    /**
     * Creates a new RequestContext with the specified tenant ID while preserving
     * all other context values from the current context (if available).
     *
     * <p>If no current context exists, a new context is created with only the tenant ID.</p>
     *
     * @param tenantId the tenant ID to set
     * @return a new RequestContext with the specified tenant ID
     * @throws IllegalArgumentException if tenantId is null or blank
     */
    public static RequestContext withTenantId(final String tenantId) {
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalArgumentException("Tenant ID cannot be null or blank");
        }

        return RequestContextHolder.get()
                .map(existingContext -> RequestContext.builder()
                        .correlationId(existingContext.correlationId())
                        .country(existingContext.country())
                        .tenantId(tenantId)
                        .userId(existingContext.userId())
                        .requestId(existingContext.requestId())
                        .build())
                .orElseGet(() -> RequestContext.builder()
                        .tenantId(tenantId)
                        .build());
    }

    /**
     * Executes the given action with the specified tenant ID.
     *
     * <p>This method temporarily sets the tenant ID in the current context,
     * executes the action, and restores the original context afterward.</p>
     *
     * @param tenantId the tenant ID to use
     * @param action   the action to execute
     * @param <T>      the return type of the action
     * @return the result of the action
     * @throws IllegalArgumentException if tenantId is null or blank
     */
    public static <T> T withTenantId(final String tenantId, final java.util.function.Supplier<T> action) {
        RequestContext originalContext = RequestContextHolder.get().orElse(null);

        try {
            RequestContext newContext = withTenantId(tenantId);
            RequestContextHolder.set(newContext);
            return action.get();
        } finally {
            if (originalContext != null) {
                RequestContextHolder.set(originalContext);
            } else {
                RequestContextHolder.clear();
            }
        }
    }
}
