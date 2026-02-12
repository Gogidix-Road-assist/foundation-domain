package com.gogidix.rapidassist.shared.ai.contracts.domain.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.TreeMap;

/**
 * Metadata about AI execution including timing, resources, and observability data.
 * Captures all execution-related information for monitoring and debugging.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AIExecutionMetadata {

    /**
     * The type of AI service that executed.
     */
    private ServiceType serviceType;

    /**
     * Version of the AI service/model.
     */
    private String serviceVersion;

    /**
     * When execution started.
     */
    private Instant startedAt;

    /**
     * When execution completed.
     */
    private Instant completedAt;

    /**
     * Total execution duration.
     */
    private Duration duration;

    /**
     * Processing time in milliseconds (actual AI inference time).
     */
    private Long processingTimeMs;

    /**
     * Memory usage in bytes, if available.
     */
    private Long memoryUsedBytes;

    /**
     * CPU usage percentage, if available.
     */
    private Double cpuUsagePercent;

    /**
     * Number of retry attempts, if any.
     */
    private Integer retryCount;

    /**
     * Trace ID for distributed tracing.
     */
    private String traceId;

    /**
     * Span ID for this specific operation.
     */
    private String spanId;

    /**
     * Model or algorithm version used.
     */
    private String modelVersion;

    /**
     * Confidence score of the AI result (0.0 to 1.0), if applicable.
     */
    private Double confidenceScore;

    /**
     * Additional metrics specific to the service.
     */
    private Map<String, Object> metrics;

    /**
     * Supported AI service types.
     */
    public enum ServiceType {
        CONTENT_MODERATION("content-moderation"),
        NLP_PROCESSING("nlp-processing"),
        IMAGE_RECOGNITION("image-recognition"),
        SPEECH_RECOGNITION("speech-recognition"),
        ANOMALY_DETECTION("anomaly-detection"),
        PREDICTIVE_ANALYTICS("predictive-analytics"),
        RECOMMENDATION("recommendation"),
        TRANSLATION("translation"),
        SUMMARIZATION("summarization"),
        SENTIMENT_ANALYSIS("sentiment-analysis"),
        SEARCH_OPTIMIZATION("search-optimization"),
        PRICING_ENGINE("pricing-engine"),
        MATCHING_ALGORITHM("matching-algorithm"),
        MODEL_MANAGEMENT("model-management"),
        FORECASTING("forecasting"),
        RISK_ASSESSMENT("risk-assessment"),
        REPORT_GENERATION("report-generation"),
        PERSONALIZATION("personalization"),
        OPTIMIZATION("optimization"),
        DATA_QUALITY("data-quality"),
        AUTOMATED_TAGGING("automated-tagging"),
        CATEGORIZATION("categorization"),
        CHATBOT("chatbot"),
        COMPUTER_VISION("computer-vision"),
        FRAUD_DETECTION("fraud-detection"),
        GATEWAY("gateway"),
        INFERENCE("inference"),
        BI_ANALYTICS("bi-analytics"),
        ANALYTICS("analytics");

        private final String code;

        ServiceType(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }

        public static ServiceType fromCode(String code) {
            for (ServiceType type : values()) {
                if (type.code.equals(code)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown service type: " + code);
        }
    }

    /**
     * Default constructor.
     */
    public AIExecutionMetadata() {
        this.metrics = new TreeMap<>();
        this.startedAt = Instant.now();
        this.retryCount = 0;
    }

    /**
     * Constructor with service type.
     *
     * @param serviceType The type of AI service
     */
    public AIExecutionMetadata(ServiceType serviceType) {
        this();
        this.serviceType = serviceType;
    }

    /**
     * Marks the execution as completed and calculates duration.
     */
    public void markCompleted() {
        this.completedAt = Instant.now();
        if (this.startedAt != null) {
            this.duration = Duration.between(this.startedAt, this.completedAt);
        }
    }

    /**
     * Gets the duration in milliseconds.
     *
     * @return Duration in milliseconds, or null if not completed
     */
    public Long getDurationMs() {
        return duration != null ? duration.toMillis() : null;
    }

    /**
     * Adds a custom metric.
     *
     * @param key   The metric key
     * @param value The metric value
     */
    public void addMetric(String key, Object value) {
        if (this.metrics == null) {
            this.metrics = new TreeMap<>();
        }
        this.metrics.put(key, value);
    }

    /**
     * Gets a metric value.
     *
     * @param key The metric key
     * @return The metric value, or null if not found
     */
    public Object getMetric(String key) {
        return metrics != null ? metrics.get(key) : null;
    }

    // Getters and Setters

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public String getServiceVersion() {
        return serviceVersion;
    }

    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Long getProcessingTimeMs() {
        return processingTimeMs;
    }

    public void setProcessingTimeMs(Long processingTimeMs) {
        this.processingTimeMs = processingTimeMs;
    }

    public Long getMemoryUsedBytes() {
        return memoryUsedBytes;
    }

    public void setMemoryUsedBytes(Long memoryUsedBytes) {
        this.memoryUsedBytes = memoryUsedBytes;
    }

    public Double getCpuUsagePercent() {
        return cpuUsagePercent;
    }

    public void setCpuUsagePercent(Double cpuUsagePercent) {
        this.cpuUsagePercent = cpuUsagePercent;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getSpanId() {
        return spanId;
    }

    public void setSpanId(String spanId) {
        this.spanId = spanId;
    }

    public String getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }

    public Double getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(Double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public Map<String, Object> getMetrics() {
        return metrics;
    }

    public void setMetrics(Map<String, Object> metrics) {
        this.metrics = metrics;
    }

    /**
     * Creates a builder for AIExecutionMetadata.
     *
     * @return A new builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for AIExecutionMetadata.
     */
    public static class Builder {
        private final AIExecutionMetadata metadata;

        private Builder() {
            this.metadata = new AIExecutionMetadata();
        }

        public Builder serviceType(ServiceType serviceType) {
            metadata.serviceType = serviceType;
            return this;
        }

        public Builder serviceVersion(String serviceVersion) {
            metadata.serviceVersion = serviceVersion;
            return this;
        }

        public Builder modelVersion(String modelVersion) {
            metadata.modelVersion = modelVersion;
            return this;
        }

        public Builder confidenceScore(Double confidenceScore) {
            metadata.confidenceScore = confidenceScore;
            return this;
        }

        public Builder traceId(String traceId) {
            metadata.traceId = traceId;
            return this;
        }

        public Builder spanId(String spanId) {
            metadata.spanId = spanId;
            return this;
        }

        public Builder processingTimeMs(Long processingTimeMs) {
            metadata.processingTimeMs = processingTimeMs;
            return this;
        }

        public Builder addMetric(String key, Object value) {
            metadata.addMetric(key, value);
            return this;
        }

        public AIExecutionMetadata build() {
            return metadata;
        }
    }
}
