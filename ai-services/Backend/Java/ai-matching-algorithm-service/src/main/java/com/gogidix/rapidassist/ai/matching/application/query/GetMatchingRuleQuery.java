package com.gogidix.rapidassist.ai.matching.application.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

/**
 * Query to get a matching rule.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetMatchingRuleQuery {

    @NotBlank(message = "Tenant ID is required")
    private String tenantId;

    private java.util.UUID ruleId;

    private String ruleCode;
}
