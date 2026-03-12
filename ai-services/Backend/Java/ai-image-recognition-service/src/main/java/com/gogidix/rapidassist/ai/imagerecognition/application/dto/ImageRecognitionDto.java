package com.gogidix.rapidassist.ai.imagerecognition.application.dto;

import com.gogidix.rapidassist.ai.imagerecognition.domain.model.RecognitionStatus;
import com.gogidix.rapidassist.ai.imagerecognition.domain.model.RecognitionType;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for ImageRecognition.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageRecognitionDto {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private String userId;
    private String requestId;
    private String imageUrl;
    private String imageStoragePath;
    private RecognitionStatus status;
    private RecognitionType recognitionType;
    private String aiModelUsed;
    private Double processingTimeMs;
    private String errorMessage;
    private java.util.Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
    private String createdBy;
    private String updatedBy;

    private List<RecognizedObjectDto> recognizedObjects;
    private List<SceneLabelDto> sceneLabels;
    private List<BrandDetectionDto> brandDetections;
    private List<ImageFeatureDto> imageFeatures;
}
