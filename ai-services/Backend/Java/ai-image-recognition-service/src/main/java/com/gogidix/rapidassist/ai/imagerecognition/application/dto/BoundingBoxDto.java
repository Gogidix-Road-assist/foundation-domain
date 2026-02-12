package com.gogidix.rapidassist.ai.imagerecognition.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for BoundingBox.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoundingBoxDto {

    private Double x;
    private Double y;
    private Double width;
    private Double height;
}
