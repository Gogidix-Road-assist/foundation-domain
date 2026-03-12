package com.gogidix.rapidassist.ai.matching.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Domain model representing a Similarity Score.
 * Stores detailed similarity metrics between entities.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimilarityScore {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private String scoreId;
    private String sourceEntityType;
    private String sourceEntityId;
    private String targetEntityType;
    private String targetEntityId;
    private MatchingAlgorithmType algorithmType;
    private double overallSimilarity;
    private Map<String, Double> fieldSimilarities;
    private Map<String, Object> similarityDetails;
    private long computationTimeMs;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private String createdBy;
}
