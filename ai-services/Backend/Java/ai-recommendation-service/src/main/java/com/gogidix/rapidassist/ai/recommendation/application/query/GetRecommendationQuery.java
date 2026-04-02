package com.gogidix.rapidassist.ai.recommendation.application.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Query to get recommendation result.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetRecommendationQuery {

    private String tenantId;
    private UUID resultId;
    private Boolean useCache;
}
