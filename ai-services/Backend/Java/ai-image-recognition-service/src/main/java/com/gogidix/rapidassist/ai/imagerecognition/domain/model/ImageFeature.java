package com.gogidix.rapidassist.ai.imagerecognition.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Domain model representing extracted visual features from an image.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageFeature {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private UUID imageRecognitionId;
    private String featureType;
    private String featureName;
    private Double featureValue;
    private java.util.List<Double> featureVector;
    private java.util.Map<String, Object> metadata;

    /**
     * Business logic: Check if feature is of specific type
     */
    public boolean isOfType(String type) {
        return featureType != null && featureType.equalsIgnoreCase(type);
    }

    /**
     * Business logic: Get feature vector size
     */
    public int getFeatureVectorSize() {
        return featureVector != null ? featureVector.size() : 0;
    }

    /**
     * Business logic: Calculate similarity with another feature vector (cosine similarity)
     */
    public double calculateSimilarity(java.util.List<Double> otherVector) {
        if (featureVector == null || otherVector == null ||
            featureVector.size() != otherVector.size()) {
            return 0.0;
        }

        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (int i = 0; i < featureVector.size(); i++) {
            dotProduct += featureVector.get(i) * otherVector.get(i);
            norm1 += Math.pow(featureVector.get(i), 2);
            norm2 += Math.pow(otherVector.get(i), 2);
        }

        if (norm1 == 0.0 || norm2 == 0.0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
}
