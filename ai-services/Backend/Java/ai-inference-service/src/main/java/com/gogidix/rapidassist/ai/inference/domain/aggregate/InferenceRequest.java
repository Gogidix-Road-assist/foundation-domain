package com.gogidix.rapidassist.ai.inference.domain.aggregate;

import com.gogidix.rapidassist.ai.inference.domain.model.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Aggregate Root for Inference Request.
 * Manages the lifecycle and business logic of AI model inference requests.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InferenceRequest {

    private UUID id;
    private String tenantId;
    private String requestId;
    private String modelId;
    private String modelVersion;
    private InferenceStatus status;
    private InferenceType inferenceType;
    private String inputData;
    private java.util.Map<String, Object> parameters;
    private Integer priority;
    private String requestedBy;
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private String errorMessage;
    private Integer retryCount;
    private java.util.Map<String, Object> metadata;
    private String createdBy;
    private String updatedBy;

    // Child entities (part of aggregate)
    @Builder.Default
    private List<InferenceResult> results = new ArrayList<>();

    @Builder.Default
    private List<InferenceMetrics> metrics = new ArrayList<>();

    /**
     * Business logic: Initialize a new inference request
     */
    public static InferenceRequest initialize(String tenantId, String modelId, String modelVersion,
                                            InferenceType inferenceType, String inputData, String requestedBy) {
        return InferenceRequest.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .requestId(UUID.randomUUID().toString())
                .modelId(modelId)
                .modelVersion(modelVersion)
                .status(InferenceStatus.PENDING)
                .inferenceType(inferenceType)
                .inputData(inputData)
                .parameters(new java.util.HashMap<>())
                .priority(0)
                .requestedBy(requestedBy)
                .createdAt(LocalDateTime.now())
                .retryCount(0)
                .metadata(new java.util.HashMap<>())
                .results(new ArrayList<>())
                .metrics(new ArrayList<>())
                .build();
    }

    /**
     * Business logic: Start inference processing
     */
    public void startProcessing() {
        if (this.status != InferenceStatus.PENDING) {
            throw new IllegalStateException("Cannot start inference in status: " + this.status);
        }
        this.status = InferenceStatus.PROCESSING;
        this.startedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Complete inference successfully
     */
    public void completeSuccessfully(InferenceResult result) {
        if (this.status != InferenceStatus.PROCESSING) {
            throw new IllegalStateException("Cannot complete inference in status: " + this.status);
        }
        this.status = InferenceStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
        result.setInferenceRequestId(this.id);
        result.setTenantId(this.tenantId);
        this.results.add(result);
    }

    /**
     * Business logic: Fail inference
     */
    public void fail(String errorMessage) {
        if (this.status == InferenceStatus.COMPLETED) {
            throw new IllegalStateException("Cannot fail completed inference");
        }
        this.status = InferenceStatus.FAILED;
        this.completedAt = LocalDateTime.now();
        this.errorMessage = errorMessage;
    }

    /**
     * Business logic: Retry inference
     */
    public void retry() {
        if (this.status != InferenceStatus.FAILED) {
            throw new IllegalStateException("Can only retry failed inferences");
        }
        this.status = InferenceStatus.PENDING;
        this.retryCount++;
        this.errorMessage = null;
    }

    /**
     * Business logic: Add parameter
     */
    public void addParameter(String key, Object value) {
        if (this.parameters == null) {
            this.parameters = new java.util.HashMap<>();
        }
        this.parameters.put(key, value);
    }

    /**
     * Business logic: Add metadata
     */
    public void addMetadata(String key, Object value) {
        if (this.metadata == null) {
            this.metadata = new java.util.HashMap<>();
        }
        this.metadata.put(key, value);
    }

    /**
     * Business logic: Add metric
     */
    public void addMetric(InferenceMetrics metric) {
        metric.setInferenceRequestId(this.id);
        metric.setTenantId(this.tenantId);
        this.metrics.add(metric);
    }

    /**
     * Business logic: Get processing duration in milliseconds
     */
    public long getProcessingDurationMillis() {
        if (startedAt == null || completedAt == null) {
            return 0;
        }
        return java.time.Duration.between(startedAt, completedAt).toMillis();
    }

    /**
     * Business logic: Check if inference is pending
     */
    public boolean isPending() {
        return InferenceStatus.PENDING.equals(this.status);
    }

    /**
     * Business logic: Check if inference is processing
     */
    public boolean isProcessing() {
        return InferenceStatus.PROCESSING.equals(this.status);
    }

    /**
     * Business logic: Check if inference is completed
     */
    public boolean isCompleted() {
        return InferenceStatus.COMPLETED.equals(this.status);
    }

    /**
     * Business logic: Check if inference is failed
     */
    public boolean isFailed() {
        return InferenceStatus.FAILED.equals(this.status);
    }

    /**
     * Business logic: Get latest result
     */
    public InferenceResult getLatestResult() {
        return this.results.isEmpty() ? null : this.results.get(this.results.size() - 1);
    }

    /**
     * Business logic: Get all metrics for a specific type
     */
    public List<InferenceMetrics> getMetricsByType(String metricType) {
        return this.metrics.stream()
                .filter(m -> metricType.equals(m.getMetricType()))
                .toList();
    }
}
