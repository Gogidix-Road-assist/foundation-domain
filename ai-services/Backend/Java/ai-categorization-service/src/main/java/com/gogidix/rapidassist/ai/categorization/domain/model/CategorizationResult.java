package com.gogidix.rapidassist.ai.categorization.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Domain model representing a Categorization Result.
 * Contains the AI-predicted categories for content.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategorizationResult {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private UUID requestId;
    private String contentId;
    private String contentType;
    private UUID taxonomyId;
    private List<CategoryPrediction> predictions;
    private CategorizationStatus status;
    private String errorMessage;
    private String modelVersion;
    private Double processingTimeMs;
    private java.util.Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private String createdBy;

    /**
     * Business logic: Create a new result from request
     */
    public static CategorizationResult fromRequest(CategorizationRequest request) {
        return CategorizationResult.builder()
                .id(UUID.randomUUID())
                .tenantId(request.getTenantId())
                .requestId(request.getId())
                .contentId(request.getContentId())
                .contentType(request.getContentType())
                .taxonomyId(request.getTaxonomyId())
                .predictions(new ArrayList<>())
                .status(CategorizationStatus.PENDING)
                .metadata(new java.util.HashMap<>())
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * Business logic: Add prediction
     */
    public void addPrediction(UUID categoryId, String categoryName, String categoryPath, Double confidence) {
        if (this.predictions == null) {
            this.predictions = new ArrayList<>();
        }
        CategoryPrediction prediction = CategoryPrediction.builder()
                .categoryId(categoryId)
                .categoryName(categoryName)
                .categoryPath(categoryPath)
                .confidence(confidence)
                .build();
        this.predictions.add(prediction);
    }

    /**
     * Business logic: Get high confidence predictions
     */
    public List<CategoryPrediction> getHighConfidencePredictions(double threshold) {
        if (this.predictions == null) {
            return new ArrayList<>();
        }
        return this.predictions.stream()
                .filter(p -> p.getConfidence() >= threshold)
                .toList();
    }

    /**
     * Business logic: Get top prediction
     */
    public CategoryPrediction getTopPrediction() {
        if (this.predictions == null || this.predictions.isEmpty()) {
            return null;
        }
        return this.predictions.stream()
                .max((p1, p2) -> Double.compare(p1.getConfidence(), p2.getConfidence()))
                .orElse(null);
    }

    /**
     * Business logic: Mark as completed
     */
    public void markAsCompleted(String modelVersion, double processingTimeMs) {
        this.status = CategorizationStatus.COMPLETED;
        this.modelVersion = modelVersion;
        this.processingTimeMs = processingTimeMs;
    }

    /**
     * Business logic: Mark as failed
     */
    public void markAsFailed(String errorMessage) {
        this.status = CategorizationStatus.FAILED;
        this.errorMessage = errorMessage;
    }

    /**
     * Business logic: Check if result is successful
     */
    public boolean isSuccessful() {
        return CategorizationStatus.COMPLETED.equals(this.status);
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

    /**
     * Nested class for category predictions
     */
    @Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryPrediction {
        private UUID categoryId;
        private String categoryName;
        private String categoryPath;
        private Double confidence;
        private java.util.Map<String, Object> additionalInfo;

        /**
         * Business logic: Check if prediction is high confidence
         */
        public boolean isHighConfidence(double threshold) {
            return this.confidence != null && this.confidence >= threshold;
        }
    }
}
