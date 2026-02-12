package com.gogidix.rapidassist.ai.imagerecognition.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Domain model representing a brand/logo detection in an image.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BrandDetection {

    private UUID id;
    private String tenantId;
    private UUID imageRecognitionId;
    private String brandName;
    private String logoVariant;
    private Double confidence;
    private BoundingBox boundingBox;
    private java.util.Map<String, Object> attributes;

    /**
     * Business logic: Check if confidence meets threshold
     */
    public boolean meetsConfidenceThreshold(double threshold) {
        return confidence != null && confidence >= threshold;
    }

    /**
     * Business logic: Check if brand matches
     */
    public boolean isBrand(String brandName) {
        return this.brandName != null && this.brandName.equalsIgnoreCase(brandName);
    }
}
