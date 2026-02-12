package com.gogidix.rapidassist.shared.observability.library.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.ToDoubleFunction;

/**
 * Centralized metrics collection service using Micrometer.
 * Provides type-safe metrics recording for counters, gauges, timers, and distribution summaries.
 */
@Component
public class MetricsService {

    private final MeterRegistry meterRegistry;
    private final ConcurrentHashMap<String, Counter> counters = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Timer> timers = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, DistributionSummary> summaries = new ConcurrentHashMap<>();

    public MetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    /**
     * Record a counter increment
     * @param name metric name
     * @param tags metric tags
     */
    public void incrementCounter(String name, String... tags) {
        Counter counter = counters.computeIfAbsent(name, n ->
            Counter.builder(n)
                .tags(tags)
                .register(meterRegistry)
        );
        counter.increment();
    }

    /**
     * Record a counter increment by specific amount
     * @param name metric name
     * @param amount amount to increment
     * @param tags metric tags
     */
    public void incrementCounter(String name, Double amount, String... tags) {
        Counter counter = counters.computeIfAbsent(name, n ->
            Counter.builder(n)
                .tags(tags)
                .register(meterRegistry)
        );
        counter.increment(amount);
    }

    /**
     * Create or get a counter
     * @param name metric name
     * @param tags metric tags
     * @return Counter instance
     */
    public Counter getCounter(String name, String... tags) {
        return counters.computeIfAbsent(name, n ->
            Counter.builder(n)
                .tags(tags)
                .register(meterRegistry)
        );
    }

    /**
     * Start a timer sample
     * @return Timer.Sample for stopping
     */
    public Timer.Sample startTimer() {
        return Timer.start(meterRegistry);
    }

    /**
     * Stop timer sample and record duration
     * @param sample timer sample
     * @param name metric name
     * @param tags metric tags
     */
    public void stopTimer(Timer.Sample sample, String name, String... tags) {
        Timer timer = timers.computeIfAbsent(name, n ->
            Timer.builder(name)
                .tags(tags)
                .register(meterRegistry)
        );
        sample.stop(timer);
    }

    /**
     * Record an operation duration
     * @param name metric name
     * @param durationMs duration in milliseconds
     * @param tags metric tags
     */
    public void recordTimer(String name, Long durationMs, String... tags) {
        Timer timer = timers.computeIfAbsent(name, n ->
            Timer.builder(n)
                .tags(tags)
                .register(meterRegistry)
        );
        timer.record(durationMs, TimeUnit.MILLISECONDS);
    }

    /**
     * Time a runnable operation
     * @param name metric name
     * @param runnable action to time
     * @param tags metric tags
     */
    public void recordRunnable(String name, Runnable runnable, String... tags) {
        Timer timer = timers.computeIfAbsent(name, n ->
            Timer.builder(n)
                .tags(tags)
                .register(meterRegistry)
        );
        timer.record(runnable);
    }

    /**
     * Record a value in a distribution summary
     * @param name metric name
     * @param value value to record
     * @param tags metric tags
     */
    public void recordDistributionSummary(String name, Double value, String... tags) {
        DistributionSummary summary = summaries.computeIfAbsent(name, n ->
            DistributionSummary.builder(n)
                .tags(tags)
                .register(meterRegistry)
        );
        summary.record(value);
    }

    /**
     * Record a value in a distribution summary
     * @param name metric name
     * @param value value to record
     * @param tags metric tags
     */
    public void recordDistributionSummary(String name, Long value, String... tags) {
        DistributionSummary summary = summaries.computeIfAbsent(name, n ->
            DistributionSummary.builder(n)
                .tags(tags)
                .register(meterRegistry)
        );
        summary.record(value);
    }

    /**
     * Register a gauge
     * @param name metric name
     * @param obj object to measure
     * @param valueFunction function to extract value
     * @param tags metric tags
     * @param <T> type of object
     */
    public <T> void registerGauge(String name, T obj, ToDoubleFunction<T> valueFunction, String... tags) {
        Gauge.builder(name, obj, valueFunction)
            .tags(tags)
            .register(meterRegistry);
    }

    /**
     * Get the underlying MeterRegistry
     * @return MeterRegistry
     */
    public MeterRegistry getMeterRegistry() {
        return meterRegistry;
    }

    /**
     * Common metric names constants
     */
    public static class MetricsNames {
        // HTTP metrics
        public static final String HTTP_REQUESTS = "http.requests";
        public static final String HTTP_RESPONSES = "http.responses";
        public static final String HTTP_ERRORS = "http.errors";
        public static final String HTTP_LATENCY = "http.latency";

        // Service metrics
        public static final String SERVICE_INVOCATIONS = "service.invocations";
        public static final String SERVICE_ERRORS = "service.errors";
        public static final String SERVICE_LATENCY = "service.latency";

        // Database metrics
        public static final String DB_QUERIES = "db.queries";
        public static final String DB_ERRORS = "db.errors";
        public static final String DB_LATENCY = "db.latency";

        // Business metrics
        public static final String SERVICE_REQUESTS_CREATED = "service.requests.created";
        public static final String SERVICE_REQUESTS_COMPLETED = "service.requests.completed";
        public static final String PAYMENTS_PROCESSED = "payments.processed";
        public static final String PAYMENTS_FAILED = "payments.failed";
    }

    /**
     * Common tag names constants
     */
    public static class TagNames {
        public static final String SERVICE = "service";
        public static final String OPERATION = "operation";
        public static final String STATUS = "status";
        public static final String ERROR = "error";
        public static final String TENANT_ID = "tenant_id";
        public static final String ORGANIZATION_ID = "organization_id";
        public static final String USER_ID = "user_id";
        public static final String METHOD = "method";
        public static final String URI = "uri";
        public static final String EXCEPTION = "exception";
    }
}
