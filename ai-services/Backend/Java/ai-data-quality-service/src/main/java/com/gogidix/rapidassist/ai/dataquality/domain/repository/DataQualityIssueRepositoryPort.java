package com.gogidix.rapidassist.ai.dataquality.domain.repository;

import com.gogidix.rapidassist.ai.dataquality.domain.model.DataQualityIssue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for DataQualityIssue aggregate
 */
public interface DataQualityIssueRepositoryPort {

    DataQualityIssue save(DataQualityIssue issue);

    Optional<DataQualityIssue> findById(String tenantId, UUID id);

    List<DataQualityIssue> findByTenantId(String tenantId);

    List<DataQualityIssue> findByTenantIdAndStatus(String tenantId, DataQualityIssue.IssueStatus status);

    List<DataQualityIssue> findByTenantIdAndSeverity(String tenantId, DataQualityIssue.IssueSeverity severity);

    List<DataQualityIssue> findByTenantIdAndEntityType(String tenantId, String entityType);

    List<DataQualityIssue> findByCheckId(String tenantId, UUID checkId);

    List<DataQualityIssue> findByRuleId(String tenantId, UUID ruleId);

    List<DataQualityIssue> findOpenIssuesByTenantId(String tenantId);

    List<DataQualityIssue> findCriticalIssuesByTenantId(String tenantId);

    List<DataQualityIssue> findStaleIssuesByTenantId(String tenantId, int staleThresholdDays);

    List<DataQualityIssue> findIssuesDetectedBetween(
            String tenantId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    void deleteById(String tenantId, UUID id);

    long countByTenantIdAndStatus(String tenantId, DataQualityIssue.IssueStatus status);

    long countByTenantIdAndSeverity(String tenantId, DataQualityIssue.IssueSeverity severity);
}
