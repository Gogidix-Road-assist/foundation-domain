package com.gogidix.rapidassist.ai.imagerecognition.infrastructure.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.UUID;

/**
 * MongoDB Document for BrandDetection.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "brand_detection")
public class BrandDetectionEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private UUID imageRecognitionId;

    private String brandName;

    private String logoVariant;

    private Double confidence;

    private String boundingBox;

    private String attributes;
}
