package com.gogidix.rapidassist.ai.computervision.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Domain model representing a detected face in an image
 * Pure domain model without MongoDB annotations
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FaceDetection {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private UUID imageAnalysisId;
    private FaceEmotion emotion;
    private Double emotionConfidence;
    private Double confidenceScore;
    private Integer age;
    private String gender;
    private ObjectDetection.BoundingBox boundingBox;
    @Builder.Default
    private List<FacialLandmark> landmarks = new ArrayList<>();
    private Boolean hasGlasses;
    private Boolean hasBeard;
    private Boolean hasMustache;
    private Double smileConfidence;

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
     * Business logic: Check if face is confidently detected
     */
    public boolean isConfident(double threshold) {
        return confidenceScore != null && confidenceScore >= threshold;
    }

    /**
     * Business logic: Check if person is smiling
     */
    public boolean isSmiling(double threshold) {
        return smileConfidence != null && smileConfidence >= threshold;
    }

    /**
     * Business logic: Get primary emotion
     */
    public FaceEmotion getPrimaryEmotion() {
        return emotion != null ? emotion : FaceEmotion.UNKNOWN;
    }

    /**
     * Business logic: Check if emotion is confidently detected
     */
    public boolean hasConfidentEmotion(double threshold) {
        return emotion != null && emotionConfidence != null && emotionConfidence >= threshold;
    }

    /**
     * Business logic: Get face center point
     */
    public ObjectDetection.Point getCenterPoint() {
        if (boundingBox != null) {
            return boundingBox.getCenter();
        }
        return null;
    }

    /**
     * Business logic: Check if face is in center of image
     */
    public boolean isInCenter(double imageWidth, double imageHeight, double threshold) {
        ObjectDetection.Point center = getCenterPoint();
        if (center != null && center.getX() != null && center.getY() != null) {
            double centerX = imageWidth / 2;
            double centerY = imageHeight / 2;
            double distance = Math.sqrt(Math.pow(center.getX() - centerX, 2) + Math.pow(center.getY() - centerY, 2));
            return distance < threshold;
        }
        return false;
    }

    /**
     * Business logic: Get age group
     */
    public String getAgeGroup() {
        if (age == null) {
            return "Unknown";
        }
        if (age < 13) return "Child";
        if (age < 20) return "Teenager";
        if (age < 60) return "Adult";
        return "Senior";
    }

    /**
     * Inner class representing facial landmarks
     */
    @Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FacialLandmark {
        private String type;
        private ObjectDetection.Point position;
        private Double confidence;
    }
}
