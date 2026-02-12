package com.gogidix.rapidassist.shared.ai.contracts.application;

import com.gogidix.rapidassist.shared.ai.contracts.domain.model.AIExecutionMetadata;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("AIObservabilityPort Tests")
class AIObservabilityPortTest {

    @Test
    @DisplayName("NO_OP implementation should not throw exceptions")
    void noOpShouldNotThrow() {
        AIObservabilityPort noOp = AIObservabilityPort.NO_OP;

        AIObservabilityPort.OperationHandle handle = noOp.recordOperationStart(
                "tenant-123",
                AIExecutionMetadata.ServiceType.CONTENT_MODERATION,
                "req-456"
        );

        assertThat(handle).isNotNull();
        assertThat(handle.getRequestId()).isEqualTo("req-456");
        assertThat(handle.getStartTimeMillis()).isGreaterThan(0);

        // Should not throw
        noOp.recordOperationComplete(handle, null, null);
        noOp.recordMetric("tenant-123", AIExecutionMetadata.ServiceType.CONTENT_MODERATION, "metric", 1.0);
        noOp.recordEvent("tenant-123", AIExecutionMetadata.ServiceType.CONTENT_MODERATION, "event", null);
        noOp.incrementCounter("tenant-123", AIExecutionMetadata.ServiceType.CONTENT_MODERATION, "counter");
        noOp.recordGauge("tenant-123", AIExecutionMetadata.ServiceType.CONTENT_MODERATION, "gauge", 10.0);
        noOp.recordTiming("tenant-123", AIExecutionMetadata.ServiceType.CONTENT_MODERATION, "timer", 100);
    }

    @Test
    @DisplayName("NO_OP observe method should execute operation")
    void noOpObserveShouldExecuteOperation() throws Exception {
        AIObservabilityPort noOp = AIObservabilityPort.NO_OP;

        String result = noOp.observe(
                "tenant-123",
                AIExecutionMetadata.ServiceType.CONTENT_MODERATION,
                "req-456",
                () -> "success"
        );

        assertThat(result).isEqualTo("success");
    }

    @Test
    @DisplayName("NO_OP observe method should propagate exceptions")
    void noOpObserveShouldPropagateExceptions() {
        AIObservabilityPort noOp = AIObservabilityPort.NO_OP;

        assertThatThrownBy(() -> noOp.observe(
                "tenant-123",
                AIExecutionMetadata.ServiceType.CONTENT_MODERATION,
                "req-456",
                () -> {
                    throw new RuntimeException("Test exception");
                }
        )).isInstanceOf(RuntimeException.class)
                .hasMessage("Test exception");
    }

    @Test
    @DisplayName("Should create custom implementation")
    void shouldCreateCustomImplementation() {
        AtomicBoolean operationStarted = new AtomicBoolean(false);
        AtomicBoolean operationCompleted = new AtomicBoolean(false);
        AtomicReference<AIObservabilityPort.OperationHandle> capturedHandle = new AtomicReference<>();

        AIObservabilityPort customPort = new AIObservabilityPort() {
            @Override
            public OperationHandle recordOperationStart(String tenantId, AIExecutionMetadata.ServiceType serviceType, String requestId) {
                operationStarted.set(true);
                OperationHandle handle = new OperationHandle() {
                    private final long startTime = System.currentTimeMillis();
                    private final String reqId = requestId;

                    @Override
                    public String getRequestId() {
                        return reqId;
                    }

                    @Override
                    public long getStartTimeMillis() {
                        return startTime;
                    }
                };
                capturedHandle.set(handle);
                return handle;
            }

            @Override
            public void recordOperationComplete(OperationHandle handle, AIExecutionMetadata metadata, Throwable exception) {
                operationCompleted.set(true);
            }

            @Override
            public void recordMetric(String tenantId, AIExecutionMetadata.ServiceType serviceType, String metricName, double value) {
                // Custom metric recording
            }

            @Override
            public void recordEvent(String tenantId, AIExecutionMetadata.ServiceType serviceType, String eventType, Object eventData) {
                // Custom event recording
            }

            @Override
            public void incrementCounter(String tenantId, AIExecutionMetadata.ServiceType serviceType, String counterName, String... tags) {
                // Custom counter recording
            }

            @Override
            public void recordGauge(String tenantId, AIExecutionMetadata.ServiceType serviceType, String gaugeName, double value, String... tags) {
                // Custom gauge recording
            }

            @Override
            public void recordTiming(String tenantId, AIExecutionMetadata.ServiceType serviceType, String timerName, long durationMs, String... tags) {
                // Custom timing recording
            }
        };

        // Test the custom implementation
        AIObservabilityPort.OperationHandle handle = customPort.recordOperationStart(
                "tenant-123",
                AIExecutionMetadata.ServiceType.NLP_PROCESSING,
                "req-789"
        );

        assertThat(operationStarted).isTrue();
        assertThat(handle.getRequestId()).isEqualTo("req-789");

        customPort.recordOperationComplete(handle, null, null);
        assertThat(operationCompleted).isTrue();
    }

    @Test
    @DisplayName("Should support observe with custom implementation")
    void shouldSupportObserveWithCustomImplementation() throws Exception {
        AtomicBoolean completed = new AtomicBoolean(false);

        AIObservabilityPort customPort = new AIObservabilityPort() {
            @Override
            public OperationHandle recordOperationStart(String tenantId, AIExecutionMetadata.ServiceType serviceType, String requestId) {
                return new OperationHandle() {
                    @Override
                    public String getRequestId() {
                        return requestId;
                    }

                    @Override
                    public long getStartTimeMillis() {
                        return System.currentTimeMillis();
                    }
                };
            }

            @Override
            public void recordOperationComplete(OperationHandle handle, AIExecutionMetadata metadata, Throwable exception) {
                completed.set(true);
            }

            @Override
            public void recordMetric(String tenantId, AIExecutionMetadata.ServiceType serviceType, String metricName, double value) {
            }

            @Override
            public void recordEvent(String tenantId, AIExecutionMetadata.ServiceType serviceType, String eventType, Object eventData) {
            }

            @Override
            public void incrementCounter(String tenantId, AIExecutionMetadata.ServiceType serviceType, String counterName, String... tags) {
            }

            @Override
            public void recordGauge(String tenantId, AIExecutionMetadata.ServiceType serviceType, String gaugeName, double value, String... tags) {
            }

            @Override
            public void recordTiming(String tenantId, AIExecutionMetadata.ServiceType serviceType, String timerName, long durationMs, String... tags) {
            }
        };

        String result = customPort.observe(
                "tenant-123",
                AIExecutionMetadata.ServiceType.SENTIMENT_ANALYSIS,
                "req-999",
                () -> "test result"
        );

        assertThat(result).isEqualTo("test result");
        assertThat(completed).isTrue();
    }

    @Test
    @DisplayName("Should record operation completion even when exception occurs")
    void shouldRecordCompletionOnException() throws Exception {
        AtomicBoolean completedWithException = new AtomicBoolean(false);

        AIObservabilityPort customPort = new AIObservabilityPort() {
            @Override
            public OperationHandle recordOperationStart(String tenantId, AIExecutionMetadata.ServiceType serviceType, String requestId) {
                return new OperationHandle() {
                    @Override
                    public String getRequestId() {
                        return requestId;
                    }

                    @Override
                    public long getStartTimeMillis() {
                        return System.currentTimeMillis();
                    }
                };
            }

            @Override
            public void recordOperationComplete(OperationHandle handle, AIExecutionMetadata metadata, Throwable exception) {
                if (exception != null) {
                    completedWithException.set(true);
                }
            }

            @Override
            public void recordMetric(String tenantId, AIExecutionMetadata.ServiceType serviceType, String metricName, double value) {
            }

            @Override
            public void recordEvent(String tenantId, AIExecutionMetadata.ServiceType serviceType, String eventType, Object eventData) {
            }

            @Override
            public void incrementCounter(String tenantId, AIExecutionMetadata.ServiceType serviceType, String counterName, String... tags) {
            }

            @Override
            public void recordGauge(String tenantId, AIExecutionMetadata.ServiceType serviceType, String gaugeName, double value, String... tags) {
            }

            @Override
            public void recordTiming(String tenantId, AIExecutionMetadata.ServiceType serviceType, String timerName, long durationMs, String... tags) {
            }
        };

        assertThatThrownBy(() -> customPort.observe(
                "tenant-123",
                AIExecutionMetadata.ServiceType.CONTENT_MODERATION,
                "req-500",
                () -> {
                    throw new IllegalStateException("Expected exception");
                }
        )).isInstanceOf(IllegalStateException.class);

        assertThat(completedWithException).isTrue();
    }
}
