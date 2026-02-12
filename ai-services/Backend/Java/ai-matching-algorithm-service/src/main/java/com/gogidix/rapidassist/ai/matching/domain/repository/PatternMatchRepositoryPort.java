package com.gogidix.rapidassist.ai.matching.domain.repository;

import com.gogidix.rapidassist.ai.matching.domain.model.PatternMatch;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for PatternMatch operations.
 * Defines the contract for pattern match persistence.
 */
public interface PatternMatchRepositoryPort {

    PatternMatch save(String tenantId, PatternMatch patternMatch);

    Optional<PatternMatch> findById(String tenantId, UUID id);

    Optional<PatternMatch> findByPatternId(String tenantId, String patternId);

    List<PatternMatch> findByTenantId(String tenantId);

    List<PatternMatch> findByEntityType(String tenantId, String entityType);

    List<PatternMatch> findByEntityId(String tenantId, String entityType, String entityId);

    void delete(String tenantId, UUID id);

    boolean exists(String tenantId, UUID id);

    long countByTenantId(String tenantId);
}
