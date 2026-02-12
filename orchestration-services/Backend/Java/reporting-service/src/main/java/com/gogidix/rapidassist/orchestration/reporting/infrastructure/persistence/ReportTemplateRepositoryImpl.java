package com.gogidix.rapidassist.orchestration.reporting.infrastructure.persistence;

import com.gogidix.rapidassist.orchestration.reporting.domain.model.Report;
import com.gogidix.rapidassist.orchestration.reporting.domain.model.ReportTemplate;
import com.gogidix.rapidassist.orchestration.reporting.domain.port.out.ReportTemplateRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReportTemplateRepositoryImpl implements ReportTemplateRepositoryPort {

    private final MongoReportTemplateRepository mongoRepository;

    @Override
    public ReportTemplate save(ReportTemplate template) {
        return mongoRepository.save(template);
    }

    @Override
    public Optional<ReportTemplate> findByTemplateId(String templateId) {
        return mongoRepository.findByTemplateId(templateId);
    }

    @Override
    public List<ReportTemplate> findByTenantId(String tenantId) {
        return mongoRepository.findByTenantId(tenantId);
    }

    @Override
    public List<ReportTemplate> findByTenantIdAndReportType(String tenantId, Report.ReportType reportType) {
        return mongoRepository.findByTenantIdAndReportType(tenantId, reportType);
    }

    @Override
    public List<ReportTemplate> findByTenantIdAndStatus(String tenantId, ReportTemplate.TemplateStatus status) {
        return mongoRepository.findByTenantIdAndStatus(tenantId, status);
    }

    @Override
    public List<ReportTemplate> findActiveTemplatesByTenant(String tenantId) {
        return mongoRepository.findByTenantIdAndStatus(tenantId, ReportTemplate.TemplateStatus.ACTIVE);
    }

    @Override
    public List<ReportTemplate> findSystemTemplates() {
        return mongoRepository.findByIsSystemTemplateTrue();
    }

    @Override
    public List<ReportTemplate> findByCreatedBy(String createdBy) {
        return mongoRepository.findByCreatedBy(createdBy);
    }

    @Override
    public Optional<ReportTemplate> findByNameAndTenantId(String name, String tenantId) {
        return mongoRepository.findByNameAndTenantId(name, tenantId);
    }

    @Override
    public void deleteByTemplateId(String templateId) {
        mongoRepository.deleteByTemplateId(templateId);
    }

    @Override
    public void deleteAllByTenantId(String tenantId) {
        mongoRepository.deleteByTenantId(tenantId);
    }

    @Override
    public boolean existsByTemplateId(String templateId) {
        return mongoRepository.existsByTemplateId(templateId);
    }

    @Override
    public long countByTenantId(String tenantId) {
        return mongoRepository.countByTenantId(tenantId);
    }

    @Override
    public long countByTenantIdAndReportType(String tenantId, Report.ReportType reportType) {
        return mongoRepository.countByTenantIdAndReportType(tenantId, reportType);
    }
}
