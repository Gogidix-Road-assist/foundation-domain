package com.gogidix.rapidassist.ai.nlp.processing.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain model representing a text processing request.
 * Pure domain model without persistence annotations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TextProcessing {

    private UUID id;
    private String tenantId;
    private String text;
    private ProcessingType processingType;
    private String language;
    private ProcessingStatus status;
    private java.util.Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    public enum ProcessingType {
        TOKENIZATION,
        POS_TAGGING,
        NER,
        SUMMARIZATION,
        LANGUAGE_DETECTION,
        SIMILARITY_ANALYSIS,
        BATCH_PROCESSING
    }

    public enum ProcessingStatus {
        PENDING,
        PROCESSING,
        COMPLETED,
        FAILED
    }
}
