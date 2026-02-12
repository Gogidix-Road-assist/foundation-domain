package com.gogidix.rapidassist.ai.categorization.infrastructure.tenant;

import java.util.Optional;

/**
 * ThreadLocal holder for TenantContext.
 * Context is set by TenantInterceptor and cleared after request completes.
 *
 * CRITICAL: All repository queries MUST use this context to filter by tenantId.
 * This prevents cross-tenant data access.
 */
public final class RequestContextHolder {

    private static final ThreadLocal<TenantContext> CONTEXT = new ThreadLocal<>();

    private RequestContextHolder() {
        // Utility class - prevent instantiation
    }

    /**
     * Set the context for the current thread.
     * Called by TenantInterceptor.
     *
     * @param context the tenant context to set
     */
    public static void set(TenantContext context) {
        if (context == null) {
            throw new IllegalArgumentException("TenantContext cannot be null");
        }
        CONTEXT.set(context);
    }

    /**
     * Get the context for the current thread.
     * Returns Optional.empty() if not set (should NOT happen in normal flow).
     *
     * @return Optional containing the context, or empty if not set
     */
    public static Optional<TenantContext> get() {
        return Optional.ofNullable(CONTEXT.get());
    }

    /**
     * Get the context or throw if not set.
     * Use this when context is required.
     *
     * @return the current tenant context
     * @throws IllegalStateException if context is not set
     */
    public static TenantContext require() {
        return get().orElseThrow(() ->
                new IllegalStateException("TenantContext not set. TenantInterceptor must run first.")
        );
    }

    /**
     * Clear the context for the current thread.
     * Called after request completes.
     * CRITICAL: ALWAYS clear to prevent memory leaks and cross-thread contamination.
     */
    public static void clear() {
        CONTEXT.remove();
    }

    /**
     * Get tenantId from current context.
     * Convenience method for repositories.
     *
     * @return the current tenant ID
     * @throws IllegalStateException if context is not set
     */
    public static String getTenantId() {
        return require().tenantId();
    }

    /**
     * Get userId from current context.
     * Convenience method for audit logging.
     *
     * @return Optional containing the user ID, or empty if not set
     */
    public static Optional<String> getUserId() {
        return get().flatMap(TenantContext::userId);
    }

    /**
     * Get correlationId from current context.
     * Convenience method for distributed tracing.
     *
     * @return the correlation ID
     * @throws IllegalStateException if context is not set
     */
    public static String getCorrelationId() {
        return require().correlationId();
    }

    /**
     * Check if context is set.
     * Useful for validation in services.
     *
     * @return true if context is set, false otherwise
     */
    public static boolean isSet() {
        return CONTEXT.get() != null;
    }
}
