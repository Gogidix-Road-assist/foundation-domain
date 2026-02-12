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
 * MongoDB Document for TextRecognition
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "text_recognition")
public class TextRecognitionEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private UUID imageAnalysisId;

    private String fullText;

    private String textLines;

    private String textWords;

    private String language;

    private Double confidenceScore;

    private Integer totalCharacters;

    private Integer totalWords;

    private Integer totalLines;
}
