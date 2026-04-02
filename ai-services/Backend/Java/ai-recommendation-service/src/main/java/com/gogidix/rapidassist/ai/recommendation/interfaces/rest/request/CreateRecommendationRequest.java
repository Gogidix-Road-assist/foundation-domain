package com.gogidix.rapidassist.ai.recommendation.interfaces.rest.request;

import com.gogidix.rapidassist.ai.recommendation.domain.model.RecommendationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * REST request to create a recommendation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRecommendationRequest {

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotNull(message = "Recommendation type is required")
    private RecommendationType recommendationType;

    @NotBlank(message = "Item type is required")
    private String itemType;

    private String contextType;
    private String contextId;

    @Builder.Default
    private Integer limit = 10;

    private Map<String, Object> filters;
    private Map<String, Object> parameters;
    private Integer ttlMinutes;
}
