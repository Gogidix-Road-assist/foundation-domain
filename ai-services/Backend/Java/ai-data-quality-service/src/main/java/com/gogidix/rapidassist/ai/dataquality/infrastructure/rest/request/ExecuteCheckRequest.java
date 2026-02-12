package com.gogidix.rapidassist.ai.dataquality.infrastructure.rest.request;

import com.gogidix.rapidassist.ai.dataquality.domain.model.DataQualityRule;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

/**
 * REST request to execute a data quality check
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteCheckRequest {

    @NotNull(message = "Rule ID is required")
    private UUID ruleId;

    @NotBlank(message = "Check name is required")
    private String checkName;

    @NotBlank(message = "Entity type is required")
    private String entityType;

    private String datasetIdentifier;
    private Map<String, Object> checkParameters;
    private String executedBy;
}
