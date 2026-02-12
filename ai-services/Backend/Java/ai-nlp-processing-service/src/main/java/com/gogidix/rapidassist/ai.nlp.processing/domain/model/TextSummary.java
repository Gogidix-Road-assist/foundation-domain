package com.gogidix.rapidassist.ai.nlp.processing.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain model representing text summarization results.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TextSummary {

    private UUID id;
    private String tenantId;
    private UUID textProcessingId;
    private String originalText;
    private String summary;
    private double compressionRatio;
    private int originalLength;
    private int summaryLength;
    private String summarizationMethod;
    private double relevanceScore;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum SummarizationMethod {
        EXTRACTIVE,
        ABSTRACTIVE,
        HYBRID
    }
}
