package com.gogidix.rapidassist.ai.matching.domain.repository;

import com.gogidix.rapidassist.ai.matching.domain.model.MatchingResult;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for MatchingResult operations.
 * Defines the contract for matching result persistence.
 */
public interface MatchingResultRepositoryPort {

    MatchingResult save(String tenantId, MatchingResult matchingResult);

    Optional<MatchingResult> findById(String tenantId, UUID id);

    Optional<MatchingResult> findByMatchId(String tenantId, String matchId);

    List<MatchingResult> findByTenantId(String tenantId);

    List<MatchingResult> findBySourceEntity(String tenantId, String entityType, String entityId);

    List<MatchingResult> findByTargetEntity(String tenantId, String entityType, String entityId);

    List<MatchingResult> findByStatus(String tenantId, String status);

    void delete(String tenantId, UUID id);

    boolean exists(String tenantId, UUID id);

    long countByTenantId(String tenantId);
}
