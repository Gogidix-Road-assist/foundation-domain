package com.gogidix.rapidassist.ai.imagerecognition.application.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO for RecognizedObject.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecognizedObjectDto {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private UUID imageRecognitionId;
    private String label;
    private String objectType;
    private Double confidence;
    private BoundingBoxDto boundingBox;
    private java.util.Map<String, Object> attributes;
}
