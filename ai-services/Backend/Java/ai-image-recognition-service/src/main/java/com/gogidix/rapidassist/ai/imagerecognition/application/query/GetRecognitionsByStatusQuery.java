package com.gogidix.rapidassist.ai.imagerecognition.application.query;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Query to get image recognitions by status.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetRecognitionsByStatusQuery {

    @NotBlank(message = "Tenant ID is required")
    private String tenantId;

    @NotNull(message = "Status is required")
    private String status;
}
