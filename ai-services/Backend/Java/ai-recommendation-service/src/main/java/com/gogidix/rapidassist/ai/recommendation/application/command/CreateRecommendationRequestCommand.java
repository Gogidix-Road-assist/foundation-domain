package com.gogidix.rapidassist.ai.recommendation.application.command;

import com.gogidix.rapidassist.ai.recommendation.domain.model.RecommendationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Command to create a recommendation request.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRecommendationRequestCommand {

    private String tenantId;
    private String userId;
    private RecommendationType recommendationType;
    private String itemType;
    private String contextType;
    private String contextId;
    private Integer limit;
    private Map<String, Object> filters;
    private Map<String, Object> parameters;
    private Integer ttlMinutes;
}
