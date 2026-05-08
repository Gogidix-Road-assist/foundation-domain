package com.gogidix.rapidassist.shared.persistence.infrastructure.mongodb.context;

import java.util.Optional;

/**
 * MongoDB-specific Tenant Context for multi-tenancy in MongoDB-based services.
 * <p>
 * This class provides a ThreadLocal-based storage for the current tenant ID,
 * specifically designed for MongoDB document operations. It maintains tenant
 * isolation at the application layer before database operations.
 * </p>
 * <p>
 * The tenant ID is automatically set by the {@link MongoTenantConfiguration.TenantRequestFilter}
 * from the HTTP request headers (X-Tenant-Id) or query parameters.
 * </p>
 * <p>
 * Unlike SQL-based row-level security, MongoDB tenant isolation is achieved through
 * application-level filtering using tenant_id fields in documents.
 * </p>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * // In service layer - tenant is automatically set by filter
 * public void createDocument(MyDocument doc) {
 *     // Automatically filters by tenant_id
 *     repository.save(doc);
 * }
 *
 * // Manual tenant context setting (for background jobs)
 * MongoTenantContext.setTenantId(123L);
 * try {
 *     myDocument.setTenantId(MongoTenantContext.getTenantId());
 *     repository.save(myDocument);
 * } finally {
 *     MongoTenantContext.clear();
 * }
 * }</pre>
 *
 * @see MongoTenantConfiguration
 * @see com.gogidix.rapidassist.shared.persistence.infrastructure.mongodb.document.TenantAwareDocument
 */
public final class MongoTenantContext {

    /**
     * ThreadLocal storage for the current tenant ID.
     * Each request thread has its own isolated tenant context.
     */
    private static final ThreadLocal<Long> CURRENT_TENANT = new ThreadLocal<>();

    /**
     * The default tenant ID used when no tenant is explicitly set.
     * This ensures that tenant_id is never null in documents.
     */
    private static final Long DEFAULT_TENANT = 1L;

    /**
     * Private constructor to prevent instantiation.
     * This is a utility class with only static methods.
     */
    private MongoTenantContext() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Sets the tenant ID for the current thread.
     * <p>
     * This method is typically called by the TenantRequestFilter for each request,
     * but can also be called manually for background processing or testing.
     * </p>
     *
     * @param tenantId the tenant ID to set, or null to use the default tenant
     */
    public static void setTenantId(Long tenantId) {
        CURRENT_TENANT.set(tenantId != null ? tenantId : DEFAULT_TENANT);
    }

    /**
     * Retrieves the current tenant ID from the thread context.
     * <p>
     * If no tenant ID has been set for the current thread, returns the default tenant ID.
     * This ensures that tenant_id is always populated in documents.
     * </p>
     *
     * @return the current tenant ID, or the default tenant ID if none is set
     */
    public static Long getTenantId() {
        Long tenantId = CURRENT_TENANT.get();
        return tenantId != null ? tenantId : DEFAULT_TENANT;
    }

    /**
     * Retrieves the current tenant ID as an Optional.
     * <p>
     * This is useful when you need to distinguish between an explicitly set tenant
     * and the default tenant.
     * </p>
     *
     * @return Optional containing the current tenant ID, or empty if using default
     */
    public static Optional<Long> getTenantIdOptional() {
        return Optional.ofNullable(CURRENT_TENANT.get());
    }

    /**
     * Checks if a tenant ID is currently set in the thread context.
     *
     * @return true if a tenant ID is set (not using default), false otherwise
     */
    public static boolean isTenantSet() {
        return CURRENT_TENANT.get() != null;
    }

    /**
     * Retrieves the current tenant ID or throws an exception if none is set.
     * <p>
     * Use this method when tenant context is required for an operation.
     * </p>
     *
     * @return the current tenant ID
     * @throws IllegalStateException if no tenant ID is set in the current context
     */
    public static Long getTenantIdOrThrow() {
        Long tenantId = CURRENT_TENANT.get();
        if (tenantId == null) {
            throw new IllegalStateException(
                "Tenant ID is required but not present in the current MongoDB context. " +
                "Ensure X-Tenant-Id header is set or MongoTenantContext.setTenantId() is called."
            );
        }
        return tenantId;
    }

    /**
     * Clears the tenant ID from the current thread.
     * <p>
     * This method should be called in a finally block after setting a tenant ID
     * to prevent memory leaks in thread pools. The TenantRequestFilter handles
     * this automatically for HTTP requests.
     * </p>
     * <p>
     * <strong>Important:</strong> Always clear the tenant context after manual setting
     * to avoid passing tenant context to unrelated operations in thread pools.
     * </p>
     */
    public static void clear() {
        CURRENT_TENANT.remove();
    }

    /**
     * Executes the given action with the specified tenant ID.
     * <p>
     * This method temporarily sets the tenant ID in the current context,
     * executes the action, and clears the context afterward.
     * </p>
     * <p>
     * This is particularly useful for background jobs or batch operations
     * that need to process data for multiple tenants.
     * </p>
     *
     * @param tenantId the tenant ID to use for the action
     * @param action   the action to execute
     * @param <T>      the return type of the action
     * @return the result of the action
     * @throws IllegalArgumentException if tenantId is null
     */
    public static <T> T withTenantId(Long tenantId, java.util.function.Supplier<T> action) {
        if (tenantId == null) {
            throw new IllegalArgumentException("Tenant ID cannot be null");
        }

        Long originalTenantId = CURRENT_TENANT.get();
        try {
            setTenantId(tenantId);
            return action.get();
        } finally {
            if (originalTenantId != null) {
                CURRENT_TENANT.set(originalTenantId);
            } else {
                clear();
            }
        }
    }

    /**
     * Executes the given action with the specified tenant ID (void return).
     * <p>
     * This method temporarily sets the tenant ID in the current context,
     * executes the action, and clears the context afterward.
     * </p>
     *
     * @param tenantId the tenant ID to use for the action
     * @param action   the action to execute
     * @throws IllegalArgumentException if tenantId is null
     */
    public static void withTenantId(Long tenantId, Runnable action) {
        withTenantId(tenantId, () -> {
            action.run();
            return null;
        });
    }

    /**
     * Returns the default tenant ID.
     *
     * @return the default tenant ID (1L)
     */
    public static Long getDefaultTenant() {
        return DEFAULT_TENANT;
    }
}
