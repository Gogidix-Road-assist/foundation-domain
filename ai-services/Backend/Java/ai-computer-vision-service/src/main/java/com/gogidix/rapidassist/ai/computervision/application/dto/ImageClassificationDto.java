package com.gogidix.rapidassist.ai.computervision.application.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * DTO for ImageClassification
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageClassificationDto {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private UUID imageAnalysisId;
    private String primaryClass;
    private Double primaryConfidence;
    private String primaryConfidenceLevel;
    private List<ClassPredictionDto> predictions;
    private String modelName;
    private String modelVersion;

    @Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClassPredictionDto {
        private String className;
        private Double confidence;
        private String confidenceLevel;
        private Integer rank;
    }
}
