package com.gogidix.rapidassist.ai.matching.infrastructure.persistence.entity;

import com.gogidix.rapidassist.ai.matching.domain.model.MatchingAlgorithmType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * MongoDB Document for SimilarityScore.
 * Maps to similarity_score collection with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "similarity_score")
public class SimilarityScoreEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private String scoreId;

    @Indexed
    private String sourceEntityType;

    @Indexed
    private String sourceEntityId;

    @Indexed
    private String targetEntityType;

    @Indexed
    private String targetEntityId;

    @Indexed
    private MatchingAlgorithmType algorithmType;

    private double overallSimilarity;

    private Map<String, Double> fieldSimilarities;

    private Map<String, Object> similarityDetails;

    private long computationTimeMs;

    private Map<String, Object> metadata;

    private LocalDateTime createdAt;

    private String createdBy;
}
