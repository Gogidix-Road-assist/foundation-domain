package com.gogidix.rapidassist.shared.observability.library.logging;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * Logging correlation utility for request tracing across services.
 * Uses MDC (Mapped Diagnostic Context) to add correlation IDs to log messages.
 */
@Component
public class LoggingCorrelation {

    private static final String CORRELATION_ID_KEY = "correlationId";
    private static final String TRACE_ID_KEY = "traceId";
    private static final String SPAN_ID_KEY = "spanId";
    private static final String USER_ID_KEY = "userId";
    private static final String TENANT_ID_KEY = "tenantId";
    private static final String REQUEST_ID_KEY = "requestId";

    /**
     * Generate and set a new correlation ID
     * @return generated correlation ID
     */
    public String setCorrelationId() {
        String correlationId = UUID.randomUUID().toString();
        setCorrelationId(correlationId);
        return correlationId;
    }

    /**
     * Set correlation ID in MDC
     * @param correlationId correlation ID
     */
    public void setCorrelationId(String correlationId) {
        MDC.put(CORRELATION_ID_KEY, correlationId);
    }

    /**
     * Get current correlation ID
     * @return correlation ID or null
     */
    public String getCorrelationId() {
        return MDC.get(CORRELATION_ID_KEY);
    }

    /**
     * Set trace ID in MDC
     * @param traceId trace ID
     */
    public void setTraceId(String traceId) {
        MDC.put(TRACE_ID_KEY, traceId);
    }

    /**
     * Get current trace ID
     * @return trace ID or null
     */
    public String getTraceId() {
        return MDC.get(TRACE_ID_KEY);
    }

    /**
     * Set span ID in MDC
     * @param spanId span ID
     */
    public void setSpanId(String spanId) {
        MDC.put(SPAN_ID_KEY, spanId);
    }

    /**
     * Set user ID in MDC
     * @param userId user ID
     */
    public void setUserId(String userId) {
        MDC.put(USER_ID_KEY, userId);
    }

    /**
     * Set tenant ID in MDC
     * @param tenantId tenant ID
     */
    public void setTenantId(String tenantId) {
        MDC.put(TENANT_ID_KEY, tenantId);
    }

    /**
     * Set request ID in MDC
     * @param requestId request ID
     */
    public void setRequestId(String requestId) {
        MDC.put(REQUEST_ID_KEY, requestId);
    }

    /**
     * Set multiple context values at once
     * @param context map of context values
     */
    public void setContext(Map<String, String> context) {
        if (context != null) {
            context.forEach(MDC::put);
        }
    }

    /**
     * Clear all MDC context
     */
    public void clearContext() {
        MDC.clear();
    }

    /**
     * Clear specific keys from MDC
     * @param keys keys to remove
     */
    public void clearKeys(String... keys) {
        for (String key : keys) {
            MDC.remove(key);
        }
    }

    /**
     * Execute runnable with correlation context
     * @param correlationId correlation ID
     * @param runnable action to execute
     */
    public void runWithCorrelation(String correlationId, Runnable runnable) {
        String oldCorrelationId = getCorrelationId();
        try {
            setCorrelationId(correlationId);
            runnable.run();
        } finally {
            if (oldCorrelationId != null) {
                setCorrelationId(oldCorrelationId);
            } else {
                MDC.remove(CORRELATION_ID_KEY);
            }
        }
    }

    /**
     * Execute callable with correlation context
     * @param correlationId correlation ID
     * @param callable action to execute
     * @param <T> return type
     * @return result of callable
     * @throws Exception if callable throws
     */
    public <T> T callWithCorrelation(String correlationId, Callable<T> callable) throws Exception {
        String oldCorrelationId = getCorrelationId();
        try {
            setCorrelationId(correlationId);
            return callable.call();
        } finally {
            if (oldCorrelationId != null) {
                setCorrelationId(oldCorrelationId);
            } else {
                MDC.remove(CORRELATION_ID_KEY);
            }
        }
    }

    /**
     * Execute supplier with correlation context
     * @param correlationId correlation ID
     * @param supplier action to execute
     * @param <T> return type
     * @return result of supplier
     */
    public <T> T supplyWithCorrelation(String correlationId, Supplier<T> supplier) {
        String oldCorrelationId = getCorrelationId();
        try {
            setCorrelationId(correlationId);
            return supplier.get();
        } catch (Exception e) {
            throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
        } finally {
            if (oldCorrelationId != null) {
                setCorrelationId(oldCorrelationId);
            } else {
                MDC.remove(CORRELATION_ID_KEY);
            }
        }
    }

    /**
     * Execute runnable with full context
     * @param context map of context values
     * @param runnable action to execute
     */
    public void runWithContext(Map<String, String> context, Runnable runnable) {
        Map<String, String> oldContext = getCurrentContext();
        try {
            setContext(context);
            runnable.run();
        } finally {
            clearContext();
            if (oldContext != null && !oldContext.isEmpty()) {
                setContext(oldContext);
            }
        }
    }

    /**
     * Get current MDC context as a map
     * @return map of current MDC values
     */
    public Map<String, String> getCurrentContext() {
        return MDC.getCopyOfContextMap();
    }

    /**
     * Create a correlation context builder
     * @return CorrelationContextBuilder
     */
    public static CorrelationContextBuilder builder() {
        return new CorrelationContextBuilder();
    }

    /**
     * Builder for correlation context
     */
    public static class CorrelationContextBuilder {
        private final java.util.Map<String, String> context = new java.util.HashMap<>();

        public CorrelationContextBuilder correlationId(String correlationId) {
            context.put(CORRELATION_ID_KEY, correlationId);
            return this;
        }

        public CorrelationContextBuilder traceId(String traceId) {
            context.put(TRACE_ID_KEY, traceId);
            return this;
        }

        public CorrelationContextBuilder spanId(String spanId) {
            context.put(SPAN_ID_KEY, spanId);
            return this;
        }

        public CorrelationContextBuilder userId(String userId) {
            context.put(USER_ID_KEY, userId);
            return this;
        }

        public CorrelationContextBuilder tenantId(String tenantId) {
            context.put(TENANT_ID_KEY, tenantId);
            return this;
        }

        public CorrelationContextBuilder requestId(String requestId) {
            context.put(REQUEST_ID_KEY, requestId);
            return this;
        }

        public CorrelationContextBuilder custom(String key, String value) {
            context.put(key, value);
            return this;
        }

        public Map<String, String> build() {
            return new java.util.HashMap<>(context);
        }
    }
}
