package com.gogidix.rapidassist.analytics.application.dto;

import com.gogidix.rapidassist.analytics.domain.model.Report;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for Report operations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportDto {

    private UUID id;
    private String tenantId;
    private String reportName;
    private String reportType;
    private String reportCategory;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Map<String, Object> reportData;
    private List<String> includedMetrics;
    private List<Report.ChartConfiguration> charts;
    private String format;
    private String status;
    private Integer totalViews;
    private LocalDateTime lastViewedAt;
    private String createdBy;
    private String lastModifiedBy;
    private String description;
    private String schedule;
    private List<String> recipients;
    private Map<String, Object> filters;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
