package com.gogidix.rapidassist.shared.observability.library.tracing;

import io.micrometer.observation.ObservationRegistry;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.TraceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * Distributed tracing service using Micrometer Tracing.
 * Provides span creation and trace context propagation.
 */
@Component
public class TracingService {

    private static final Logger log = LoggerFactory.getLogger(TracingService.class);
    private static final String TRACE_ID_KEY = "traceId";
    private static final String SPAN_ID_KEY = "spanId";

    private final Tracer tracer;
    private final ObservationRegistry observationRegistry;

    public TracingService(Tracer tracer, ObservationRegistry observationRegistry) {
        this.tracer = tracer;
        this.observationRegistry = observationRegistry;
    }

    /**
     * Start a new span
     * @param name span name
     * @return Span.Builder
     */
    public Span.Builder startSpan(String name) {
        return tracer.spanBuilder().name(name);
    }

    /**
     * Create and run a span with a runnable
     * @param spanName span name
     * @param runnable action to execute
     */
    public void runInSpan(String spanName, Runnable runnable) {
        Span span = tracer.spanBuilder().name(spanName).start();
        try {
            updateMDC(span);
            runnable.run();
        } finally {
            span.end();
            clearMDC();
        }
    }

    /**
     * Create and run a span with a callable that returns value
     * @param spanName span name
     * @param callable action to execute
     * @param <T> return type
     * @return result of callable
     * @throws Exception if callable throws
     */
    public <T> T runInSpan(String spanName, Callable<T> callable) throws Exception {
        Span span = tracer.spanBuilder().name(spanName).start();
        try {
            updateMDC(span);
            return callable.call();
        } finally {
            span.end();
            clearMDC();
        }
    }

    /**
     * Create and run a span with a supplier
     * @param spanName span name
     * @param supplier action to execute
     * @param <T> return type
     * @return result of supplier
     */
    public <T> T supplyInSpan(String spanName, Supplier<T> supplier) {
        Span span = tracer.spanBuilder().name(spanName).start();
        try {
            updateMDC(span);
            return supplier.get();
        } catch (Exception e) {
            log.error("Error in span: {}", spanName, e);
            throw e;
        } finally {
            span.end();
            clearMDC();
        }
    }

    /**
     * Create and run a span with tags
     * @param spanName span name
     * @param tags span tags
     * @param runnable action to execute
     */
    public void runInSpanWithTags(String spanName, Map<String, String> tags, Runnable runnable) {
        Span.Builder builder = tracer.spanBuilder().name(spanName);
        if (tags != null) {
            tags.forEach(builder::tag);
        }
        Span span = builder.start();
        try {
            updateMDC(span);
            runnable.run();
        } finally {
            span.end();
            clearMDC();
        }
    }

    /**
     * Add tags to current span
     * @param key tag key
     * @param value tag value
     */
    public void addTag(String key, String value) {
        Span span = tracer.currentSpan();
        if (span != null) {
            span.tag(key, value);
        }
    }

    /**
     * Add multiple tags to current span
     * @param tags map of tags
     */
    public void addTags(Map<String, String> tags) {
        Span span = tracer.currentSpan();
        if (span != null && tags != null) {
            tags.forEach(span::tag);
        }
    }

    /**
     * Record an event in the current span
     * @param event event name
     */
    public void recordEvent(String event) {
        Span span = tracer.currentSpan();
        if (span != null) {
            span.event(event);
        }
    }

    /**
     * Record an event with attributes
     * @param event event name
     * @param attributes event attributes
     */
    public void recordEvent(String event, Map<String, String> attributes) {
        Span span = tracer.currentSpan();
        if (span != null) {
            span.event(event);
            if (attributes != null) {
                attributes.forEach(span::tag);
            }
        }
    }

    /**
     * Get current trace ID
     * @return trace ID or null
     */
    public String getCurrentTraceId() {
        Span span = tracer.currentSpan();
        if (span != null) {
            TraceContext context = span.context();
            if (context != null) {
                return context.traceId();
            }
        }
        return null;
    }

    /**
     * Get current span ID
     * @return span ID or null
     */
    public String getCurrentSpanId() {
        Span span = tracer.currentSpan();
        if (span != null) {
            TraceContext context = span.context();
            if (context != null) {
                return context.spanId();
            }
        }
        return null;
    }

    /**
     * Get current span
     * @return current Span or null
     */
    public Span getCurrentSpan() {
        return tracer.currentSpan();
    }

    /**
     * Update MDC with trace information
     * @param span span to extract trace info from
     */
    private void updateMDC(Span span) {
        TraceContext context = span.context();
        if (context != null) {
            MDC.put(TRACE_ID_KEY, context.traceId());
            MDC.put(SPAN_ID_KEY, context.spanId());
        }
    }

    /**
     * Clear MDC trace information
     */
    private void clearMDC() {
        MDC.remove(TRACE_ID_KEY);
        MDC.remove(SPAN_ID_KEY);
    }

    /**
     * Run an observation
     * @param name observation name
     * @param action action to execute
     */
    public void observe(String name, Runnable action) {
        runInSpan(name, action);
    }

    /**
     * Observe a supplier
     * @param name observation name
     * @param supplier supplier to observe
     * @param <T> return type
     * @return result of supplier
     */
    public <T> T observe(String name, Supplier<T> supplier) {
        return supplyInSpan(name, supplier);
    }

    /**
     * Get the Tracer
     * @return Tracer
     */
    public Tracer getTracer() {
        return tracer;
    }

    /**
     * Get the ObservationRegistry
     * @return ObservationRegistry
     */
    public ObservationRegistry getObservationRegistry() {
        return observationRegistry;
    }
}
