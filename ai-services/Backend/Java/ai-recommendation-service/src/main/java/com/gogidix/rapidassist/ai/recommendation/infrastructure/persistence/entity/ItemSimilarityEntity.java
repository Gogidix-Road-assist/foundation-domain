package com.gogidix.rapidassist.ai.recommendation.infrastructure.persistence.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * MongoDB Document for ItemSimilarity.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "item_similarity")
public class ItemSimilarityEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private String itemType;

    @Indexed
    private String item1Id;

    @Indexed
    private String item2Id;

    private Double similarityScore;
    private String similarityAlgorithm;
    private Integer coOccurrenceCount;
    private LocalDateTime lastCalculatedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String metadata;
}
