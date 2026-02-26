package com.gogidix.rapidassist.orchestration.matching.domain.port.out;

import com.gogidix.rapidassist.orchestration.matching.domain.model.MatchingHistory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Output port for MatchingHistory repository operations
 */
public interface MatchingHistoryRepositoryPort {

    MatchingHistory save(MatchingHistory history);

    Optional<MatchingHistory> findById(String id);

    Optional<MatchingHistory> findByRequestId(String requestId);

    List<MatchingHistory> findByTenantId(String tenantId);

    List<MatchingHistory> findByProviderId(String providerId);

    List<MatchingHistory> findByIncidentId(String incidentId);

    List<MatchingHistory> findByDecisionStatus(MatchingHistory.DecisionStatus status);

    List<MatchingHistory> findByCreatedAtBetween(
        LocalDateTime startDate,
        LocalDateTime endDate
    );

    List<MatchingHistory> findByProviderIdAndCreatedAtBetween(
        String providerId,
        LocalDateTime startDate,
        LocalDateTime endDate
    );

    void deleteById(String id);

    List<MatchingHistory> findByTenantIdAndCreatedAtAfter(
        String tenantId,
        LocalDateTime createdAt
    );
}
