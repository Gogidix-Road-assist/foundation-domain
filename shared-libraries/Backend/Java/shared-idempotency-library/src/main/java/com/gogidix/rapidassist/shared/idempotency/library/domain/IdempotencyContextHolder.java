package com.gogidix.rapidassist.shared.idempotency.library.domain;

import java.util.Optional;

public final class IdempotencyContextHolder {

    private IdempotencyContextHolder() {
    }

    private static final ThreadLocal<IdempotencyContext> CONTEXT = new ThreadLocal<>();

    public static void set(IdempotencyContext context) {
        CONTEXT.set(context);
    }

    public static Optional<IdempotencyContext> get() {
        return Optional.ofNullable(CONTEXT.get());
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
