package com.gogidix.rapidassist.ai.dataquality.application.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Query to get a data quality rule by ID
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetDataQualityRuleQuery {

    private String tenantId;
    private UUID ruleId;
}
