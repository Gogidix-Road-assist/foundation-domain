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
 * DTO for MatchingResult.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchingResultDto {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private String matchId;
    private String sourceEntityType;
    private String sourceEntityId;
    private String targetEntityType;
    private String targetEntityId;
    private com.gogidix.rapidassist.ai.matching.domain.model.MatchingAlgorithmType algorithmType;
    private Double similarityScore;
    private Double confidenceScore;
    private com.gogidix.rapidassist.ai.matching.domain.model.MatchingStatus status;
    private Map<String, Object> matchingAttributes;
    private Map<String, Object> metadata;
    private String version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
