package com.gogidix.rapidassist.ai.analytics.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Domain model representing a chart configuration
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChartConfiguration {

    private UUID id;
    private String tenantId;
    private String name;
    private String title;
    private String description;
    private ChartType chartType;
    private UUID dashboardId;
    private String dataSource;
    private String query;
    private Map<String, Object> xAxis;
    private Map<String, Object> yAxis;
    private List<String> groupBy;
    private Map<String, Object> filters;
    private Map<String, Object> style;
    private Integer width;
    private Integer height;
    private Integer positionX;
    private Integer positionY;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Map<String, Object> metadata;
    private List<String> tags;
    private Boolean isActive;
    private String drillDownDashboardId;
    private Boolean enableDrillDown;
}
