package com.gogidix.rapidassist.ai.computervision.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Domain model representing an image analysis request and result
 * Pure domain model without MongoDB annotations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageAnalysis {

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
    private Double confidenceScore;
    private String errorMessage;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
    private String createdBy;
    private String updatedBy;
    private Long processingTimeMs;

    /**
     * Business logic: Check if analysis is completed
     */
    public boolean isCompleted() {
        return AnalysisStatus.COMPLETED.equals(this.status);
    }

    /**
     * Business logic: Check if analysis failed
     */
    public boolean isFailed() {
        return AnalysisStatus.FAILED.equals(this.status);
    }

    /**
     * Business logic: Check if analysis is processing
     */
    public boolean isProcessing() {
        return AnalysisStatus.PROCESSING.equals(this.status);
    }

    /**
     * Business logic: Check if analysis is pending
     */
    public boolean isPending() {
        return AnalysisStatus.PENDING.equals(this.status);
    }

    /**
     * Business logic: Get confidence level
     */
    public ConfidenceLevel getConfidenceLevel() {
        if (confidenceScore == null) {
            return ConfidenceLevel.VERY_LOW;
        }
        return ConfidenceLevel.fromScore(confidenceScore);
    }

    /**
     * Business logic: Check if confidence is high enough
     */
    public boolean hasHighConfidence(double threshold) {
        return confidenceScore != null && confidenceScore >= threshold;
    }

    /**
     * Business logic: Calculate processing duration
     */
    public Long getProcessingDuration() {
        if (completedAt == null || createdAt == null) {
            return null;
        }
        return java.time.Duration.between(createdAt, completedAt).toMillis();
    }

    /**
     * Business logic: Mark as processing
     */
    public void markAsProcessing() {
        if (this.status == AnalysisStatus.PENDING) {
            this.status = AnalysisStatus.PROCESSING;
            this.updatedAt = LocalDateTime.now();
        } else {
            throw new IllegalStateException("Cannot mark analysis as processing from status: " + this.status);
        }
    }

    /**
     * Business logic: Mark as completed
     */
    public void markAsCompleted(double confidenceScore) {
        if (this.status == AnalysisStatus.PROCESSING) {
            this.status = AnalysisStatus.COMPLETED;
            this.confidenceScore = confidenceScore;
            this.completedAt = LocalDateTime.now();
            this.updatedAt = LocalDateTime.now();
            this.processingTimeMs = getProcessingDuration();
        } else {
            throw new IllegalStateException("Cannot mark analysis as completed from status: " + this.status);
        }
    }

    /**
     * Business logic: Mark as failed
     */
    public void markAsFailed(String errorMessage) {
        if (this.status == AnalysisStatus.PROCESSING || this.status == AnalysisStatus.PENDING) {
            this.status = AnalysisStatus.FAILED;
            this.errorMessage = errorMessage;
            this.completedAt = LocalDateTime.now();
            this.updatedAt = LocalDateTime.now();
        } else {
            throw new IllegalStateException("Cannot mark analysis as failed from status: " + this.status);
        }
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
}
