package com.gogidix.rapidassist.ai.imagerecognition.application.query;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Query to get image recognitions by user ID.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetUserRecognitionsQuery {

    @NotBlank(message = "Tenant ID is required")
    private String tenantId;

    @NotBlank(message = "User ID is required")
    private String userId;
}
