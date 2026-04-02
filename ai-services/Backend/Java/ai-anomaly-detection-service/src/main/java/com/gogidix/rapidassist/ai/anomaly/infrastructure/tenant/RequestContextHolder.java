package com.gogidix.rapidassist.ai.anomaly.infrastructure.tenant;

/**
 * ThreadLocal holder for RequestContext.
 * Context is set by TenantInterceptor and cleared after request completes.
 */
public final class RequestContextHolder {

    private static final ThreadLocal<RequestContext> CONTEXT = new ThreadLocal<>();

    private RequestContextHolder() {}

    /**
     * Set the context for the current thread.
     * Called by TenantInterceptor.
     */
    public static void set(RequestContext context) {
        CONTEXT.set(context);
    }

    /**
     * Get the context for the current thread.
     * Returns Optional.empty() if not set (should NOT happen in normal flow).
     */
    public static java.util.Optional<RequestContext> get() {
        return java.util.Optional.ofNullable(CONTEXT.get());
    }

    /**
     * Get the context or throw if not set.
     * Use this when context is required.
     */
    public static RequestContext require() {
        return get().orElseThrow(() ->
            new IllegalStateException("RequestContext not set. TenantInterceptor must run first."));
    }

    /**
     * Clear the context for the current thread.
     * Called after request completes.
     */
    public static void clear() {
        CONTEXT.remove();
    }

    /**
     * Get tenantId from current context.
     * Convenience method.
     */
    public static String getTenantId() {
        return require().getTenantId();
    }

    /**
     * Get userId from current context.
     * Convenience method.
     */
    public static java.util.Optional<String> getUserId() {
        return get().map(RequestContext::getUserId).orElse(java.util.Optional.empty());
    }

    /**
     * Get correlationId from current context.
     * Convenience method.
     */
    public static String getCorrelationId() {
        return require().getCorrelationId().orElse(null);
    }
}
