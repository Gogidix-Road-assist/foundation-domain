package com.gogidix.rapidassist.ai.nlp.processing.application.dto;

import com.gogidix.rapidassist.ai.nlp.processing.domain.model.TextSimilarity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TextSimilarityDto {
    private UUID id;
    private String tenantId;
    private UUID textProcessingId;
    private String text1;
    private String text2;
    private double similarityScore;
    private TextSimilarity.SimilarityMethod method;
    private Map<String, Double> detailedScores;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
