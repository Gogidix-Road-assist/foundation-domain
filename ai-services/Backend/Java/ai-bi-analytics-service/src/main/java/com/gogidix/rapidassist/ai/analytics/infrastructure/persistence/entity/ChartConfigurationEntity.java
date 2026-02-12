package com.gogidix.rapidassist.ai.analytics.infrastructure.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * MongoDB entity for ChartConfiguration
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "chart_configurations")
public class ChartConfigurationEntity {

    @Id
    private String id;

    @Indexed
    private String tenantId;

    @Indexed
    private String name;

    private String title;

    private String description;

    @Indexed
    private String chartType;

    @Indexed
    private String dashboardId;

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

    @Indexed
    private LocalDateTime createdAt;

    @Indexed
    private LocalDateTime updatedAt;

    private Map<String, Object> metadata;

    private List<String> tags;

    @Indexed
    private Boolean isActive;

    private String drillDownDashboardId;

    private Boolean enableDrillDown;
}
