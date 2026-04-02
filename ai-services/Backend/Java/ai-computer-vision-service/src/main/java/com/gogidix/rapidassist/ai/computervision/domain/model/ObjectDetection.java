package com.gogidix.rapidassist.ai.computervision.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Domain model representing a detected object in an image
 * Pure domain model without MongoDB annotations
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ObjectDetection {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private UUID imageAnalysisId;
    private DetectionType type;
    private String label;
    private Double confidenceScore;
    private BoundingBox boundingBox;
    private String color;
    private String description;
    private Integer objectCount;

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
     * Business logic: Check if detection is confident
     */
    public boolean isConfident(double threshold) {
        return confidenceScore != null && confidenceScore >= threshold;
    }

    /**
     * Business logic: Get bounding box area
     */
    public double getBoundingBoxArea() {
        if (boundingBox != null) {
            return boundingBox.getArea();
        }
        return 0;
    }

    /**
     * Business logic: Check if object is large (occupies more than 50% of image)
     */
    public boolean isLargeObject(double imageWidth, double imageHeight) {
        if (boundingBox != null && imageWidth > 0 && imageHeight > 0) {
            double objectArea = getBoundingBoxArea();
            double imageArea = imageWidth * imageHeight;
            return objectArea > (imageArea * 0.5);
        }
        return false;
    }

    /**
     * Business logic: Get center point of bounding box
     */
    public Point getCenterPoint() {
        if (boundingBox != null) {
            return boundingBox.getCenter();
        }
        return null;
    }

    /**
     * Inner class representing bounding box coordinates
     */
    @Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BoundingBox {
        private Double x;
        private Double y;
        private Double width;
        private Double height;

        public double getArea() {
            return width != null && height != null ? width * height : 0;
        }

        public Point getCenter() {
            double centerX = x != null && width != null ? x + (width / 2) : 0;
            double centerY = y != null && height != null ? y + (height / 2) : 0;
            return new Point(centerX, centerY);
        }
    }

    /**
     * Inner class representing a 2D point
     */
    @Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Point {
        private Double x;
        private Double y;
    }
}
