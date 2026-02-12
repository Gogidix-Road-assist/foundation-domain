package com.gogidix.rapidassist.ai.recommendation.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for UserPreference.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferenceDto {

    private UUID id;
    private String tenantId;
    private String userId;
    private String itemType;
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
