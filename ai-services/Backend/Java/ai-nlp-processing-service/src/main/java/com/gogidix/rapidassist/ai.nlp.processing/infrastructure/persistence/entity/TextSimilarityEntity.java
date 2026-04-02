package com.gogidix.rapidassist.ai.nlp.processing.infrastructure.persistence.entity;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * MongoDB Document for TextSimilarity.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "text_similarity")
public class TextSimilarityEntity {
    @Id
    private String id;
    @Indexed(unique = true)
    private UUID uuid;
    @Indexed
    private String tenantId;
    @Indexed
    private UUID textProcessingId;
    private String text1;
    private String text2;
    private double similarityScore;
    private String similarityMethod;
    private Map<String, Double> detailedScores;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
