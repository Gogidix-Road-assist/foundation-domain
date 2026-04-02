package com.gogidix.rapidassist.ai.computervision.application.dto;

import com.gogidix.rapidassist.ai.computervision.domain.model.DetectionType;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO for ObjectDetection
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ObjectDetectionDto {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private UUID imageAnalysisId;
    private DetectionType type;
    private String label;
    private Double confidenceScore;
    private String confidenceLevel;
    private BoundingBoxDto boundingBox;
    private String color;
    private String description;
    private Integer objectCount;
    private Double area;

    @Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BoundingBoxDto {
        private Double x;
        private Double y;
        private Double width;
        private Double height;
    }
}
