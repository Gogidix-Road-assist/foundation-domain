package com.gogidix.rapidassist.ai.translation.application.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for TranslationQuality.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TranslationQualityDto {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private UUID translationRequestId;
    private Double score;
    private String confidence;
    private Integer errorCount;
    private Integer warningCount;
    private Double fluencyScore;
    private Double accuracyScore;
    private Double consistencyScore;
    private Object metrics;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
