package com.gogidix.rapidassist.orchestration.fleetorganization.shared.requestcontext;

import java.util.Optional;

/**
 * ThreadLocal holder for RequestContext
 */
public final class RequestContextHolder {

    private static final ThreadLocal<RequestContext> CONTEXT = new ThreadLocal<>();

    private RequestContextHolder() {}

    public static void set(RequestContext context) {
        CONTEXT.set(context);
    }

    public static Optional<RequestContext> get() {
        return Optional.ofNullable(CONTEXT.get());
    }

    public static RequestContext require() {
        return get().orElseThrow(() ->
            new IllegalStateException("RequestContext not set"));
    }

    public static void clear() {
        CONTEXT.remove();
    }

    public static String getTenantId() {
        return require().tenantId();
    }

    public static Optional<String> getUserId() {
        return get().map(RequestContext::userId);
    }

    public static String getCorrelationId() {
        return require().correlationId();
    }
}
