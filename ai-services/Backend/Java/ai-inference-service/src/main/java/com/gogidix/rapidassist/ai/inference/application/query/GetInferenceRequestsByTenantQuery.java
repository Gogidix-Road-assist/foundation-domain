package com.gogidix.rapidassist.ai.inference.application.query;

import com.gogidix.rapidassist.ai.inference.domain.model.InferenceStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Query to get inference requests by tenant
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetInferenceRequestsByTenantQuery {

    @NotBlank(message = "Tenant ID is required")
    private String tenantId;

    private InferenceStatus status;

    private Integer page;

    private Integer size;
}
