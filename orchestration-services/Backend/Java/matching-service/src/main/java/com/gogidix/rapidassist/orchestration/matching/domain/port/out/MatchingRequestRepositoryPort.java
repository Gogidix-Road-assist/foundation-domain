package com.gogidix.rapidassist.orchestration.matching.domain.port.out;

import com.gogidix.rapidassist.orchestration.matching.domain.model.MatchingRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Output port for MatchingRequest repository operations
 */
public interface MatchingRequestRepositoryPort {

    MatchingRequest save(MatchingRequest request);

    Optional<MatchingRequest> findById(String id);

    Optional<MatchingRequest> findByRequestId(String requestId);

    List<MatchingRequest> findByTenantId(String tenantId);

    List<MatchingRequest> findByStatus(MatchingRequest.RequestStatus status);

    List<MatchingRequest> findByIncidentId(String incidentId);

    List<MatchingRequest> findExpiredRequests(LocalDateTime currentTime);

    void deleteById(String id);

    void deleteByRequestId(String requestId);

    boolean existsByRequestId(String requestId);

    List<MatchingRequest> findByTenantIdAndStatus(
        String tenantId,
        MatchingRequest.RequestStatus status
    );
}
