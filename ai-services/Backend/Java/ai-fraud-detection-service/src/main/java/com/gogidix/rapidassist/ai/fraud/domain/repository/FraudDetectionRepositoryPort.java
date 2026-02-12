package com.gogidix.rapidassist.ai.fraud.domain.repository;

import com.gogidix.rapidassist.ai.fraud.domain.model.FraudDetection;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for FraudDetection aggregate
 */
public interface FraudDetectionRepositoryPort {

    FraudDetection save(String tenantId, FraudDetection detection);
    Optional<FraudDetection> findById(String tenantId, UUID id);
    List<FraudDetection> findByTenantId(String tenantId);
    List<FraudDetection> findByEntityId(String tenantId, String entityId);
    List<FraudDetection> findByRiskLevel(String tenantId, String riskLevel);
    List<FraudDetection> findByStatus(String tenantId, String status);
    List<FraudDetection> findPendingReview(String tenantId);
    void delete(String tenantId, UUID id);
    boolean exists(String tenantId, UUID id);
}
