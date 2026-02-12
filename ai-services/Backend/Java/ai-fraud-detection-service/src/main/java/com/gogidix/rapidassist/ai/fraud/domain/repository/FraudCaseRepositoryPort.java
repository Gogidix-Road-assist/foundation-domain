package com.gogidix.rapidassist.ai.fraud.domain.repository;

import com.gogidix.rapidassist.ai.fraud.domain.model.FraudCase;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for FraudCase aggregate
 */
public interface FraudCaseRepositoryPort {

    FraudCase save(String tenantId, FraudCase caseEntity);
    Optional<FraudCase> findById(String tenantId, UUID id);
    Optional<FraudCase> findByCaseNumber(String tenantId, String caseNumber);
    List<FraudCase> findByTenantId(String tenantId);
    List<FraudCase> findByStatus(String tenantId, String status);
    List<FraudCase> findByAssignedTo(String tenantId, String assignedTo);
    List<FraudCase> findByPriority(String tenantId, String priority);
    List<FraudCase> findOpenCases(String tenantId);
    void delete(String tenantId, UUID id);
    boolean exists(String tenantId, UUID id);
}
