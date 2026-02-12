package com.gogidix.rapidassist.ai.summization.domain.repository;

import com.gogidix.rapidassist.ai.summization.domain.model.SummaryHistory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository port for SummaryHistory.
 */
public interface SummaryHistoryRepositoryPort {

    SummaryHistory save(String tenantId, SummaryHistory history);

    List<SummaryHistory> findByTenantId(String tenantId);

    List<SummaryHistory> findByUserId(String tenantId, String userId);

    List<SummaryHistory> findByTaskId(String tenantId, UUID taskId);

    List<SummaryHistory> findBySummaryId(String tenantId, UUID summaryId);

    List<SummaryHistory> findByDateRange(String tenantId, LocalDateTime startDate, LocalDateTime endDate);

    List<SummaryHistory> findByAction(String tenantId, String action);

    long countByTenantId(String tenantId);

    List<SummaryHistory> findRecent(String tenantId, int limit);
}
