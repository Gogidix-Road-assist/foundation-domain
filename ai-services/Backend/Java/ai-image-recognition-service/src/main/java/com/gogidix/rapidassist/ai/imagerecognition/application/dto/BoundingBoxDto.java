package com.gogidix.rapidassist.ai.imagerecognition.application.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for BoundingBox.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoundingBoxDto {

    private Double x;
    private Double y;
    private Double width;
    private Double height;
}
