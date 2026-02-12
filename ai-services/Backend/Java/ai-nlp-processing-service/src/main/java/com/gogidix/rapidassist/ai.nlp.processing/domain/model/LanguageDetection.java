package com.gogidix.rapidassist.ai.nlp.processing.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain model representing language detection results.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LanguageDetection {

    private UUID id;
    private String tenantId;
    private UUID textProcessingId;
    private String text;
    private String detectedLanguage;
    private String languageCode;
    private double confidence;
    private java.util.Map<String, Double> alternativeLanguages;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
