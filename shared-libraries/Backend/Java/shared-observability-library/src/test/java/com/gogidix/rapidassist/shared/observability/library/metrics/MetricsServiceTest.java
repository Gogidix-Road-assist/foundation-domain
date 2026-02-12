package com.gogidix.rapidassist.shared.observability.library.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for MetricsService class.
 * Tests metrics recording for counters, gauges, timers, and distribution summaries.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Metrics Service Tests")
class MetricsServiceTest {

    @Mock
    private MeterRegistry meterRegistry;

    private MetricsService metricsService;

    @BeforeEach
    void setUp() {
        metricsService = new MetricsService(meterRegistry);
    }

    @Test
    @DisplayName("Should increment counter by 1")
    void testIncrementCounter() {
        Counter mockCounter = mock(Counter.class);
        when(meterRegistry.counter(any(String.class), any(String[].class))).thenReturn(mockCounter);

        metricsService.incrementCounter("test.counter", "tag1", "value1");

        verify(mockCounter).increment();
    }

    @Test
    @DisplayName("Should increment counter by custom amount")
    void testIncrementCounterByAmount() {
        Counter mockCounter = mock(Counter.class);
        when(meterRegistry.counter(any(String.class), any(String[].class))).thenReturn(mockCounter);

        metricsService.incrementCounter("test.counter", 5.0, "tag1", "value1");

        verify(mockCounter).increment(5.0);
    }

    @Test
    @DisplayName("Should get or create counter")
    void testGetCounter() {
        Counter mockCounter = mock(Counter.class);
        when(meterRegistry.counter(any(String.class), any(String[].class))).thenReturn(mockCounter);

        Counter counter = metricsService.getCounter("test.counter", "tag1", "value1");

        assertNotNull(counter);
        verify(meterRegistry).counter("test.counter", "tag1", "value1");
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
        Timer mockTimer = mock(Timer.class);
        when(meterRegistry.timer(any(String.class), any(String[].class))).thenReturn(mockTimer);

        metricsService.stopTimer(sample, "test.timer", "tag1", "value1");

        verify(mockTimer).record(any(java.util.function.DoubleSupplier.class));
    }

    @Test
    @DisplayName("Should record timer duration")
    void testRecordTimer() {
        Timer mockTimer = mock(Timer.class);
        when(meterRegistry.timer(any(String.class), any(String[].class))).thenReturn(mockTimer);

        metricsService.recordTimer("test.timer", 100L, "tag1", "value1");

        verify(mockTimer).record(100L, java.util.concurrent.TimeUnit.MILLISECONDS);
    }

    @Test
    @DisplayName("Should record runnable duration")
    void testRecordRunnable() {
        Timer mockTimer = mock(Timer.class);
        when(meterRegistry.timer(any(String.class), any(String[].class))).thenReturn(mockTimer);

        Runnable runnable = () -> {};
        metricsService.recordRunnable("test.timer", runnable, "tag1", "value1");

        verify(mockTimer).record(runnable);
    }

    @Test
    @DisplayName("Should record distribution summary with double value")
    void testRecordDistributionSummaryDouble() {
        io.micrometer.core.instrument.DistributionSummary mockSummary =
            mock(io.micrometer.core.instrument.DistributionSummary.class);
        when(meterRegistry.summary(any(String.class), any(String[].class))).thenReturn(mockSummary);

        metricsService.recordDistributionSummary("test.summary", 100.5, "tag1", "value1");

        verify(mockSummary).record(100.5);
    }

    @Test
    @DisplayName("Should record distribution summary with long value")
    void testRecordDistributionSummaryLong() {
        io.micrometer.core.instrument.DistributionSummary mockSummary =
            mock(io.micrometer.core.instrument.DistributionSummary.class);
        when(meterRegistry.summary(any(String.class), any(String[].class))).thenReturn(mockSummary);

        metricsService.recordDistributionSummary("test.summary", 100L, "tag1", "value1");

        verify(mockSummary).record(100.0);
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
