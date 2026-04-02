package com.gogidix.rapidassist.ai.computervision.domain.aggregate;

import com.gogidix.rapidassist.ai.computervision.domain.model.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Aggregate Root for Image Analysis.
 * Manages the lifecycle and business logic of image analysis operations.
 * Contains: ObjectDetection, FaceDetection, TextRecognition, and ImageClassification as child entities.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageAnalysisAggregate {

    private UUID id;
    private String tenantId;
    private String userId;
    private String imageUrl;
    private String imageStoragePath;
    private ImageFormat format;
    private Long fileSize;
    private Integer width;
    private Integer height;
    private AnalysisStatus status;
    private String analysisType;
    private Double overallConfidence;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
    private String createdBy;
    private String updatedBy;
    private Long processingTimeMs;

    // Child entities (part of aggregate)
    @Builder.Default
    private List<ObjectDetection> detectedObjects = new ArrayList<>();

    @Builder.Default
    private List<FaceDetection> detectedFaces = new ArrayList<>();

    @Builder.Default
    private List<TextRecognition> recognizedTexts = new ArrayList<>();

    @Builder.Default
    private List<ImageClassification> classifications = new ArrayList<>();

    /**
     * Business logic: Initialize a new image analysis
     */
    public static ImageAnalysisAggregate initialize(String tenantId, String userId, String imageUrl,
                                                     String imageStoragePath, ImageFormat format,
                                                     Long fileSize, Integer width, Integer height, String analysisType) {
        return ImageAnalysisAggregate.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .userId(userId)
                .imageUrl(imageUrl)
                .imageStoragePath(imageStoragePath)
                .format(format)
                .fileSize(fileSize)
                .width(width)
                .height(height)
                .status(AnalysisStatus.PENDING)
                .analysisType(analysisType)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .detectedObjects(new ArrayList<>())
                .detectedFaces(new ArrayList<>())
                .recognizedTexts(new ArrayList<>())
                .classifications(new ArrayList<>())
                .build();
    }

    /**
     * Business logic: Start processing
     */
    public void startProcessing() {
        if (this.status == AnalysisStatus.PENDING) {
            this.status = AnalysisStatus.PROCESSING;
            this.updatedAt = LocalDateTime.now();
        } else {
            throw new IllegalStateException("Cannot start processing from status: " + this.status);
        }
    }

    /**
     * Business logic: Complete analysis successfully
     */
    public void completeAnalysis(double overallConfidence) {
        if (this.status == AnalysisStatus.PROCESSING) {
            this.status = AnalysisStatus.COMPLETED;
            this.overallConfidence = overallConfidence;
            this.completedAt = LocalDateTime.now();
            this.updatedAt = LocalDateTime.now();
            if (this.createdAt != null) {
                this.processingTimeMs = java.time.Duration.between(createdAt, completedAt).toMillis();
            }
        } else {
            throw new IllegalStateException("Cannot complete analysis from status: " + this.status);
        }
    }

    /**
     * Business logic: Fail analysis
     */
    public void failAnalysis(String errorMessage) {
        if (this.status == AnalysisStatus.PROCESSING || this.status == AnalysisStatus.PENDING) {
            this.status = AnalysisStatus.FAILED;
            this.errorMessage = errorMessage;
            this.completedAt = LocalDateTime.now();
            this.updatedAt = LocalDateTime.now();
        } else {
            throw new IllegalStateException("Cannot fail analysis from status: " + this.status);
        }
    }

    /**
     * Business logic: Cancel analysis
     */
    public void cancelAnalysis() {
        if (this.status == AnalysisStatus.PENDING || this.status == AnalysisStatus.PROCESSING) {
            this.status = AnalysisStatus.CANCELLED;
            this.updatedAt = LocalDateTime.now();
        } else {
            throw new IllegalStateException("Cannot cancel analysis from status: " + this.status);
        }
    }

    /**
     * Business logic: Add detected object
     */
    public void addDetectedObject(ObjectDetection object) {
        object.setId(UUID.randomUUID());
        object.setTenantId(this.tenantId);
        object.setImageAnalysisId(this.id);
        this.detectedObjects.add(object);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Add detected face
     */
    public void addDetectedFace(FaceDetection face) {
        face.setId(UUID.randomUUID());
        face.setTenantId(this.tenantId);
        face.setImageAnalysisId(this.id);
        this.detectedFaces.add(face);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Add recognized text
     */
    public void addRecognizedText(TextRecognition text) {
        text.setId(UUID.randomUUID());
        text.setTenantId(this.tenantId);
        text.setImageAnalysisId(this.id);
        this.recognizedTexts.add(text);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Add classification
     */
    public void addClassification(ImageClassification classification) {
        classification.setId(UUID.randomUUID());
        classification.setTenantId(this.tenantId);
        classification.setImageAnalysisId(this.id);
        this.classifications.add(classification);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Get total detections count
     */
    public int getTotalDetectionsCount() {
        return this.detectedObjects.size() + this.detectedFaces.size() +
               this.recognizedTexts.size() + this.classifications.size();
    }

    /**
     * Business logic: Get high confidence detections
     */
    public List<ObjectDetection> getHighConfidenceObjects(double threshold) {
        return this.detectedObjects.stream()
                .filter(obj -> obj.isConfident(threshold))
                .toList();
    }

    /**
     * Business logic: Get high confidence faces
     */
    public List<FaceDetection> getHighConfidenceFaces(double threshold) {
        return this.detectedFaces.stream()
                .filter(face -> face.isConfident(threshold))
                .toList();
    }

    /**
     * Business logic: Check if has any text
     */
    public boolean hasExtractedText() {
        return this.recognizedTexts.stream()
                .anyMatch(TextRecognition::hasText);
    }

    /**
     * Business logic: Get all extracted text
     */
    public String getAllExtractedText() {
        return this.recognizedTexts.stream()
                .filter(TextRecognition::hasText)
                .map(TextRecognition::getFullText)
                .reduce("", (a, b) -> a + "\n" + b);
    }

    /**
     * Business logic: Get primary classification
     */
    public ImageClassification getPrimaryClassification() {
        return this.classifications.isEmpty() ? null : this.classifications.get(0);
    }

    /**
     * Business logic: Check if is completed
     */
    public boolean isCompleted() {
        return AnalysisStatus.COMPLETED.equals(this.status);
    }

    /**
     * Business logic: Check if is failed
     */
    public boolean isFailed() {
        return AnalysisStatus.FAILED.equals(this.status);
    }

    /**
     * Business logic: Check if is processing
     */
    public boolean isProcessing() {
        return AnalysisStatus.PROCESSING.equals(this.status);
    }

    /**
     * Business logic: Check if is pending
     */
    public boolean isPending() {
        return AnalysisStatus.PENDING.equals(this.status);
    }

    /**
     * Business logic: Get image resolution
     */
    public String getResolution() {
        if (width != null && height != null) {
            return width + "x" + height;
        }
        return "Unknown";
    }

    /**
     * Business logic: Get aspect ratio
     */
    public double getAspectRatio() {
        if (width != null && height != null && height > 0) {
            return (double) width / height;
        }
        return 0;
    }

    /**
     * Business logic: Check if image is landscape
     */
    public boolean isLandscape() {
        return width != null && height != null && width > height;
    }

    /**
     * Business logic: Check if image is portrait
     */
    public boolean isPortrait() {
        return width != null && height != null && height > width;
    }

    /**
     * Business logic: Get confidence level
     */
    public ConfidenceLevel getOverallConfidenceLevel() {
        if (overallConfidence == null) {
            return ConfidenceLevel.VERY_LOW;
        }
        return ConfidenceLevel.fromScore(overallConfidence);
    }

    /**
     * Business logic: Check if has high confidence
     */
    public boolean hasHighConfidence(double threshold) {
        return overallConfidence != null && overallConfidence >= threshold;
    }

    /**
     * Business logic: Get processing duration
     */
    public Long getProcessingDuration() {
        if (completedAt == null || createdAt == null) {
            return null;
        }
        return java.time.Duration.between(createdAt, completedAt).toMillis();
    }
}
