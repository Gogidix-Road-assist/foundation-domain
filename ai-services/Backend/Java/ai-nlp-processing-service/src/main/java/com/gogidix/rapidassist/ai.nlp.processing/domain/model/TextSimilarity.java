package com.gogidix.rapidassist.ai.nlp.processing.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain model representing text similarity analysis results.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TextSimilarity {

    private UUID id;
    private String tenantId;
    private UUID textProcessingId;
    private String text1;
    private String text2;
    private double similarityScore;
    private SimilarityMethod method;
    private java.util.Map<String, Double> detailedScores;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum SimilarityMethod {
        COSINE,
        JACCARD,
        LEVENSHTEIN,
        EUCLIDEAN,
        SEMANTIC
    }
}
