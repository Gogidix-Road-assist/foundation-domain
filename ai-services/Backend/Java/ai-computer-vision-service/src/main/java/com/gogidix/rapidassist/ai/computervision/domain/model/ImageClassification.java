package com.gogidix.rapidassist.ai.computervision.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Domain model representing image classification results
 * Pure domain model without MongoDB annotations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageClassification {

    private UUID id;
    private String tenantId;
    private UUID imageAnalysisId;
    private String primaryClass;
    private Double primaryConfidence;
    @Builder.Default
    private List<ClassPrediction> predictions = new ArrayList<>();
    private String modelName;
    private String modelVersion;

    /**
     * Business logic: Get top N predictions
     */
    public List<ClassPrediction> getTopPredictions(int n) {
        return predictions.stream()
                .sorted(Comparator.comparingDouble(ClassPrediction::getConfidence).reversed())
                .limit(n)
                .collect(Collectors.toList());
    }

    /**
     * Business logic: Get predictions with minimum confidence
     */
    public List<ClassPrediction> getPredictionsWithMinConfidence(double minConfidence) {
        return predictions.stream()
                .filter(prediction -> prediction.getConfidence() != null && prediction.getConfidence() >= minConfidence)
                .sorted(Comparator.comparingDouble(ClassPrediction::getConfidence).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Business logic: Get prediction by class name
     */
    public ClassPrediction getPredictionByClass(String className) {
        return predictions.stream()
                .filter(prediction -> className.equals(prediction.getClassName()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Business logic: Check if primary classification is confident
     */
    public boolean isPrimaryConfident(double threshold) {
        return primaryConfidence != null && primaryConfidence >= threshold;
    }

    /**
     * Business logic: Get confidence level of primary classification
     */
    public ConfidenceLevel getPrimaryConfidenceLevel() {
        if (primaryConfidence == null) {
            return ConfidenceLevel.VERY_LOW;
        }
        return ConfidenceLevel.fromScore(primaryConfidence);
    }

    /**
     * Business logic: Check if class exists in predictions
     */
    public boolean hasClass(String className) {
        return predictions.stream()
                .anyMatch(prediction -> className.equals(prediction.getClassName()));
    }

    /**
     * Business logic: Get all unique class names
     */
    public List<String> getAllClassNames() {
        return predictions.stream()
                .map(ClassPrediction::getClassName)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Business logic: Check if image contains specific content type
     */
    public boolean containsContentType(String contentType) {
        if (primaryClass != null && primaryClass.toLowerCase().contains(contentType.toLowerCase())) {
            return true;
        }
        return predictions.stream()
                .anyMatch(prediction -> prediction.getClassName() != null &&
                                       prediction.getClassName().toLowerCase().contains(contentType.toLowerCase()));
    }

    /**
     * Inner class representing a class prediction
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClassPrediction {
        private String className;
        private Double confidence;
        private Integer rank;

        /**
         * Business logic: Get confidence level
         */
        public ConfidenceLevel getConfidenceLevel() {
            if (confidence == null) {
                return ConfidenceLevel.VERY_LOW;
            }
            return ConfidenceLevel.fromScore(confidence);
        }

        /**
         * Business logic: Check if prediction is confident
         */
        public boolean isConfident(double threshold) {
            return confidence != null && confidence >= threshold;
        }
    }
}
