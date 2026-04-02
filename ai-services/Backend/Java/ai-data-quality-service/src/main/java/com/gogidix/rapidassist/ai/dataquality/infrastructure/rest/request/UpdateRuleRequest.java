package com.gogidix.rapidassist.ai.dataquality.infrastructure.rest.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * REST request to update a data quality rule
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRuleRequest {

    private String name;
    private String description;
    private String thresholdValue;
    private Map<String, Object> parameters;
    private String updatedBy;
}
