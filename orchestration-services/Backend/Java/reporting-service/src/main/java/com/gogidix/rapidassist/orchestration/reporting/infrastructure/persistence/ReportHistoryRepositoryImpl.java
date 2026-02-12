package com.gogidix.rapidassist.orchestration.reporting.infrastructure.persistence;

import com.gogidix.rapidassist.orchestration.reporting.domain.model.ReportHistory;
import com.gogidix.rapidassist.orchestration.reporting.domain.port.out.ReportHistoryRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReportHistoryRepositoryImpl implements ReportHistoryRepositoryPort {

    private final MongoReportHistoryRepository mongoRepository;
    private final MongoTemplate mongoTemplate;

    @Override
    public ReportHistory save(ReportHistory history) {
        return mongoRepository.save(history);
    }

    @Override
    public List<ReportHistory> findByReportId(String reportId) {
        return mongoRepository.findByReportId(reportId);
    }

    @Override
    public List<ReportHistory> findByScheduleId(String scheduleId) {
        return mongoRepository.findByScheduleId(scheduleId);
    }

    @Override
    public List<ReportHistory> findByTenantId(String tenantId) {
        return mongoRepository.findByTenantId(tenantId);
    }

    @Override
    public List<ReportHistory> findByTenantIdAndEventType(String tenantId, ReportHistory.HistoryEventType eventType) {
        return mongoRepository.findByTenantIdAndEventType(tenantId, eventType);
    }

    @Override
    public List<ReportHistory> findByTenantIdAndEventStatus(String tenantId, ReportHistory.HistoryEventStatus eventStatus) {
        return mongoRepository.findByTenantIdAndEventStatus(tenantId, eventStatus);
    }

    @Override
    public List<ReportHistory> findByTenantIdAndCreatedAtBetween(String tenantId, LocalDateTime startDate, LocalDateTime endDate) {
        return mongoRepository.findByTenantIdAndCreatedAtBetween(tenantId, startDate, endDate);
    }

    @Override
    public List<ReportHistory> findRecentHistoryByTenant(String tenantId, int limit) {
        Query query = new Query();
        query.addCriteria(Criteria.where("tenantId").is(tenantId));
        query.limit(limit);
        return mongoTemplate.find(query, ReportHistory.class);
    }

    @Override
    public void deleteByHistoryId(String historyId) {
        mongoRepository.deleteByHistoryId(historyId);
    }

    @Override
    public void deleteByReportId(String reportId) {
        mongoRepository.deleteByReportId(reportId);
    }

    @Override
    public void deleteByScheduleId(String scheduleId) {
        mongoRepository.deleteByScheduleId(scheduleId);
    }

    @Override
    public void deleteHistoryBefore(LocalDateTime cutoffDate) {
        mongoRepository.deleteByCreatedAtBefore(cutoffDate);
    }

    @Override
    public void deleteAllByTenantId(String tenantId) {
        mongoRepository.deleteByTenantId(tenantId);
    }

    @Override
    public long countByTenantId(String tenantId) {
        return mongoRepository.countByTenantId(tenantId);
    }

    @Override
    public long countByReportId(String reportId) {
        return mongoRepository.countByReportId(reportId);
    }
}
