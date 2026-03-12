package com.gogidix.rapidassist.ai.matching.application.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for SimilarityScore.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimilarityScoreDto {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private String entity1Id;
    private String entity1Type;
    private String entity2Id;
    private String entity2Type;
    private Double score;
    private String algorithm;
    private Map<String, Object> scoreDetails;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
