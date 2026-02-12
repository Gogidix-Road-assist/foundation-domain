package com.gogidix.rapidassist.orchestration.alerting_service.shared.requestcontext;

import java.util.Optional;

/**
 * ThreadLocal holder for RequestContext
 * Context is set by TenantInterceptor and cleared after request completes
 */
public final class RequestContextHolder {

    private static final ThreadLocal<RequestContext> CONTEXT = new ThreadLocal<>();

    private RequestContextHolder() {}

    /**
     * Set the context for the current thread
     * Called by TenantInterceptor
     */
    public static void set(RequestContext context) {
        CONTEXT.set(context);
    }

    /**
     * Get the context for the current thread
     * Returns context or throws if not set
     */
    public static RequestContext get() {
        RequestContext context = CONTEXT.get();
        if (context == null) {
            throw new IllegalStateException("RequestContext not set. TenantInterceptor must run first.");
        }
        return context;
    }

    /**
     * Clear the context for the current thread
     * Called after request completes
     */
    public static void clear() {
        CONTEXT.remove();
    }
}
