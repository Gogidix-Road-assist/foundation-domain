package com.gogidix.rapidassist.ai.imagerecognition.application.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * DTO for ImageFeature.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageFeatureDto {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private UUID imageRecognitionId;
    private String featureType;
    private String featureName;
    private Double featureValue;
    private List<Double> featureVector;
    private java.util.Map<String, Object> metadata;
}
