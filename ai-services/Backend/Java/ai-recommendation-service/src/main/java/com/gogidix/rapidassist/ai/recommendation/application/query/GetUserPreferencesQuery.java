package com.gogidix.rapidassist.ai.recommendation.application.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Query to get user preferences.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetUserPreferencesQuery {

    private String tenantId;
    private String userId;
    private String itemType;
    private Integer page;
    private Integer size;
}
