package com.gogidix.rapidassist.ai.computervision.infrastructure.persistence.entity;

import com.gogidix.rapidassist.ai.computervision.domain.model.FaceEmotion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

/**
 * MongoDB Document for FaceDetection
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "face_detection")
public class FaceDetectionEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private UUID imageAnalysisId;

    private FaceEmotion emotion;

    private Double emotionConfidence;

    private Double confidenceScore;

    private Integer age;

    private String gender;

    private String boundingBox;

    private String landmarks;

    private Boolean hasGlasses;

    private Boolean hasBeard;

    private Boolean hasMustache;

    private Double smileConfidence;
}
