package com.gogidix.rapidassist.ai.imagerecognition.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Domain model representing a scene label detected in an image.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SceneLabel {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private UUID imageRecognitionId;
    private String label;
    private String category;
    private Double confidence;
    private java.util.List<String> tags;
    private String description;

    /**
     * Business logic: Check if confidence meets threshold
     */
    public boolean meetsConfidenceThreshold(double threshold) {
        return confidence != null && confidence >= threshold;
    }

    /**
     * Business logic: Check if label has specific tag
     */
    public boolean hasTag(String tag) {
        return tags != null && tags.contains(tag);
    }

    /**
     * Business logic: Check if label is in category
     */
    public boolean isInCategory(String category) {
        return this.category != null && this.category.equalsIgnoreCase(category);
    }
}
