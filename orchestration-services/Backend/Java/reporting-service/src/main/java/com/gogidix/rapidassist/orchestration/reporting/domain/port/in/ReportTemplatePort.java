package com.gogidix.rapidassist.orchestration.reporting.domain.port.in;

import com.gogidix.rapidassist.orchestration.reporting.domain.model.ReportTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Input port for Report Template operations.
 * Defines the contract for template management use cases.
 */
public interface ReportTemplatePort {

    /**
     * Create a new template
     */
    ReportTemplate createTemplate(ReportTemplate template);

    /**
     * Update template
     */
    ReportTemplate updateTemplate(String templateId, ReportTemplate template);

    /**
     * Get template by ID
     */
    Optional<ReportTemplate> getTemplate(String templateId);

    /**
     * Get templates by tenant
     */
    List<ReportTemplate> getTemplatesByTenant(String tenantId);

    /**
     * Get templates by report type
     */
    List<ReportTemplate> getTemplatesByType(String tenantId, String reportType);

    /**
     * Get active templates
     */
    List<ReportTemplate> getActiveTemplates(String tenantId);

    /**
     * Get system templates
     */
    List<ReportTemplate> getSystemTemplates();

    /**
     * Delete template
     */
    void deleteTemplate(String templateId);

    /**
     * Activate template
     */
    void activateTemplate(String templateId);

    /**
     * Deactivate template
     */
    void deactivateTemplate(String templateId);

    /**
     * Validate template structure
     */
    boolean validateTemplate(ReportTemplate template);

    /**
     * Clone template
     */
    ReportTemplate cloneTemplate(String templateId, String newName, String tenantId);

    /**
     * Get template usage statistics
     */
    TemplateStatistics getTemplateStatistics(String templateId);

    record TemplateStatistics(
        long timesUsed,
        long reportsGenerated,
        LocalDateTime lastUsed
    ) {}
}
