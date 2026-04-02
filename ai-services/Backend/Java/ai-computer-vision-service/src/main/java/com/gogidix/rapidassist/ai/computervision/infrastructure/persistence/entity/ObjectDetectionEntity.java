package com.gogidix.rapidassist.ai.computervision.infrastructure.persistence.entity;

import com.gogidix.rapidassist.ai.computervision.domain.model.DetectionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

/**
 * MongoDB Document for ObjectDetection
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "object_detection")
public class ObjectDetectionEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private UUID imageAnalysisId;

    private DetectionType type;

    private String label;

    private Double confidenceScore;

    private String boundingBox;

    private String color;

    private String description;

    private Integer objectCount;
}
