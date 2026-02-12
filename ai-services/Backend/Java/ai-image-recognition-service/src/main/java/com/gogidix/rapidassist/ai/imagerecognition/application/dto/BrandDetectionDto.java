package com.gogidix.rapidassist.ai.imagerecognition.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO for BrandDetection.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BrandDetectionDto {

    private UUID id;
    private String tenantId;
    private UUID imageRecognitionId;
    private String brandName;
    private String logoVariant;
    private Double confidence;
    private BoundingBoxDto boundingBox;
    private java.util.Map<String, Object> attributes;
}
