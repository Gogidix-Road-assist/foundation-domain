package com.gogidix.rapidassist.ai.analytics.infrastructure.tenant;

/**
 * ThreadLocal holder for request context (tenant, user, correlation)
 * Provides thread-safe access to the current request's context
 */
public class RequestContextHolder {

    private static final ThreadLocal<TenantContext> CONTEXT = new ThreadLocal<>();

    private RequestContextHolder() {
        // Private constructor to prevent instantiation
    }

    /**
     * Set the current request context
     */
    public static void setContext(TenantContext context) {
        CONTEXT.set(context);
    }

    /**
     * Get the current request context
     */
    public static TenantContext getContext() {
        TenantContext context = CONTEXT.get();
        if (context == null) {
            context = new TenantContext();
            CONTEXT.set(context);
        }
        return context;
    }

    /**
     * Get the current tenant ID
     * @throws IllegalStateException if tenant ID is not set
     */
    public static String getTenantId() {
        String tenantId = getContext().getTenantId();
        if (tenantId == null) {
            throw new IllegalStateException("Tenant ID not found in request context");
        }
        return tenantId;
    }

    /**
     * Get the current user ID
     */
    public static String getUserId() {
        return getContext().getUserId();
    }

    /**
     * Get the current correlation ID
     */
    public static String getCorrelationId() {
        return getContext().getCorrelationId();
    }

    /**
     * Clear the current request context
     * Should be called at the end of request processing
     */
    public static void clearContext() {
        CONTEXT.remove();
    }

    /**
     * Check if context is set
     */
    public static boolean hasContext() {
        TenantContext context = CONTEXT.get();
        return context != null && context.getTenantId() != null;
    }
}
