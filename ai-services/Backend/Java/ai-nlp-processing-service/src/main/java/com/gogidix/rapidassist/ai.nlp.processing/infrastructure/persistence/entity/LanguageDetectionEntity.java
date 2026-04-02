package com.gogidix.rapidassist.ai.nlp.processing.infrastructure.persistence.entity;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * MongoDB Document for LanguageDetection.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "language_detection")
public class LanguageDetectionEntity {
    @Id
    private String id;
    @Indexed(unique = true)
    private UUID uuid;
    @Indexed
    private String tenantId;
    @Indexed
    private UUID textProcessingId;
    private String text;
    @Indexed
    private String detectedLanguage;
    private String languageCode;
    private double confidence;
    private Map<String, Double> alternativeLanguages;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
