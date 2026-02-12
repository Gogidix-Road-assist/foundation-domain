package com.gogidix.rapidassist.ai.nlp.processing.infrastructure.persistence.entity;

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
 * MongoDB Document for TextSummary.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "text_summary")
public class TextSummaryEntity {
    @Id
    private String id;
    @Indexed(unique = true)
    private UUID uuid;
    @Indexed
    private String tenantId;
    @Indexed
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
}
