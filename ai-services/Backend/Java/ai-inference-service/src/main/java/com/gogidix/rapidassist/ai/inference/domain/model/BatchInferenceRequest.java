package com.gogidix.rapidassist.ai.inference.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Domain model representing a batch inference request
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "batch_inference_requests")
public class BatchInferenceRequest {

    @Id
    private UUID id;

    @Indexed
    private String tenantId;

    @Indexed
    private String batchId;

    private String modelId;
    private String modelVersion;
    private BatchStatus status;
    private List<String> inputItems;
    private List<String> outputItems;
    private Integer totalItems;
    private Integer completedItems;
    private Integer failedItems;
    private Double progress;
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private String createdBy;
    private java.util.Map<String, Object> parameters;
    private String errorMessage;

    public static BatchInferenceRequest create(String tenantId, String modelId,
                                              String modelVersion, List<String> inputItems, String createdBy) {
        return BatchInferenceRequest.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .batchId(UUID.randomUUID().toString())
                .modelId(modelId)
                .modelVersion(modelVersion)
                .status(BatchStatus.PENDING)
                .inputItems(inputItems)
                .outputItems(new ArrayList<>())
                .totalItems(inputItems.size())
                .completedItems(0)
                .failedItems(0)
                .progress(0.0)
                .createdAt(LocalDateTime.now())
                .createdBy(createdBy)
                .parameters(new java.util.HashMap<>())
                .build();
    }

    public void startProcessing() {
        if (this.status != BatchStatus.PENDING) {
            throw new IllegalStateException("Cannot start batch in status: " + this.status);
        }
        this.status = BatchStatus.PROCESSING;
        this.startedAt = LocalDateTime.now();
    }

    public void addItemResult(String output) {
        this.outputItems.add(output);
        this.completedItems++;
        updateProgress();
    }

    public void addItemFailure() {
        this.failedItems++;
        updateProgress();
    }

    public void complete() {
        if (this.status != BatchStatus.PROCESSING) {
            throw new IllegalStateException("Cannot complete batch in status: " + this.status);
        }
        this.status = BatchStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
        this.progress = 100.0;
    }

    public void fail(String errorMessage) {
        this.status = BatchStatus.FAILED;
        this.completedAt = LocalDateTime.now();
        this.errorMessage = errorMessage;
    }

    private void updateProgress() {
        this.progress = (double) (this.completedItems + this.failedItems) / this.totalItems * 100;
    }

    public boolean isCompleted() {
        return BatchStatus.COMPLETED.equals(this.status);
    }

    public boolean isProcessing() {
        return BatchStatus.PROCESSING.equals(this.status);
    }

    public boolean isPending() {
        return BatchStatus.PENDING.equals(this.status);
    }
}
