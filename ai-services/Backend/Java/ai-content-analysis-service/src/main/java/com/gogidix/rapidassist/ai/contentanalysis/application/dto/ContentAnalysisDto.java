package com.gogidix.rapidassist.ai.contentanalysis.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for Content Analysis responses
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentAnalysisDto {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private String contentId;
    private String status;
    private String contentType;
    private String contentTitle;
    private String contentBody;
    private String contentLanguage;

    private ContentMetricsDto metrics;
    private SentimentAnalysisDto sentiment;
    private SEOAnalysisDto seoAnalysis;
    private ReadabilityAnalysisDto readability;

    private Integer wordCount;
    private Integer characterCount;
    private Integer sentenceCount;
    private Integer paragraphCount;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime analyzedAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    private String createdBy;
    private String updatedBy;
    private String analysisVersion;
    private Double overallScore;
    private String qualityGrade;

    private List<ContentTopicDto> topics;
}
