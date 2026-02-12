package com.gogidix.rapidassist.ai.report.domain.repository;

import com.gogidix.rapidassist.ai.report.domain.model.ReportTemplate;
import com.gogidix.rapidassist.ai.report.domain.model.TemplateType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository Port for ReportTemplate.
 */
public interface ReportTemplateRepositoryPort {

    /**
     * Save a template (create or update).
     */
    ReportTemplate save(String tenantId, ReportTemplate template);

    /**
     * Find a template by ID and tenant.
     */
    Optional<ReportTemplate> findById(String tenantId, UUID templateId);

    /**
     * Find all templates for a tenant.
     */
    List<ReportTemplate> findByTenantId(String tenantId);

    /**
     * Find active templates for a tenant.
     */
    List<ReportTemplate> findActiveTemplates(String tenantId);

    /**
     * Find templates by type and tenant.
     */
    List<ReportTemplate> findByTemplateType(String tenantId, TemplateType templateType);

    /**
     * Find templates by name pattern and tenant.
     */
    List<ReportTemplate> findByNameContaining(String tenantId, String namePattern);

    /**
     * Delete a template by ID and tenant.
     */
    void delete(String tenantId, UUID templateId);

    /**
     * Check if a template exists.
     */
    boolean exists(String tenantId, UUID templateId);

    /**
     * Count templates by tenant.
     */
    long countByTenantId(String tenantId);
}
