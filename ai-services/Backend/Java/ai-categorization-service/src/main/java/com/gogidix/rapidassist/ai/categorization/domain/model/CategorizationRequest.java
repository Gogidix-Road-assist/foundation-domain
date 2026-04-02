package com.gogidix.rapidassist.ai.categorization.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain model representing a Categorization Request.
 * Requests content to be categorized by the AI service.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategorizationRequest {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private String contentId;
    private String contentType;
    private String content;
    private UUID taxonomyId;
    private boolean batchRequest;
    private Integer maxCategories;
    private Double minConfidence;
    private CategorizationStatus status;
    private String errorMessage;
    private java.util.Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
    private String createdBy;

    /**
     * Business logic: Create a new categorization request
     */
    public static CategorizationRequest create(String tenantId, String contentId, String contentType, String content, UUID taxonomyId) {
        return CategorizationRequest.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .contentId(contentId)
                .contentType(contentType)
                .content(content)
                .taxonomyId(taxonomyId)
                .batchRequest(false)
                .maxCategories(5)
                .minConfidence(0.5)
                .status(CategorizationStatus.PENDING)
                .metadata(new java.util.HashMap<>())
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * Business logic: Mark as processing
     */
    public void markAsProcessing() {
        this.status = CategorizationStatus.PROCESSING;
    }

    /**
     * Business logic: Mark as completed
     */
    public void markAsCompleted() {
        this.status = CategorizationStatus.COMPLETED;
        this.processedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Mark as failed
     */
    public void markAsFailed(String errorMessage) {
        this.status = CategorizationStatus.FAILED;
        this.errorMessage = errorMessage;
        this.processedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Check if request is pending
     */
    public boolean isPending() {
        return CategorizationStatus.PENDING.equals(this.status);
    }

    /**
     * Business logic: Check if request is processed
     */
    public boolean isProcessed() {
        return CategorizationStatus.COMPLETED.equals(this.status) || 
               CategorizationStatus.FAILED.equals(this.status);
    }

    /**
     * Business logic: Update metadata
     */
    public void updateMetadata(String key, Object value) {
        if (this.metadata == null) {
            this.metadata = new java.util.HashMap<>();
        }
        this.metadata.put(key, value);
    }
}
