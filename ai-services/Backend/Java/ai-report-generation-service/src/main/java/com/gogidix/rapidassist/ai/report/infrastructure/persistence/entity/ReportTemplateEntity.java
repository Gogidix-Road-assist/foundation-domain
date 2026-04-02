package com.gogidix.rapidassist.ai.report.infrastructure.persistence.entity;

import com.gogidix.rapidassist.ai.report.domain.model.ReportFormat;
import com.gogidix.rapidassist.ai.report.domain.model.TemplateType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * MongoDB Document for ReportTemplate.
 * Maps to report_template collection with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "report_template")
public class ReportTemplateEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private String name;

    private String description;

    @Indexed
    private TemplateType templateType;

    private String templateDefinition;

    private Map<String, Object> defaultParameters;

    private ReportFormat defaultFormat;

    @Indexed
    private Boolean isActive;

    private String createdBy;

    private String updatedBy;

    @Indexed
    private LocalDateTime createdAt;

    @Indexed
    private LocalDateTime updatedAt;

    private Integer version;
}
