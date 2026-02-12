package com.gogidix.rapidassist.ai.dataquality.application.dto;

import com.gogidix.rapidassist.ai.dataquality.domain.model.DataQualityIssue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for DataQualityIssue
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataQualityIssueDto {

    private UUID id;
    private String tenantId;
    private UUID checkId;
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
