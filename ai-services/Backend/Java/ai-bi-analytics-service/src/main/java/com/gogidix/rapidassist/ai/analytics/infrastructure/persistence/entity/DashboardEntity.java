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
 * MongoDB entity for Dashboard
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "dashboards")
public class DashboardEntity {

    @Id
    private String id;

    @Indexed
    private String tenantId;

    @Indexed
    private String name;

    private String description;

    private String layout;

    private String theme;

    @Indexed
    private Boolean isPublic;

    private String createdBy;

    private String updatedBy;

    @Indexed
    private LocalDateTime createdAt;

    @Indexed
    private LocalDateTime updatedAt;

    private List<String> chartIds;

    private Map<String, Object> filters;

    private String refreshInterval;

    private Boolean autoRefresh;

    private Map<String, Object> metadata;

    private List<String> tags;

    @Indexed
    private Boolean isActive;

    private Integer displayOrder;

    @Indexed
    private String category;
}
