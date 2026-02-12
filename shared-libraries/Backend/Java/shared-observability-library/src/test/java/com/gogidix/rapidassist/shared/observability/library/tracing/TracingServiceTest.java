package com.gogidix.rapidassist.shared.observability.library.tracing;

import io.micrometer.observation.ObservationRegistry;
import io.micrometer.tracing.Tracer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TracingService class.
 * Tests distributed tracing operations with Micrometer Tracing.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tracing Service Tests")
class TracingServiceTest {

    @Mock
    private Tracer tracer;

    @Mock
    private ObservationRegistry observationRegistry;

    private TracingService tracingService;

    @BeforeEach
    void setUp() {
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
    @DisplayName("Should start span")
    void testStartSpan() {
        assertNotNull(tracingService.startSpan("test-span"));
    }

    @Test
    @DisplayName("Should run in span with runnable")
    void testRunInSpan() {
        assertDoesNotThrow(() -> {
            tracingService.runInSpan("test-span", () -> {
                // Test runnable execution
            });
        });
    }

    @Test
    @DisplayName("Should supply in span")
    void testSupplyInSpan() {
        String result = tracingService.supplyInSpan("test-span", () -> "test-result");
        assertEquals("test-result", result);
    }

    @Test
    @DisplayName("Should run in span with tags")
    void testRunInSpanWithTags() {
        assertDoesNotThrow(() -> {
            tracingService.runInSpanWithTags("test-span", Map.of("key", "value"), () -> {
                // Test runnable execution
            });
        });
    }

    @Test
    @DisplayName("Should observe runnable")
    void testObserveRunnable() {
        assertDoesNotThrow(() -> {
            tracingService.observe("test-observation", () -> {
                // Test runnable execution
            });
        });
    }

    @Test
    @DisplayName("Should observe supplier")
    void testObserveSupplier() {
        String result = tracingService.observe("test-observation", () -> "test-result");
        assertEquals("test-result", result);
    }
}
