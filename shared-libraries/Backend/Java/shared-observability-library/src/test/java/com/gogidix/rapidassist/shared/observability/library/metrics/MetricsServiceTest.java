package com.gogidix.rapidassist.shared.observability.library.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MetricsService class.
 * Tests metrics recording for counters, gauges, timers, and distribution summaries.
 */
@DisplayName("Metrics Service Tests")
class MetricsServiceTest {

    private MeterRegistry meterRegistry;
    private MetricsService metricsService;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        metricsService = new MetricsService(meterRegistry);
    }

    @Test
    @DisplayName("Should increment counter by 1")
    void testIncrementCounter() {
        assertDoesNotThrow(() ->
            metricsService.incrementCounter("test.counter", "tag1", "value1")
        );
        Counter counter = meterRegistry.counter("test.counter", "tag1", "value1");
        assertEquals(1.0, counter.count());
    }

    @Test
    @DisplayName("Should increment counter by custom amount")
    void testIncrementCounterByAmount() {
        metricsService.incrementCounter("test.counter", 5.0, "tag1", "value1");
        Counter counter = meterRegistry.counter("test.counter", "tag1", "value1");
        assertEquals(5.0, counter.count());
    }

    @Test
    @DisplayName("Should get or create counter")
    void testGetCounter() {
        Counter counter = metricsService.getCounter("test.counter", "tag1", "value1");
        assertNotNull(counter);
    }

    @Test
    @DisplayName("Should start timer sample")
    void testStartTimer() {
        Timer.Sample sample = metricsService.startTimer();
        assertNotNull(sample);
    }

    @Test
    @DisplayName("Should record timer with sample")
    void testStopTimer() {
        Timer.Sample sample = Timer.start(meterRegistry);
        assertDoesNotThrow(() ->
            metricsService.stopTimer(sample, "test.timer", "tag1", "value1")
        );
    }

    @Test
    @DisplayName("Should record timer duration")
    void testRecordTimer() {
        assertDoesNotThrow(() ->
            metricsService.recordTimer("test.timer", 100L, "tag1", "value1")
        );
    }

    @Test
    @DisplayName("Should record runnable duration")
    void testRecordRunnable() {
        Runnable runnable = () -> {};
        assertDoesNotThrow(() ->
            metricsService.recordRunnable("test.timer", runnable, "tag1", "value1")
        );
    }

    @Test
    @DisplayName("Should record distribution summary with double value")
    void testRecordDistributionSummaryDouble() {
        assertDoesNotThrow(() ->
            metricsService.recordDistributionSummary("test.summary", 100.5, "tag1", "value1")
        );
    }

    @Test
    @DisplayName("Should record distribution summary with long value")
    void testRecordDistributionSummaryLong() {
        assertDoesNotThrow(() ->
            metricsService.recordDistributionSummary("test.summary", 100L, "tag1", "value1")
        );
    }

    @Test
    @DisplayName("Should register gauge")
    void testRegisterGauge() {
        class TestObject {
            public Double getValue() {
                return 42.0;
            }
        }

        TestObject obj = new TestObject();

        assertDoesNotThrow(() ->
            metricsService.registerGauge("test.gauge", obj, TestObject::getValue, "tag1", "value1")
        );
    }

    @Test
    @DisplayName("Should get meter registry")
    void testGetMeterRegistry() {
        MeterRegistry registry = metricsService.getMeterRegistry();
        assertEquals(meterRegistry, registry);
    }

    @Test
    @DisplayName("MetricsNames constants should be defined")
    void testMetricsNamesConstants() {
        assertNotNull(MetricsService.MetricsNames.HTTP_REQUESTS);
        assertNotNull(MetricsService.MetricsNames.SERVICE_INVOCATIONS);
        assertNotNull(MetricsService.MetricsNames.DB_QUERIES);
        assertNotNull(MetricsService.MetricsNames.SERVICE_REQUESTS_CREATED);
    }

    @Test
    @DisplayName("TagNames constants should be defined")
    void testTagNamesConstants() {
        assertNotNull(MetricsService.TagNames.SERVICE);
        assertNotNull(MetricsService.TagNames.OPERATION);
        assertNotNull(MetricsService.TagNames.STATUS);
        assertNotNull(MetricsService.TagNames.TENANT_ID);
    }
}
