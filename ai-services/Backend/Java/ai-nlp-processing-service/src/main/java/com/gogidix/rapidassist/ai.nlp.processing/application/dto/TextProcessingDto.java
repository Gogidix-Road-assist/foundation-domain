package com.gogidix.rapidassist.ai.nlp.processing.application.dto;

import com.gogidix.rapidassist.ai.nlp.processing.domain.model.TextProcessing;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.Map;

/**
 * DTO for TextProcessing.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TextProcessingDto {
    private UUID id;
    private String tenantId;
    private String text;
    private TextProcessing.ProcessingType processingType;
    private String language;
    private TextProcessing.ProcessingStatus status;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
