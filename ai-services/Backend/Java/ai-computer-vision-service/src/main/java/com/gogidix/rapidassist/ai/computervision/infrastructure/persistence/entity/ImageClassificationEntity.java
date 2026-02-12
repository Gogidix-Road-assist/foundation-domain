package com.gogidix.rapidassist.ai.computervision.infrastructure.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

/**
 * MongoDB Document for ImageClassification
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "image_classification")
public class ImageClassificationEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private UUID imageAnalysisId;

    private String primaryClass;

    private Double primaryConfidence;

    private String predictions;

    private String modelName;

    private String modelVersion;
}
