package com.gogidix.rapidassist.ai.dataquality.application.query;

import com.gogidix.rapidassist.ai.dataquality.domain.model.DataQualityIssue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Query to list data quality issues with filters
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListDataQualityIssuesQuery {

    private String tenantId;
    private DataQualityIssue.IssueStatus status;
    private DataQualityIssue.IssueSeverity severity;
    private String entityType;
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDirection;
}
