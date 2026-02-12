package com.gogidix.rapidassist.ai.summization.infrastructure.persistence.entity;

import com.gogidix.rapidassist.ai.summization.domain.model.DocumentType;
import com.gogidix.rapidassist.ai.summization.domain.model.SummaryStyle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * MongoDB Document for SummaryHistory.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "summary_history")
public class SummaryHistoryEntity {

    @org.springframework.data.annotation.Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private String userId;

    @Indexed
    private UUID summarizationTaskId;

    @Indexed
    private UUID documentSummaryId;

    private String action;

    private String originalTextSnippet;

    private String summaryTextSnippet;

    private DocumentType documentType;

    private SummaryStyle summaryStyle;

    private String language;

    private String targetLanguage;

    private Integer originalLength;

    private Integer summaryLength;

    private Double compressionRatio;

    private Double qualityScore;

    private Long processingTimeMs;

    private String ipAddress;

    private String userAgent;

    private String metadata;

    private LocalDateTime createdAt;

    private Long version;
}
