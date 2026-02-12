package com.gogidix.rapidassist.ai.nlp.processing.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * DTO for NER results.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NERResultDto {
    private UUID id;
    private UUID textProcessingId;
    private List<EntityDto> entities;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class EntityDto {
    private String text;
    private String entityType;
    private double confidence;
    private int startPosition;
    private int endPosition;
}
