package com.gogidix.rapidassist.shared.ai.contracts.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("AIExecutionMetadata Tests")
class AIExecutionMetadataTest {

    @Test
    @DisplayName("Should create metadata with default values")
    void shouldCreateMetadataWithDefaults() {
        AIExecutionMetadata metadata = new AIExecutionMetadata();

        assertThat(metadata.getStartedAt()).isNotNull();
        assertThat(metadata.getRetryCount()).isEqualTo(0);
        assertThat(metadata.getMetrics()).isNotNull();
    }

    @Test
    @DisplayName("Should create metadata with service type")
    void shouldCreateMetadataWithServiceType() {
        AIExecutionMetadata metadata = new AIExecutionMetadata(AIExecutionMetadata.ServiceType.CONTENT_MODERATION);

        assertThat(metadata.getServiceType()).isEqualTo(AIExecutionMetadata.ServiceType.CONTENT_MODERATION);
        assertThat(metadata.getStartedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should mark completion and calculate duration")
    void shouldMarkCompletionAndCalculateDuration() {
        AIExecutionMetadata metadata = new AIExecutionMetadata();
        metadata.markCompleted();

        assertThat(metadata.getCompletedAt()).isNotNull();
        assertThat(metadata.getDuration()).isNotNull();
        assertThat(metadata.getDurationMs()).isGreaterThanOrEqualTo(0L);
    }

    @Test
    @DisplayName("Should add and retrieve custom metrics")
    void shouldAddAndRetrieveCustomMetrics() {
        AIExecutionMetadata metadata = new AIExecutionMetadata();
        metadata.addMetric("custom_metric", 42.0);
        metadata.addMetric("another_metric", "value");

        assertThat(metadata.getMetric("custom_metric")).isEqualTo(42.0);
        assertThat(metadata.getMetric("another_metric")).isEqualTo("value");
        assertThat(metadata.getMetric("nonexistent")).isNull();
    }

    @Test
    @DisplayName("Should create metadata using builder")
    void shouldCreateMetadataUsingBuilder() {
        AIExecutionMetadata metadata = AIExecutionMetadata.builder()
                .serviceType(AIExecutionMetadata.ServiceType.NLP_PROCESSING)
                .serviceVersion("1.0.0")
                .modelVersion("gpt-4")
                .confidenceScore(0.95)
                .traceId("trace-123")
                .spanId("span-456")
                .processingTimeMs(150L)
                .addMetric("tokens_used", 100)
                .build();

        assertThat(metadata.getServiceType()).isEqualTo(AIExecutionMetadata.ServiceType.NLP_PROCESSING);
        assertThat(metadata.getServiceVersion()).isEqualTo("1.0.0");
        assertThat(metadata.getModelVersion()).isEqualTo("gpt-4");
        assertThat(metadata.getConfidenceScore()).isEqualTo(0.95);
        assertThat(metadata.getTraceId()).isEqualTo("trace-123");
        assertThat(metadata.getSpanId()).isEqualTo("span-456");
        assertThat(metadata.getProcessingTimeMs()).isEqualTo(150L);
        assertThat(metadata.getMetric("tokens_used")).isEqualTo(100);
    }

    @Test
    @DisplayName("Should convert service type from code")
    void shouldConvertServiceTypeFromCode() {
        assertThat(AIExecutionMetadata.ServiceType.fromCode("content-moderation"))
                .isEqualTo(AIExecutionMetadata.ServiceType.CONTENT_MODERATION);

        assertThat(AIExecutionMetadata.ServiceType.fromCode("nlp-processing"))
                .isEqualTo(AIExecutionMetadata.ServiceType.NLP_PROCESSING);

        assertThat(AIExecutionMetadata.ServiceType.fromCode("image-recognition"))
                .isEqualTo(AIExecutionMetadata.ServiceType.IMAGE_RECOGNITION);
    }

    @Test
    @DisplayName("Should throw exception for unknown service type code")
    void shouldThrowForUnknownServiceTypeCode() {
        assertThatThrownBy(() -> AIExecutionMetadata.ServiceType.fromCode("unknown"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown service type");
    }

    @Test
    @DisplayName("Should get service type code")
    void shouldGetServiceTypeCode() {
        assertThat(AIExecutionMetadata.ServiceType.CONTENT_MODERATION.getCode())
                .isEqualTo("content-moderation");

        assertThat(AIExecutionMetadata.ServiceType.NLP_PROCESSING.getCode())
                .isEqualTo("nlp-processing");
    }

    @Test
    @DisplayName("Should set all properties via setters")
    void shouldSetAllPropertiesViaSetters() {
        AIExecutionMetadata metadata = new AIExecutionMetadata();
        metadata.setServiceType(AIExecutionMetadata.ServiceType.SENTIMENT_ANALYSIS);
        metadata.setServiceVersion("2.0.0");
        metadata.setModelVersion("bert-base");
        metadata.setConfidenceScore(0.87);
        metadata.setTraceId("trace-789");
        metadata.setSpanId("span-012");
        metadata.setProcessingTimeMs(200L);
        metadata.setMemoryUsedBytes(1024000L);
        metadata.setCpuUsagePercent(75.5);
        metadata.setRetryCount(2);

        assertThat(metadata.getServiceType()).isEqualTo(AIExecutionMetadata.ServiceType.SENTIMENT_ANALYSIS);
        assertThat(metadata.getServiceVersion()).isEqualTo("2.0.0");
        assertThat(metadata.getModelVersion()).isEqualTo("bert-base");
        assertThat(metadata.getConfidenceScore()).isEqualTo(0.87);
        assertThat(metadata.getTraceId()).isEqualTo("trace-789");
        assertThat(metadata.getSpanId()).isEqualTo("span-012");
        assertThat(metadata.getProcessingTimeMs()).isEqualTo(200L);
        assertThat(metadata.getMemoryUsedBytes()).isEqualTo(1024000L);
        assertThat(metadata.getCpuUsagePercent()).isEqualTo(75.5);
        assertThat(metadata.getRetryCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should handle null duration gracefully")
    void shouldHandleNullDuration() {
        AIExecutionMetadata metadata = new AIExecutionMetadata();

        assertThat(metadata.getDurationMs()).isNull();
    }

    @Test
    @DisplayName("Should replace metrics map when set")
    void shouldReplaceMetricsMap() {
        AIExecutionMetadata metadata = new AIExecutionMetadata();
        metadata.addMetric("old_metric", 1.0);

        java.util.Map<String, Object> newMetrics = new java.util.TreeMap<>();
        newMetrics.put("new_metric", 2.0);
        metadata.setMetrics(newMetrics);

        assertThat(metadata.getMetric("old_metric")).isNull();
        assertThat(metadata.getMetric("new_metric")).isEqualTo(2.0);
    }

    @Test
    @DisplayName("Should set duration explicitly")
    void shouldSetDurationExplicitly() {
        AIExecutionMetadata metadata = new AIExecutionMetadata();
        Duration customDuration = Duration.ofMillis(500);
        metadata.setDuration(customDuration);

        assertThat(metadata.getDuration()).isEqualTo(customDuration);
        assertThat(metadata.getDurationMs()).isEqualTo(500L);
    }
}
