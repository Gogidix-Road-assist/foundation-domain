package com.gogidix.rapidassist.ai.analytics.interfaces.rest.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * REST request to update a dashboard
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDashboardRequest {

    private String name;

    private String description;

    private String layout;

    private String theme;

    private Boolean isPublic;

    private List<String> chartIds;

    private Map<String, Object> filters;

    private String refreshInterval;

    private Boolean autoRefresh;

    private List<String> tags;

    private Boolean isActive;

    private Integer displayOrder;

    private String category;
}
