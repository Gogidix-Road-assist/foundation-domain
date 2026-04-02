package com.gogidix.rapidassist.ai.summization.infrastructure.persistence.entity;

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
 * MongoDB Document for SummaryConfig.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "summary_config")
public class SummaryConfigEntity {

    @org.springframework.data.annotation.Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
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

    private String additionalSettings;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String createdBy;

    private String updatedBy;

    private Boolean isActive;

    private Long version;
}
