package com.gogidix.rapidassist.ai.report.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Domain model representing a report template.
 * Templates define the structure and layout for generated reports.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportTemplate {

    private UUID id;
    private String tenantId;
    private String name;
    private String description;
    private TemplateType templateType;
    private String templateDefinition;
    private Map<String, Object> defaultParameters;
    private ReportFormat defaultFormat;
    private boolean isActive;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer version;
}
