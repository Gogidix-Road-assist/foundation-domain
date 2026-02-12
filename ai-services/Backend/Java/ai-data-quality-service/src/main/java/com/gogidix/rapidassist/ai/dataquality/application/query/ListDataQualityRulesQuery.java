package com.gogidix.rapidassist.ai.dataquality.application.query;

import com.gogidix.rapidassist.ai.dataquality.domain.model.DataQualityRule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Query to list data quality rules with filters
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListDataQualityRulesQuery {

    private String tenantId;
    private DataQualityRule.RuleType ruleType;
    private String entityType;
    private DataQualityRule.RuleSeverity severity;
    private Boolean active;
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDirection;
}
