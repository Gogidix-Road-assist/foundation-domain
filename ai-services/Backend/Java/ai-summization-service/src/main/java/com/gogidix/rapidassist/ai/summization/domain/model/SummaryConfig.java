package com.gogidix.rapidassist.ai.summization.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain model representing a summary configuration.
 * Contains settings for how summaries should be generated.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SummaryConfig {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private String configName;
    private String description;
    private SummaryStyle defaultStyle;
    private Integer maxLength;
    private Integer minLength;
    private Boolean extractKeyPoints;
    private Boolean includeStatistics;
    private String targetLanguage;
    private String defaultLanguage;
    private Double qualityThreshold;
    private Boolean enableMultiLanguage;
    private java.util.Map<String, Object> additionalSettings;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private Boolean isActive;

    /**
     * Business logic: Check if configuration is active
     */
    public boolean isActiveConfig() {
        return Boolean.TRUE.equals(this.isActive);
    }

    /**
     * Business logic: Check if multi-language support is enabled
     */
    public boolean isMultiLanguageEnabled() {
        return Boolean.TRUE.equals(this.enableMultiLanguage);
    }

    /**
     * Business logic: Check if key points extraction is enabled
     */
    public boolean shouldExtractKeyPoints() {
        return Boolean.TRUE.equals(this.extractKeyPoints);
    }

    /**
     * Business logic: Check if statistics should be included
     */
    public boolean shouldIncludeStatistics() {
        return Boolean.TRUE.equals(this.includeStatistics);
    }

    /**
     * Business logic: Get effective max length
     */
    public int getEffectiveMaxLength() {
        return maxLength != null ? maxLength : 500;
    }

    /**
     * Business logic: Get effective min length
     */
    public int getEffectiveMinLength() {
        return minLength != null ? minLength : 50;
    }

    /**
     * Business logic: Validate configuration
     */
    public boolean isValid() {
        return configName != null && !configName.trim().isEmpty()
                && defaultStyle != null
                && getEffectiveMinLength() <= getEffectiveMaxLength();
    }
}
