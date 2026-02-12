package com.gogidix.rapidassist.orchestration.reporting.infrastructure.persistence;

import com.gogidix.rapidassist.orchestration.reporting.domain.model.Report;
import com.gogidix.rapidassist.orchestration.reporting.domain.port.out.ReportRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReportRepositoryImpl implements ReportRepositoryPort {

    private final MongoReportRepository mongoRepository;
    private final MongoTemplate mongoTemplate;

    @Override
    public Report save(Report report) {
        return mongoRepository.save(report);
    }

    @Override
    public Optional<Report> findByReportId(String reportId) {
        return mongoRepository.findByReportId(reportId);
    }

    @Override
    public List<Report> findByTenantId(String tenantId) {
        return mongoRepository.findByTenantId(tenantId);
    }

    @Override
    public List<Report> findByTenantIdAndReportType(String tenantId, Report.ReportType reportType) {
        return mongoRepository.findByTenantIdAndReportType(tenantId, reportType);
    }

    @Override
    public List<Report> findByTenantIdAndStatus(String tenantId, Report.ReportStatus status) {
        return mongoRepository.findByTenantIdAndStatus(tenantId, status);
    }

    @Override
    public List<Report> findByScheduleId(String scheduleId) {
        return mongoRepository.findByScheduleId(scheduleId);
    }

    @Override
    public List<Report> findByTemplateId(String templateId) {
        return mongoRepository.findByTemplateId(templateId);
    }

    @Override
    public List<Report> findByTenantIdAndGeneratedBy(String tenantId, String userId) {
        return mongoRepository.findByTenantIdAndGeneratedBy(tenantId, userId);
    }

    @Override
    public List<Report> findActiveReportsByTenant(String tenantId) {
        return mongoRepository.findByTenantIdAndDeletedAtIsNull(tenantId);
    }

    @Override
    public List<Report> findExpiredReports(LocalDateTime now) {
        Query query = new Query();
        query.addCriteria(Criteria.where("expiresAt").lt(now)
                .and("status").ne(Report.ReportStatus.EXPIRED));
        return mongoTemplate.find(query, Report.class);
    }

    @Override
    public List<Report> findReportsToDelete(LocalDateTime cutoffDate) {
        Query query = new Query();
        query.addCriteria(Criteria.where("deletedAt").lt(cutoffDate));
        return mongoTemplate.find(query, Report.class);
    }

    @Override
    public void deleteByReportId(String reportId) {
        mongoRepository.deleteByReportId(reportId);
    }

    @Override
    public void deleteAllByTenantId(String tenantId) {
        mongoRepository.deleteByTenantId(tenantId);
    }

    @Override
    public boolean existsByReportId(String reportId) {
        return mongoRepository.existsByReportId(reportId);
    }

    @Override
    public long countByTenantId(String tenantId) {
        return mongoRepository.countByTenantId(tenantId);
    }

    @Override
    public long countByTenantIdAndStatus(String tenantId, Report.ReportStatus status) {
        return mongoRepository.countByTenantIdAndStatus(tenantId, status);
    }
}
