package com.gogidix.rapidassist.shared.idempotency.library.infrastructure.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Metrics collector for idempotency operations.
 * Uses Micrometer to track idempotency-related metrics.
 */
public class IdempotencyMetrics {

    private static final Logger log = LoggerFactory.getLogger(IdempotencyMetrics.class);

    private final Counter hitCounter;
    private final Counter missCounter;
    private final Counter expiredCounter;
    private final Counter inProgressCounter;
    private final Counter conflictCounter;
    private final Timer responseTimer;

    public IdempotencyMetrics(MeterRegistry meterRegistry) {
        this.hitCounter = Counter.builder("idempotency.hit")
                .description("Number of idempotency key hits (cached responses returned)")
                .tag("type", "idempotency")
                .register(meterRegistry);

        this.missCounter = Counter.builder("idempotency.miss")
                .description("Number of idempotency key misses (new requests processed)")
                .tag("type", "idempotency")
                .register(meterRegistry);

        this.expiredCounter = Counter.builder("idempotency.expired")
                .description("Number of expired idempotency keys encountered")
                .tag("type", "idempotency")
                .register(meterRegistry);

        this.inProgressCounter = Counter.builder("idempotency.in_progress")
                .description("Number of requests rejected due to in-progress idempotency key")
                .tag("type", "idempotency")
                .register(meterRegistry);

        this.conflictCounter = Counter.builder("idempotency.conflict")
                .description("Number of requests rejected due to request hash mismatch")
                .tag("type", "idempotency")
                .register(meterRegistry);

        this.responseTimer = Timer.builder("idempotency.response.duration")
                .description("Time taken to process idempotent requests")
                .tag("type", "idempotency")
                .register(meterRegistry);
    }

    /**
     * Record an idempotency hit - a cached response was returned.
     */
    public void recordHit() {
        hitCounter.increment();
        log.trace("Idempotency hit recorded");
    }

    /**
     * Record an idempotency miss - a new request was processed.
     */
    public void recordMiss() {
        missCounter.increment();
        log.trace("Idempotency miss recorded");
    }

    /**
     * Record an expired idempotency key encounter.
     */
    public void recordExpired() {
        expiredCounter.increment();
        log.trace("Idempotency expired key recorded");
    }

    /**
     * Record an in-progress rejection.
     */
    public void recordInProgress() {
        inProgressCounter.increment();
        log.trace("Idempotency in-progress rejection recorded");
    }

    /**
     * Record a conflict rejection due to request hash mismatch.
     */
    public void recordConflict() {
        conflictCounter.increment();
        log.trace("Idempotency conflict rejection recorded");
    }

    /**
     * Record the duration of processing an idempotent request.
     *
     * @param duration the duration of the operation
     */
    public void recordResponseTime(Duration duration) {
        responseTimer.record(duration);
        log.trace("Idempotency response time recorded: {}ms", duration.toMillis());
    }

    /**
     * Record the duration of processing an idempotent request in milliseconds.
     *
     * @param milliseconds the duration in milliseconds
     */
    public void recordResponseTime(long milliseconds) {
        responseTimer.record(milliseconds, TimeUnit.MILLISECONDS);
        log.trace("Idempotency response time recorded: {}ms", milliseconds);
    }

    /**
     * Create a timer sample for measuring operation duration.
     *
     * @return a timer sample
     */
    public Timer.Sample startTimer() {
        return Timer.start();
    }

    /**
     * Stop a timer sample and record the duration.
     *
     * @param sample the timer sample to stop
     */
    public void stopTimer(Timer.Sample sample) {
        sample.stop(responseTimer);
    }
}
