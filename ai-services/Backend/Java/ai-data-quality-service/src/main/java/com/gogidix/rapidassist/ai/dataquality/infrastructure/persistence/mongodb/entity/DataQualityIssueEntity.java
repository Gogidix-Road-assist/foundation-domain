package com.gogidix.rapidassist.ai.dataquality.infrastructure.persistence.mongodb.entity;

import com.gogidix.rapidassist.ai.dataquality.domain.model.DataQualityIssue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * MongoDB entity for DataQualityIssue
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "data_quality_issues")
public class DataQualityIssueEntity {

    @Id
    private UUID id;

    @Indexed
    private String tenantId;

    @Indexed
    private UUID checkId;

    @Indexed
    private UUID ruleId;

    private String issueType;
    private DataQualityIssue.IssueSeverity severity;
    private String entityType;
    private String entityId;
    private String attributeName;
    private String currentValue;
    private String expectedValue;
    private String description;
    private DataQualityIssue.IssueStatus status;
    private LocalDateTime detectedAt;
    private LocalDateTime resolvedAt;
    private String resolvedBy;
    private String resolutionNotes;
    private Map<String, Object> issueDetails;
    private int occurrenceCount;
    private String affectedBusinessKey;
}
