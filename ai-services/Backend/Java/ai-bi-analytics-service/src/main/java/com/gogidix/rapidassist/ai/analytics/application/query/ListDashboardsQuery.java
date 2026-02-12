package com.gogidix.rapidassist.ai.analytics.application.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Query to list dashboards with filters
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListDashboardsQuery {

    private String tenantId;
    private Boolean isPublic;
    private Boolean isActive;
    private String category;
    private Integer page;
    private Integer size;
}
