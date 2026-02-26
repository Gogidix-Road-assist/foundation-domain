package com.gogidix.rapidassist.orchestration.reporting.domain.port.out;

import com.gogidix.rapidassist.orchestration.reporting.domain.model.Report;
import com.gogidix.rapidassist.orchestration.reporting.domain.model.ReportTemplate;

import java.util.List;
import java.util.Optional;

/**
 * Output port for ReportTemplate repository operations.
 * Defines the contract for template persistence.
 */
public interface ReportTemplateRepositoryPort {

    ReportTemplate save(ReportTemplate template);

    Optional<ReportTemplate> findByTemplateId(String templateId);

    List<ReportTemplate> findByTenantId(String tenantId);

    List<ReportTemplate> findByTenantIdAndReportType(String tenantId, Report.ReportType reportType);

    List<ReportTemplate> findByTenantIdAndStatus(String tenantId, ReportTemplate.TemplateStatus status);

    List<ReportTemplate> findActiveTemplatesByTenant(String tenantId);

    List<ReportTemplate> findSystemTemplates();

    List<ReportTemplate> findByCreatedBy(String createdBy);

    Optional<ReportTemplate> findByNameAndTenantId(String name, String tenantId);

    void deleteByTemplateId(String templateId);

    void deleteAllByTenantId(String tenantId);

    boolean existsByTemplateId(String templateId);

    long countByTenantId(String tenantId);

    long countByTenantIdAndReportType(String tenantId, Report.ReportType reportType);
}
