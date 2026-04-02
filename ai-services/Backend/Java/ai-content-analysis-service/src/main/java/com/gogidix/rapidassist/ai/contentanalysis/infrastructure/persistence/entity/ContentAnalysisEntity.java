package com.gogidix.rapidassist.ai.contentanalysis.infrastructure.persistence.entity;

import com.gogidix.rapidassist.ai.contentanalysis.domain.model.ContentAnalysis;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * MongoDB entity for ContentAnalysis
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "content_analyses")
@CompoundIndex(name = "tenant_content_idx", def = "{'tenantId': 1, 'contentId': 1}")
@CompoundIndex(name = "tenant_status_idx", def = "{'tenantId': 1, 'status': 1}")
public class ContentAnalysisEntity {

    @Id
    private UUID id;

    @Indexed
    private String tenantId;

    @Indexed
    private String contentId;

    @Indexed
    private String status;

    @Indexed
    private String contentType;

    private String contentTitle;
    private String contentBody;
    private String contentLanguage;

    private ContentMetricsEntity metrics;
    private SentimentAnalysisEntity sentiment;
    private SEOAnalysisEntity seoAnalysis;
    private ReadabilityAnalysisEntity readability;

    private Integer wordCount;
    private Integer characterCount;
    private Integer sentenceCount;
    private Integer paragraphCount;

    private LocalDateTime analyzedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String createdBy;
    private String updatedBy;
    private String analysisVersion;
    private Double overallScore;
}
