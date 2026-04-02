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
 * MongoDB Document for TranslationMemory.
 * Maps to translation_memory collection with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "translation_memory")
public class TranslationMemoryEntity {

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
    private String sourceHash;

    @Indexed
    private Integer usageCount;

    @Indexed
    private Double qualityScore;

    @Indexed
    private Boolean verified;

    @Indexed
    private String domain;

    @Indexed
    private LocalDateTime lastUsedAt;

    private String sourceSegment;

    private String targetSegment;

    private String context;

    private String verifiedBy;

    private LocalDateTime verifiedAt;

    private String metadata;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Long version;
}
