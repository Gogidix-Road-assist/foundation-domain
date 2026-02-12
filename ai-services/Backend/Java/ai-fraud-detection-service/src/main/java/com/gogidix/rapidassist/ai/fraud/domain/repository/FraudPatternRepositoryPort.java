package com.gogidix.rapidassist.ai.fraud.domain.repository;

import com.gogidix.rapidassist.ai.fraud.domain.model.FraudPattern;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for FraudPattern aggregate
 */
public interface FraudPatternRepositoryPort {

    FraudPattern save(String tenantId, FraudPattern pattern);
    Optional<FraudPattern> findById(String tenantId, UUID id);
    List<FraudPattern> findByTenantId(String tenantId);
    List<FraudPattern> findByPatternType(String tenantId, String patternType);
    List<FraudPattern> findActivePatterns(String tenantId);
    void delete(String tenantId, UUID id);
    boolean exists(String tenantId, UUID id);
}
