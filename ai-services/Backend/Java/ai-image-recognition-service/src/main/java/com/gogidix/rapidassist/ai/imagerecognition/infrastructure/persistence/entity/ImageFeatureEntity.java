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
 * MongoDB Document for ImageFeature.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "image_feature")
public class ImageFeatureEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private UUID imageRecognitionId;

    private String featureType;

    private String featureName;

    private Double featureValue;

    private String featureVector;

    private String metadata;
}
