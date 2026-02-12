package com.gogidix.rapidassist.ai.recommendation.interfaces.rest.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * REST request to update user preference.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePreferenceRequest {

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "Item type is required")
    private String itemType;

    @NotBlank(message = "Item ID is required")
    private String itemId;

    private String preferenceKey;
    private String preferenceValue;

    @NotNull(message = "Score delta is required")
    private Double scoreDelta;

    private String metadata;
}
