package com.gogidix.rapidassist.ai.imagerecognition.domain.aggregate;

import com.gogidix.rapidassist.ai.imagerecognition.domain.model.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Aggregate Root for Image Recognition.
 * Manages the lifecycle and business logic of image recognition tasks.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageRecognition {

    private UUID id;
    private String tenantId;
    private String userId;
    private String requestId;
    private String imageUrl;
    private String imageStoragePath;
    private RecognitionStatus status;
    private RecognitionType recognitionType;
    private String aiModelUsed;
    private Double processingTimeMs;
    private String errorMessage;
    private java.util.Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
    private String createdBy;
    private String updatedBy;

    // Child entities (part of aggregate)
    @Builder.Default
    private List<RecognizedObject> recognizedObjects = new ArrayList<>();

    @Builder.Default
    private List<SceneLabel> sceneLabels = new ArrayList<>();

    @Builder.Default
    private List<BrandDetection> brandDetections = new ArrayList<>();

    @Builder.Default
    private List<ImageFeature> imageFeatures = new ArrayList<>();

    /**
     * Business logic: Initialize a new recognition request
     */
    public static ImageRecognition initialize(String tenantId, String userId, String imageUrl, RecognitionType type) {
        return ImageRecognition.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .userId(userId)
                .requestId(UUID.randomUUID().toString())
                .imageUrl(imageUrl)
                .status(RecognitionStatus.PENDING)
                .recognitionType(type)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .recognizedObjects(new ArrayList<>())
                .sceneLabels(new ArrayList<>())
                .brandDetections(new ArrayList<>())
                .imageFeatures(new ArrayList<>())
                .build();
    }

    /**
     * Business logic: Start processing
     */
    public void startProcessing() {
        if (this.status != RecognitionStatus.PENDING) {
            throw new IllegalStateException("Cannot start processing from status: " + this.status);
        }
        this.status = RecognitionStatus.PROCESSING;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Complete processing
     */
    public void completeProcessing() {
        if (this.status != RecognitionStatus.PROCESSING) {
            throw new IllegalStateException("Cannot complete processing from status: " + this.status);
        }
        this.status = RecognitionStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
        this.updatedAt = this.completedAt;
    }

    /**
     * Business logic: Fail processing
     */
    public void failProcessing(String errorMessage) {
        if (this.status != RecognitionStatus.PROCESSING) {
            throw new IllegalStateException("Cannot fail processing from status: " + this.status);
        }
        this.status = RecognitionStatus.FAILED;
        this.errorMessage = errorMessage;
        this.completedAt = LocalDateTime.now();
        this.updatedAt = this.completedAt;
    }

    /**
     * Business logic: Cancel processing
     */
    public void cancel() {
        if (this.status == RecognitionStatus.COMPLETED || this.status == RecognitionStatus.FAILED) {
            throw new IllegalStateException("Cannot cancel from status: " + this.status);
        }
        this.status = RecognitionStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Check if processing is complete
     */
    public boolean isComplete() {
        return RecognitionStatus.COMPLETED.equals(this.status);
    }

    /**
     * Business logic: Check if processing failed
     */
    public boolean hasFailed() {
        return RecognitionStatus.FAILED.equals(this.status);
    }

    /**
     * Business logic: Check if processing is in progress
     */
    public boolean isProcessing() {
        return RecognitionStatus.PROCESSING.equals(this.status);
    }

    /**
     * Business logic: Add recognized object
     */
    public void addRecognizedObject(RecognizedObject object) {
        object.setId(UUID.randomUUID());
        object.setTenantId(this.tenantId);
        object.setImageRecognitionId(this.id);
        this.recognizedObjects.add(object);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Add scene label
     */
    public void addSceneLabel(SceneLabel label) {
        label.setId(UUID.randomUUID());
        label.setTenantId(this.tenantId);
        label.setImageRecognitionId(this.id);
        this.sceneLabels.add(label);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Add brand detection
     */
    public void addBrandDetection(BrandDetection detection) {
        detection.setId(UUID.randomUUID());
        detection.setTenantId(this.tenantId);
        detection.setImageRecognitionId(this.id);
        this.brandDetections.add(detection);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Add image feature
     */
    public void addImageFeature(ImageFeature feature) {
        feature.setId(UUID.randomUUID());
        feature.setTenantId(this.tenantId);
        feature.setImageRecognitionId(this.id);
        this.imageFeatures.add(feature);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Get high confidence objects
     */
    public List<RecognizedObject> getHighConfidenceObjects(double threshold) {
        return this.recognizedObjects.stream()
                .filter(obj -> obj.meetsConfidenceThreshold(threshold))
                .toList();
    }

    /**
     * Business logic: Get high confidence scene labels
     */
    public List<SceneLabel> getHighConfidenceSceneLabels(double threshold) {
        return this.sceneLabels.stream()
                .filter(label -> label.meetsConfidenceThreshold(threshold))
                .toList();
    }

    /**
     * Business logic: Get high confidence brand detections
     */
    public List<BrandDetection> getHighConfidenceBrandDetections(double threshold) {
        return this.brandDetections.stream()
                .filter(detection -> detection.meetsConfidenceThreshold(threshold))
                .toList();
    }

    /**
     * Business logic: Update processing time
     */
    public void setProcessingTime(long startTimeMs) {
        this.processingTimeMs = (double) (System.currentTimeMillis() - startTimeMs);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Update metadata
     */
    public void updateMetadata(String key, Object value) {
        if (this.metadata == null) {
            this.metadata = new java.util.HashMap<>();
        }
        this.metadata.put(key, value);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Check if has recognized objects
     */
    public boolean hasRecognizedObjects() {
        return this.recognizedObjects != null && !this.recognizedObjects.isEmpty();
    }

    /**
     * Business logic: Check if has scene labels
     */
    public boolean hasSceneLabels() {
        return this.sceneLabels != null && !this.sceneLabels.isEmpty();
    }

    /**
     * Business logic: Check if has brand detections
     */
    public boolean hasBrandDetections() {
        return this.brandDetections != null && !this.brandDetections.isEmpty();
    }

    /**
     * Business logic: Check if has image features
     */
    public boolean hasImageFeatures() {
        return this.imageFeatures != null && !this.imageFeatures.isEmpty();
    }
}
