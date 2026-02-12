package com.gogidix.rapidassist.ai.fraud.domain.repository;

import com.gogidix.rapidassist.ai.fraud.domain.model.FraudAlert;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for FraudAlert aggregate
 */
public interface FraudAlertRepositoryPort {

    FraudAlert save(String tenantId, FraudAlert alert);
    Optional<FraudAlert> findById(String tenantId, UUID id);
    List<FraudAlert> findByTenantId(String tenantId);
    List<FraudAlert> findByStatus(String tenantId, String status);
    List<FraudAlert> findBySeverity(String tenantId, String severity);
    List<FraudAlert> findByAssignedTo(String tenantId, String assignedTo);
    List<FraudAlert> findPendingAlerts(String tenantId);
    List<FraudAlert> findCriticalAlerts(String tenantId);
    void delete(String tenantId, UUID id);
    boolean exists(String tenantId, UUID id);
}
