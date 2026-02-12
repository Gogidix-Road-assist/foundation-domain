package com.gogidix.rapidassist.shared.request.context.library.domain;

import java.util.Optional;

public final class RequestContextHolder {

    private RequestContextHolder() {
    }

    private static final ThreadLocal<RequestContext> CONTEXT = new ThreadLocal<>();

    public static void set(RequestContext context) {
        CONTEXT.set(context);
    }

    public static Optional<RequestContext> get() {
        return Optional.ofNullable(CONTEXT.get());
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
