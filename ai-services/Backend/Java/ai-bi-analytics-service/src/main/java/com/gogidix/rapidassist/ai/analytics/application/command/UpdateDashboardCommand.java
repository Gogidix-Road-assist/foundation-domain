package com.gogidix.rapidassist.ai.analytics.application.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Command to update a dashboard
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDashboardCommand {

    private String dashboardId;
    private String tenantId;
    private String name;
    private String description;
    private String layout;
    private String theme;
    private Boolean isPublic;
    private String updatedBy;
    private List<String> chartIds;
    private Map<String, Object> filters;
    private String refreshInterval;
    private Boolean autoRefresh;
    private List<String> tags;
    private Boolean isActive;
    private Integer displayOrder;
    private String category;
}
