package com.gogidix.rapidassist.orchestration.reporting.infrastructure.persistence;

import com.gogidix.rapidassist.orchestration.reporting.domain.model.ReportHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MongoReportHistoryRepository extends MongoRepository<ReportHistory, String> {

    List<ReportHistory> findByReportId(String reportId);

    List<ReportHistory> findByScheduleId(String scheduleId);

    List<ReportHistory> findByTenantId(String tenantId);

    List<ReportHistory> findByTenantIdAndEventType(String tenantId, ReportHistory.HistoryEventType eventType);

    List<ReportHistory> findByTenantIdAndEventStatus(String tenantId, ReportHistory.HistoryEventStatus eventStatus);

    List<ReportHistory> findByTenantIdAndCreatedAtBetween(String tenantId, LocalDateTime startDate, LocalDateTime endDate);

    void deleteByHistoryId(String historyId);

    void deleteByReportId(String reportId);

    void deleteByScheduleId(String scheduleId);

    void deleteByCreatedAtBefore(LocalDateTime cutoffDate);

    void deleteByTenantId(String tenantId);

    long countByTenantId(String tenantId);

    long countByReportId(String reportId);
}
