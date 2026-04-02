package com.gogidix.rapidassist.shared.ai.contracts.application;

import com.gogidix.rapidassist.shared.ai.contracts.domain.model.AIExecutionMetadata;

import java.util.function.Supplier;

/**
 * Port interface for AI service observability.
 * Implementations should integrate with the platform's observability infrastructure
 * including metrics, tracing, and logging.
 *
 * This port provides hooks for:
 * - Recording AI execution metrics
 * - Tracing AI operations
 * - Logging AI events
 * - Publishing AI-specific telemetry
 */
public interface AIObservabilityPort {

    /**
     * Records the start of an AI operation.
     *
     * @param tenantId   The tenant ID
     * @param serviceType The AI service type
     * @param requestId  The request ID
     * @return A handle that can be used to mark completion
     */
    OperationHandle recordOperationStart(String tenantId, AIExecutionMetadata.ServiceType serviceType, String requestId);

    /**
     * Records the completion of an AI operation.
     *
     * @param handle      The operation handle
     * @param metadata    The execution metadata
     * @param exception   The exception if operation failed, null otherwise
     */
    void recordOperationComplete(OperationHandle handle, AIExecutionMetadata metadata, Throwable exception);

    /**
     * Records a custom metric for an AI operation.
     *
     * @param tenantId   The tenant ID
     * @param serviceType The AI service type
     * @param metricName The metric name
     * @param value      The metric value
     */
    void recordMetric(String tenantId, AIExecutionMetadata.ServiceType serviceType, String metricName, double value);

    /**
     * Executes a lambda with observability recording.
     *
     * @param tenantId    The tenant ID
     * @param serviceType The AI service type
     * @param requestId   The request ID
     * @param operation   The operation to execute
     * @param <T>         The return type
     * @return The result of the operation
     * @throws Exception if the operation fails
     */
    default <T> T observe(String tenantId, AIExecutionMetadata.ServiceType serviceType,
                         String requestId, Supplier<T> operation) throws Exception {
        OperationHandle handle = recordOperationStart(tenantId, serviceType, requestId);
        try {
            T result = operation.get();
            recordOperationComplete(handle, null, null);
            return result;
        } catch (Exception e) {
            recordOperationComplete(handle, null, e);
            throw e;
        }
    }

    /**
     * Records an AI event for logging/auditing.
     *
     * @param tenantId   The tenant ID
     * @param serviceType The AI service type
     * @param eventType  The event type
     * @param eventData  The event data
     */
    void recordEvent(String tenantId, AIExecutionMetadata.ServiceType serviceType,
                    String eventType, Object eventData);

    /**
     * Increment a counter metric.
     *
     * @param tenantId   The tenant ID
     * @param serviceType The AI service type
     * @param counterName The counter name
     * @param tags       Additional tags for the counter
     */
    void incrementCounter(String tenantId, AIExecutionMetadata.ServiceType serviceType,
                         String counterName, String... tags);

    /**
     * Record a gauge value.
     *
     * @param tenantId   The tenant ID
     * @param serviceType The AI service type
     * @param gaugeName  The gauge name
     * @param value      The gauge value
     * @param tags       Additional tags for the gauge
     */
    void recordGauge(String tenantId, AIExecutionMetadata.ServiceType serviceType,
                    String gaugeName, double value, String... tags);

    /**
     * Record a timing value.
     *
     * @param tenantId   The tenant ID
     * @param serviceType The AI service type
     * @param timerName  The timer name
     * @param durationMs The duration in milliseconds
     * @param tags       Additional tags for the timer
     */
    void recordTiming(String tenantId, AIExecutionMetadata.ServiceType serviceType,
                     String timerName, long durationMs, String... tags);

    /**
     * Handle for an ongoing operation.
     */
    interface OperationHandle {
        /**
         * Gets the request ID for this operation.
         */
        String getRequestId();

        /**
         * Gets the start time for this operation.
         */
        long getStartTimeMillis();
    }

    /**
     * Default no-op implementation for testing.
     */
    AIObservabilityPort NO_OP = new AIObservabilityPort() {
        @Override
        public OperationHandle recordOperationStart(String tenantId, AIExecutionMetadata.ServiceType serviceType, String requestId) {
            long startTime = System.currentTimeMillis();
            return new OperationHandle() {
                @Override
                public String getRequestId() {
                    return requestId;
                }

                @Override
                public long getStartTimeMillis() {
                    return startTime;
                }
            };
        }

        @Override
        public void recordOperationComplete(OperationHandle handle, AIExecutionMetadata metadata, Throwable exception) {
            // No-op
        }

        @Override
        public void recordMetric(String tenantId, AIExecutionMetadata.ServiceType serviceType,
                                String metricName, double value) {
            // No-op
        }

        @Override
        public void recordEvent(String tenantId, AIExecutionMetadata.ServiceType serviceType,
                              String eventType, Object eventData) {
            // No-op
        }

        @Override
        public void incrementCounter(String tenantId, AIExecutionMetadata.ServiceType serviceType,
                                    String counterName, String... tags) {
            // No-op
        }

        @Override
        public void recordGauge(String tenantId, AIExecutionMetadata.ServiceType serviceType,
                               String gaugeName, double value, String... tags) {
            // No-op
        }

        @Override
        public void recordTiming(String tenantId, AIExecutionMetadata.ServiceType serviceType,
                                String timerName, long durationMs, String... tags) {
            // No-op
        }
    };
}
