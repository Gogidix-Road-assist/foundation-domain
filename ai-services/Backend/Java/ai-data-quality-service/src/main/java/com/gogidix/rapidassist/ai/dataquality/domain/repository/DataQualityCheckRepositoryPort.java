package com.gogidix.rapidassist.ai.dataquality.domain.repository;

import com.gogidix.rapidassist.ai.dataquality.domain.model.DataQualityCheck;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for DataQualityCheck aggregate
 */
public interface DataQualityCheckRepositoryPort {

    DataQualityCheck save(DataQualityCheck check);

    Optional<DataQualityCheck> findById(String tenantId, UUID id);

    List<DataQualityCheck> findByTenantId(String tenantId);

    List<DataQualityCheck> findByTenantIdAndRuleId(String tenantId, UUID ruleId);

    List<DataQualityCheck> findByTenantIdAndStatus(String tenantId, DataQualityCheck.CheckStatus status);

    List<DataQualityCheck> findByTenantIdAndEntityType(String tenantId, String entityType);

    List<DataQualityCheck> findByTenantIdAndExecutedAtBetween(
            String tenantId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    List<DataQualityCheck> findFailedChecksByTenantId(String tenantId);

    List<DataQualityCheck> findRecentChecksByTenantId(String tenantId, int limit);

    void deleteById(String tenantId, UUID id);

    long countByTenantIdAndStatus(String tenantId, DataQualityCheck.CheckStatus status);
}
