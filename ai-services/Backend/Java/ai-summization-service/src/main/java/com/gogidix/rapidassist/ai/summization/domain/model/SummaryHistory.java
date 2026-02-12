package com.gogidix.rapidassist.ai.summization.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain model representing a summary history entry.
 * Tracks past summarization operations for audit and reference.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SummaryHistory {

    private UUID id;
    private String tenantId;
    private String userId;
    private UUID summarizationTaskId;
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
    private java.util.Map<String, Object> metadata;
    private LocalDateTime createdAt;

    /**
     * Business logic: Create history entry for summarization
     */
    public static SummaryHistory forSummarization(String tenantId, String userId, SummarizationTask task, DocumentSummary summary) {
        return SummaryHistory.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .userId(userId)
                .summarizationTaskId(task.getId())
                .documentSummaryId(summary.getId())
                .action("SUMMARIZE")
                .originalTextSnippet(truncate(task.getInputText(), 200))
                .summaryTextSnippet(truncate(summary.getSummaryText(), 200))
                .documentType(task.getDocumentType())
                .summaryStyle(summary.getSummaryStyle())
                .language(task.getSourceLanguage())
                .targetLanguage(task.getTargetLanguage())
                .originalLength(summary.getOriginalLength())
                .summaryLength(summary.getSummaryLength())
                .compressionRatio(summary.getCompressionRatio())
                .processingTimeMs(task.getProcessingTimeMs())
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * Business logic: Create history entry for re-summarization
     */
    public static SummaryHistory forReSummarization(String tenantId, String userId, UUID originalSummaryId, SummaryStyle newStyle) {
        return SummaryHistory.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .userId(userId)
                .documentSummaryId(originalSummaryId)
                .action("RE-SUMMARIZE")
                .summaryStyle(newStyle)
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * Business logic: Create history entry for translation
     */
    public static SummaryHistory forTranslation(String tenantId, String userId, UUID summaryId, String targetLanguage) {
        return SummaryHistory.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .userId(userId)
                .documentSummaryId(summaryId)
                .action("TRANSLATE")
                .targetLanguage(targetLanguage)
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * Business logic: Truncate text
     */
    private static String truncate(String text, int maxLength) {
        if (text == null) {
            return null;
        }
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength) + "...";
    }

    /**
     * Business logic: Check if is summarization action
     */
    public boolean isSummarization() {
        return "SUMMARIZE".equals(this.action);
    }

    /**
     * Business logic: Check if is re-summarization action
     */
    public boolean isReSummarization() {
        return "RE-SUMMARIZE".equals(this.action);
    }

    /**
     * Business logic: Check if is translation action
     */
    public boolean isTranslation() {
        return "TRANSLATE".equals(this.action);
    }

    /**
     * Business logic: Check if has quality info
     */
    public boolean hasQualityInfo() {
        return qualityScore != null;
    }

    /**
     * Business logic: Check if completed successfully
     */
    public boolean isCompleted() {
        return "SUMMARIZE".equals(this.action) && summaryTextSnippet != null && !summaryTextSnippet.trim().isEmpty();
    }

    /**
     * Business logic: Check if is high quality
     */
    public boolean isHighQuality(double threshold) {
        return qualityScore != null && qualityScore >= threshold;
    }

    /**
     * Business logic: Get efficiency rating
     */
    public String getEfficiencyRating() {
        if (compressionRatio == null) {
            return "UNKNOWN";
        }
        if (compressionRatio <= 0.2) {
            return "EXCELLENT";
        } else if (compressionRatio <= 0.4) {
            return "GOOD";
        } else if (compressionRatio <= 0.6) {
            return "ACCEPTABLE";
        } else {
            return "POOR";
        }
    }
}
