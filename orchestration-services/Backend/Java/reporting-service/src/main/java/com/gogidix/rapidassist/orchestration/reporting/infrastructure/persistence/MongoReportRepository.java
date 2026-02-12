package com.gogidix.rapidassist.orchestration.reporting.infrastructure.persistence;

import com.gogidix.rapidassist.orchestration.reporting.domain.model.Report;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MongoReportRepository extends MongoRepository<Report, String> {

    Optional<Report> findByReportId(String reportId);

    List<Report> findByTenantId(String tenantId);

    List<Report> findByTenantIdAndReportType(String tenantId, Report.ReportType reportType);

    List<Report> findByTenantIdAndStatus(String tenantId, Report.ReportStatus status);

    List<Report> findByScheduleId(String scheduleId);

    List<Report> findByTemplateId(String templateId);

    List<Report> findByTenantIdAndGeneratedBy(String tenantId, String generatedBy);

    List<Report> findByTenantIdAndDeletedAtIsNull(String tenantId);

    void deleteByReportId(String reportId);

    void deleteByTenantId(String tenantId);

    boolean existsByReportId(String reportId);

    long countByTenantId(String tenantId);

    long countByTenantIdAndStatus(String tenantId, Report.ReportStatus status);
}
