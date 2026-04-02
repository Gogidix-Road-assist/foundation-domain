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
 * MongoDB Document for UserPreference.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "user_preference")
public class UserPreferenceEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private String userId;

    @Indexed
    private String itemType;

    @Indexed
    private String itemId;

    private String preferenceKey;
    private String preferenceValue;
    private Double preferenceScore;
    private Integer interactionCount;
    private LocalDateTime lastInteractionAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String metadata;
}
