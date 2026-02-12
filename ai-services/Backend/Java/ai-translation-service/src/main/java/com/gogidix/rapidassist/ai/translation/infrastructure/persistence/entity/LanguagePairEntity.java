package com.gogidix.rapidassist.ai.translation.infrastructure.persistence.entity;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * MongoDB Document for LanguagePair.
 * Maps to language_pair collection with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "language_pair")
public class LanguagePairEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private String sourceLanguage;

    @Indexed
    private String targetLanguage;

    @Indexed(unique = true)
    private String languagePairCode;

    @Indexed
    private Boolean supported;

    @Indexed
    private Double qualityScore;

    @Indexed
    private Long translationCount;

    @Indexed
    private Boolean requiresSpecializedModel;

    private String sourceLanguageName;

    private String targetLanguageName;

    private Long averageProcessingTimeMs;

    private String modelVersion;

    private String configuration;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Long version;
}
