package com.gogidix.rapidassist.ai.analytics.application.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO for Dashboard
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDto {

    private String id;
    private String tenantId;
    private String name;
    private String description;
    private String layout;
    private String theme;
    private Boolean isPublic;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> chartIds;
    private Map<String, Object> filters;
    private String refreshInterval;
    private Boolean autoRefresh;
    private Map<String, Object> metadata;
    private List<String> tags;
    private Boolean isActive;
    private Integer displayOrder;
    private String category;
}
