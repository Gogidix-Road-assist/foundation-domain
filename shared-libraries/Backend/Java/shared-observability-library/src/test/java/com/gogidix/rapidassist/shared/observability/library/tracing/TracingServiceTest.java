package com.gogidix.rapidassist.shared.observability.library.tracing;

import io.micrometer.observation.ObservationRegistry;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.TraceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TracingService class.
 * Tests distributed tracing operations with Micrometer Tracing.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("Tracing Service Tests")
class TracingServiceTest {

    @Mock
    private Tracer tracer;

    @Mock
    private ObservationRegistry observationRegistry;

    @Mock
    private Span.Builder spanBuilder;

    @Mock
    private Span span;

    @Mock
    private TraceContext traceContext;

    private TracingService tracingService;

    @BeforeEach
    void setUp() {
        // Setup Tracer mock to return proper Span.Builder chain
        when(tracer.spanBuilder()).thenReturn(spanBuilder);
        when(spanBuilder.name(anyString())).thenReturn(spanBuilder);
        when(spanBuilder.start()).thenReturn(span);

        // Setup Span context for getCurrentTraceId/getCurrentSpanId
        when(span.context()).thenReturn(traceContext);
        when(traceContext.traceId()).thenReturn("test-trace-id");
        when(traceContext.spanId()).thenReturn("test-span-id");

        tracingService = new TracingService(tracer, observationRegistry);
    }

    @Test
    @DisplayName("Should create tracing service")
    void testCreateTracingService() {
        assertNotNull(tracingService);
    }

    @Test
    @DisplayName("Should get tracer")
    void testGetTracer() {
        assertEquals(tracer, tracingService.getTracer());
    }

    @Test
    @DisplayName("Should get observation registry")
    void testGetObservationRegistry() {
        assertEquals(observationRegistry, tracingService.getObservationRegistry());
    }

    @Test
    @DisplayName("Should start span and return Span.Builder")
    void testStartSpan() {
        Span.Builder result = tracingService.startSpan("test-span");
        assertNotNull(result);
        verify(tracer).spanBuilder();
        verify(spanBuilder).name("test-span");
    }

    @Test
    @DisplayName("Should run in span with runnable")
    void testRunInSpan() {
        assertDoesNotThrow(() -> {
            tracingService.runInSpan("test-span", () -> {
                // Test runnable execution
            });
        });
        verify(spanBuilder).name("test-span");
        verify(spanBuilder).start();
        verify(span).end();
    }

    @Test
    @DisplayName("Should supply in span")
    void testSupplyInSpan() {
        String result = tracingService.supplyInSpan("test-span", () -> "test-result");
        assertEquals("test-result", result);
        verify(spanBuilder).name("test-span");
        verify(spanBuilder).start();
        verify(span).end();
    }

    @Test
    @DisplayName("Should run in span with tags")
    void testRunInSpanWithTags() {
        assertDoesNotThrow(() -> {
            tracingService.runInSpanWithTags("test-span", Map.of("key", "value"), () -> {
                // Test runnable execution
            });
        });
        verify(spanBuilder).name("test-span");
        verify(spanBuilder).tag("key", "value");
        verify(spanBuilder).start();
        verify(span).end();
    }

    @Test
    @DisplayName("Should add tag to current span")
    void testAddTag() {
        when(tracer.currentSpan()).thenReturn(span);
        tracingService.addTag("test-key", "test-value");
        verify(span).tag("test-key", "test-value");
    }

    @Test
    @DisplayName("Should add tags to current span")
    void testAddTags() {
        when(tracer.currentSpan()).thenReturn(span);
        tracingService.addTags(Map.of("key1", "value1", "key2", "value2"));
        verify(span, times(2)).tag(anyString(), anyString());
    }

    @Test
    @DisplayName("Should get current trace ID")
    void testGetCurrentTraceId() {
        when(tracer.currentSpan()).thenReturn(span);
        String traceId = tracingService.getCurrentTraceId();
        assertEquals("test-trace-id", traceId);
    }

    @Test
    @DisplayName("Should get current span ID")
    void testGetCurrentSpanId() {
        when(tracer.currentSpan()).thenReturn(span);
        String spanId = tracingService.getCurrentSpanId();
        assertEquals("test-span-id", spanId);
    }

    @Test
    @DisplayName("Should get current span")
    void testGetCurrentSpan() {
        when(tracer.currentSpan()).thenReturn(span);
        Span currentSpan = tracingService.getCurrentSpan();
        assertEquals(span, currentSpan);
    }

    @Test
    @DisplayName("Should observe runnable")
    void testObserveRunnable() {
        assertDoesNotThrow(() -> {
            tracingService.observe("test-observation", () -> {
                // Test runnable execution
            });
        });
        verify(spanBuilder).name("test-observation");
        verify(spanBuilder).start();
        verify(span).end();
    }

    @Test
    @DisplayName("Should observe supplier")
    void testObserveSupplier() {
        String result = tracingService.observe("test-observation", () -> "test-result");
        assertEquals("test-result", result);
        verify(spanBuilder).name("test-observation");
        verify(spanBuilder).start();
        verify(span).end();
    }
}
