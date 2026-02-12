package com.gogidix.rapidassist.ai.computervision.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * DTO for TextRecognition
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TextRecognitionDto {

    private UUID id;
    private String tenantId;
    private UUID imageAnalysisId;
    private String fullText;
    private List<TextLineDto> textLines;
    private List<TextWordDto> textWords;
    private String language;
    private Double confidenceScore;
    private String confidenceLevel;
    private Integer totalCharacters;
    private Integer totalWords;
    private Integer totalLines;
    private Double averageLineHeight;
    private String longestLine;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TextLineDto {
        private String text;
        private Double confidence;
        private ObjectDetectionDto.BoundingBoxDto boundingBox;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TextWordDto {
        private String text;
        private Double confidence;
        private ObjectDetectionDto.BoundingBoxDto boundingBox;
        private Integer lineNumber;
    }
}
