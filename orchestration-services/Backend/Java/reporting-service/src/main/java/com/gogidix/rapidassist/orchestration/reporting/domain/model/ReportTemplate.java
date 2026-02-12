package com.gogidix.rapidassist.orchestration.reporting.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * ReportTemplate entity representing reusable report templates.
 * Defines the structure and parameters for report generation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "report_templates")
@CompoundIndex(name = "tenant_type_idx", def = "{'tenantId': 1, 'reportType': 1}")
public class ReportTemplate {

    @Id
    private String id;

    @Indexed(unique = true)
    private String templateId;

    @Indexed
    private String name;

    private String description;

    @Indexed
    private Report.ReportType reportType;

    @Indexed
    @Builder.Default
    private TemplateStatus status = TemplateStatus.ACTIVE;

    @Indexed
    private TemplateCategory category;

    private String layoutDefinition; // JSON structure defining report layout

    private Map<String, TemplateParameter> parameters;

    private Map<String, Object> defaultValues;

    @Indexed
    @Builder.Default
    private Boolean isSystemTemplate = false;

    @Indexed
    private String createdBy;

    private Integer version;

    @Indexed
    private String tenantId;

    @CreatedDate
    @Indexed
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Indexed
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    /**
     * Domain logic: Check if template is system-defined
     */
    public boolean isSystemDefined() {
        return Boolean.TRUE.equals(isSystemTemplate);
    }

    /**
     * Domain logic: Check if template is active
     */
    public boolean isActive() {
        return status == TemplateStatus.ACTIVE;
    }

    /**
     * Domain logic: Get parameter definition
     */
    public TemplateParameter getParameter(String parameterName) {
        return parameters != null ? parameters.get(parameterName) : null;
    }

    /**
     * Domain logic: Validate template structure
     */
    public void validate() {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Template name cannot be blank");
        }
        if (reportType == null) {
            throw new IllegalArgumentException("Report type is required");
        }
        if (category == null) {
            throw new IllegalArgumentException("Template category is required");
        }
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalArgumentException("Tenant ID is required");
        }
        if (layoutDefinition == null || layoutDefinition.isBlank()) {
            throw new IllegalArgumentException("Layout definition is required");
        }
    }

    /**
     * Domain logic: Activate template
     */
    public void activate() {
        this.status = TemplateStatus.ACTIVE;
    }

    /**
     * Domain logic: Deactivate template
     */
    public void deactivate() {
        this.status = TemplateStatus.INACTIVE;
    }

    /**
     * Domain logic: Soft delete template
     */
    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
        this.status = TemplateStatus.INACTIVE;
    }

    public enum TemplateStatus {
        ACTIVE,
        INACTIVE,
        DRAFT,
        ARCHIVED
    }

    public enum TemplateCategory {
        OPERATIONAL,
        ANALYTICAL,
        COMPLIANCE,
        FINANCIAL,
        CUSTOM
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TemplateParameter {
        private String name;
        private String displayName;
        private ParameterType type;
        private boolean required;
        private Object defaultValue;
        private Map<String, Object> validationRules;

        public enum ParameterType {
            STRING,
            NUMBER,
            DATE,
            DATE_RANGE,
            BOOLEAN,
            SELECT,
            MULTI_SELECT
        }
    }
}
