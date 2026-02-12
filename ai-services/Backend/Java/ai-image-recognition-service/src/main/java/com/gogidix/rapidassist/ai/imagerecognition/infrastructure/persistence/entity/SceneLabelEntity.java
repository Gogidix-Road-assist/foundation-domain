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
 * MongoDB Document for SceneLabel.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "scene_label")
public class SceneLabelEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private UUID imageRecognitionId;

    private String label;

    private String category;

    private Double confidence;

    private String tags;

    private String description;
}
