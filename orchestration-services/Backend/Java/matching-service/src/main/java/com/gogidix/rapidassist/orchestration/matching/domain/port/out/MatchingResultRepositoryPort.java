package com.gogidix.rapidassist.orchestration.matching.domain.port.out;

import com.gogidix.rapidassist.orchestration.matching.domain.model.MatchingResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Output port for MatchingResult repository operations
 */
public interface MatchingResultRepositoryPort {

    MatchingResult save(MatchingResult result);

    Optional<MatchingResult> findById(String id);

    Optional<MatchingResult> findByRequestId(String requestId);

    List<MatchingResult> findByTenantId(String tenantId);

    List<MatchingResult> findByIncidentId(String incidentId);

    List<MatchingResult> findByStatus(MatchingResult.ResultStatus status);

    List<MatchingResult> findExpiredResults(LocalDateTime currentTime);

    void deleteById(String id);

    void deleteByRequestId(String requestId);

    List<MatchingResult> findByProviderId(String providerId);

    List<MatchingResult> findByTenantIdAndCreatedAtAfter(
        String tenantId,
        LocalDateTime createdAt
    );
}
