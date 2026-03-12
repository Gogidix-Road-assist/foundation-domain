package com.gogidix.rapidassist.ai.recommendation.application.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for ItemSimilarity.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemSimilarityDto {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private String itemType;
    private String item1Id;
    private String item2Id;
    private Double similarityScore;
    private String similarityAlgorithm;
    private Integer coOccurrenceCount;
    private LocalDateTime lastCalculatedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String metadata;
}
