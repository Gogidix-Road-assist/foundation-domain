package com.gogidix.rapidassist.ai.computervision.application.dto;

import com.gogidix.rapidassist.ai.computervision.domain.model.FaceEmotion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * DTO for FaceDetection
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FaceDetectionDto {

    private UUID id;
    private String tenantId;
    private UUID imageAnalysisId;
    private FaceEmotion emotion;
    private Double emotionConfidence;
    private Double confidenceScore;
    private String confidenceLevel;
    private Integer age;
    private String ageGroup;
    private String gender;
    private ObjectDetectionDto.BoundingBoxDto boundingBox;
    private List<FacialLandmarkDto> landmarks;
    private Boolean hasGlasses;
    private Boolean hasBeard;
    private Boolean hasMustache;
    private Double smileConfidence;
    private Boolean smiling;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FacialLandmarkDto {
        private String type;
        private PointDto position;
        private Double confidence;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PointDto {
        private Double x;
        private Double y;
    }
}
