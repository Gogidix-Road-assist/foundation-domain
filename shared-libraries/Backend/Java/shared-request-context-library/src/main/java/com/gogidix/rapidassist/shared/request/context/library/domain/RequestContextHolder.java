package com.gogidix.rapidassist.shared.request.context.library.domain;

import java.util.Optional;

/**
 * Thread-local holder for the current request context.
 *
 * <p>This class provides a thread-safe mechanism for storing and retrieving
 * the request context for the current thread. It uses a {@link ThreadLocal}
 * to ensure that each thread has its own isolated context.</p>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * // Set the request context
 * RequestContextHolder.set(new RequestContext(...));
 *
 * // Get the request context
 * Optional<RequestContext> context = RequestContextHolder.get();
 *
 * // Clear the request context
 * RequestContextHolder.clear();
 * }</pre>
 *
 * @author Rapid Assist
 * @version 1.0.0
 * @since 1.0.0
 */
public final class RequestContextHolder {

    private RequestContextHolder() {
        // Utility class - prevent instantiation
    }

    /**
     * Thread-local storage for the request context.
     */
    private static final ThreadLocal<RequestContext> CONTEXT = new ThreadLocal<>();

    /**
     * Sets the request context for the current thread.
     *
     * @param context the request context to set
     */
    public static void set(final RequestContext context) {
        CONTEXT.set(context);
    }

    /**
     * Gets the request context for the current thread.
     *
     * @return Optional containing the request context if present, empty otherwise
     */
    public static Optional<RequestContext> get() {
        return Optional.ofNullable(CONTEXT.get());
    }

    /**
     * Clears the request context for the current thread.
     *
     * <p>This method should be called at the end of request processing
     * to prevent memory leaks.</p>
     */
    public static void clear() {
        CONTEXT.remove();
    }
}
