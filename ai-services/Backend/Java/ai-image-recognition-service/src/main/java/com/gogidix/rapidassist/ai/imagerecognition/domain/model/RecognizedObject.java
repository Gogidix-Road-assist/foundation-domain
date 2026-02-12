package com.gogidix.rapidassist.ai.imagerecognition.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Domain model representing a recognized object in an image.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecognizedObject {

    private UUID id;
    private String tenantId;
    private UUID imageRecognitionId;
    private String label;
    private String objectType;
    private Double confidence;
    private BoundingBox boundingBox;
    private java.util.Map<String, Object> attributes;

    /**
     * Business logic: Check if confidence meets threshold
     */
    public boolean meetsConfidenceThreshold(double threshold) {
        return confidence != null && confidence >= threshold;
    }

    /**
     * Business logic: Check if object is of specific type
     */
    public boolean isOfType(String type) {
        return objectType != null && objectType.equalsIgnoreCase(type);
    }

    /**
     * Business logic: Get area of bounding box
     */
    public double getBoundingBoxArea() {
        if (boundingBox == null) {
            return 0;
        }
        return boundingBox.getWidth() * boundingBox.getHeight();
    }
}
