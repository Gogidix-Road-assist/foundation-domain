package com.gogidix.rapidassist.orchestration.reporting.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO for ReportTemplate responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportTemplateDTO {
    private String id;
    private String templateId;
    private String name;
    private String description;
    private ReportDTO.ReportTypeDTO reportType;
    private TemplateStatusDTO status;
    private TemplateCategoryDTO category;
    private String layoutDefinition;
    private Map<String, TemplateParameterDTO> parameters;
    private Map<String, Object> defaultValues;
    private Boolean isSystemTemplate;
    private String createdBy;
    private Integer version;
    private String tenantId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum TemplateStatusDTO {
        ACTIVE, INACTIVE, DRAFT, ARCHIVED
    }

    public enum TemplateCategoryDTO {
        OPERATIONAL, ANALYTICAL, COMPLIANCE, FINANCIAL, CUSTOM
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TemplateParameterDTO {
        private String name;
        private String displayName;
        private ParameterTypeDTO type;
        private boolean required;
        private Object defaultValue;
        private Map<String, Object> validationRules;

        public enum ParameterTypeDTO {
            STRING, NUMBER, DATE, DATE_RANGE, BOOLEAN, SELECT, MULTI_SELECT
        }
    }
}
