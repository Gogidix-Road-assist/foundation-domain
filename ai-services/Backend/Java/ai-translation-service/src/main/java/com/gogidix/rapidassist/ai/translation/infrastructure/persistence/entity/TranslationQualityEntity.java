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
 * MongoDB Document for TranslationQuality.
 * Maps to translation_quality collection with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "translation_quality")
public class TranslationQualityEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private UUID translationRequestId;

    @Indexed
    private Double score;

    @Indexed
    private String confidence;

    @Indexed
    private Integer errorCount;

    @Indexed
    private Integer warningCount;

    private Double fluencyScore;

    private Double accuracyScore;

    private Double consistencyScore;

    private String metrics;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Long version;
}
