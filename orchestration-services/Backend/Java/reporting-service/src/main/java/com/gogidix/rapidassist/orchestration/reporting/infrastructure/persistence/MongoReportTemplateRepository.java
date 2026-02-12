package com.gogidix.rapidassist.orchestration.reporting.infrastructure.persistence;

import com.gogidix.rapidassist.orchestration.reporting.domain.model.Report;
import com.gogidix.rapidassist.orchestration.reporting.domain.model.ReportTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MongoReportTemplateRepository extends MongoRepository<ReportTemplate, String> {

    Optional<ReportTemplate> findByTemplateId(String templateId);

    List<ReportTemplate> findByTenantId(String tenantId);

    List<ReportTemplate> findByTenantIdAndReportType(String tenantId, Report.ReportType reportType);

    List<ReportTemplate> findByTenantIdAndStatus(String tenantId, ReportTemplate.TemplateStatus status);

    List<ReportTemplate> findByIsSystemTemplateTrue();

    List<ReportTemplate> findByCreatedBy(String createdBy);

    Optional<ReportTemplate> findByNameAndTenantId(String name, String tenantId);

    void deleteByTemplateId(String templateId);

    void deleteByTenantId(String tenantId);

    boolean existsByTemplateId(String templateId);

    long countByTenantId(String tenantId);

    long countByTenantIdAndReportType(String tenantId, Report.ReportType reportType);
}
