package com.gogidix.rapidassist.ai.summization.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Domain model representing a document summary.
 * Contains the summarized content and metadata.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentSummary {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private UUID summarizationTaskId;
    private String originalText;
    private String summaryText;
    private String title;
    private String sourceUrl;
    private DocumentType documentType;
    private SummaryStyle summaryStyle;
    private String language;
    private String targetLanguage;
    private Integer originalLength;
    private Integer summaryLength;
    private List<String> keyPoints;
    private java.util.Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private String version;

    // Associated entities
    @Builder.Default
    private List<SummaryMetrics> metrics = new ArrayList<>();

    /**
     * Business logic: Calculate compression ratio
     */
    public double getCompressionRatio() {
        if (originalLength == null || originalLength == 0 || summaryLength == null) {
            return 0.0;
        }
        return (double) summaryLength / originalLength;
    }

    /**
     * Business logic: Check if summary is empty
     */
    public boolean isSummaryEmpty() {
        return summaryText == null || summaryText.trim().isEmpty();
    }

    /**
     * Business logic: Check if has key points
     */
    public boolean hasKeyPoints() {
        return keyPoints != null && !keyPoints.isEmpty();
    }

    /**
     * Business logic: Get key points count
     */
    public int getKeyPointsCount() {
        return keyPoints != null ? keyPoints.size() : 0;
    }

    /**
     * Business logic: Check if multi-language
     */
    public boolean isMultiLanguage() {
        return targetLanguage != null && !targetLanguage.equals(language);
    }

    /**
     * Business logic: Validate summary
     */
    public boolean isValid() {
        return originalText != null && !originalText.trim().isEmpty()
                && summaryText != null && !summaryText.trim().isEmpty()
                && summaryStyle != null;
    }

    /**
     * Business logic: Check if is translation
     */
    public boolean isTranslation() {
        return isMultiLanguage();
    }

    /**
     * Business logic: Get original text length
     */
    public int getOriginalTextLength() {
        return originalText != null ? originalText.length() : 0;
    }

    /**
     * Business logic: Get summary text length
     */
    public int getSummaryTextLength() {
        return summaryText != null ? summaryText.length() : 0;
    }

    /**
     * Business logic: Add key point
     */
    public void addKeyPoint(String keyPoint) {
        if (this.keyPoints == null) {
            this.keyPoints = new ArrayList<>();
        }
        this.keyPoints.add(keyPoint);
    }

    /**
     * Business logic: Update summary text
     */
    public void updateSummaryText(String newSummary) {
        this.summaryText = newSummary;
        this.summaryLength = newSummary != null ? newSummary.length() : 0;
        this.updatedAt = LocalDateTime.now();
    }
}
