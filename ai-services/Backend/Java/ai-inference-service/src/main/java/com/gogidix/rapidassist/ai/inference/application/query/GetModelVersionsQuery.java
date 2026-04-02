package com.gogidix.rapidassist.ai.inference.application.query;

import com.gogidix.rapidassist.ai.inference.domain.model.ModelVersionStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Query to get model versions
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetModelVersionsQuery {

    @NotBlank(message = "Tenant ID is required")
    private String tenantId;

    private String modelId;

    private ModelVersionStatus status;
}
