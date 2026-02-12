package com.gogidix.rapidassist.orchestration.fleet_policy.shared.requestcontext;

/**
 * Thread-local holder for request context
 */
public class RequestContextHolder {

    private static final ThreadLocal<RequestContext> CONTEXT = new ThreadLocal<>();

    public static void setContext(RequestContext context) {
        CONTEXT.set(context);
    }

    public static RequestContext getContext() {
        RequestContext context = CONTEXT.get();
        if (context == null) {
            // Return default context for development
            return RequestContext.builder()
                    .tenantId("default-tenant")
                    .userId("system-user")
                    .build();
        }
        return context;
    }

    public static String getTenantId() {
        return getContext().getTenantId();
    }

    public static String getUserId() {
        return getContext().getUserId();
    }

    public static String getCorrelationId() {
        return getContext().getCorrelationId();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
