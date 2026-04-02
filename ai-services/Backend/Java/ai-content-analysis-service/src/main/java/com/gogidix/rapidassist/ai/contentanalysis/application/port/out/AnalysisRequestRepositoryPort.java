package com.gogidix.rapidassist.ai.contentanalysis.application.port.out;

import com.gogidix.rapidassist.ai.contentanalysis.domain.model.AnalysisRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for Analysis Request entity.
 * Defines the contract for persisting and retrieving analysis request data.
 */
public interface AnalysisRequestRepositoryPort {

    /**
     * Save analysis request (create or update)
     */
    AnalysisRequest save(AnalysisRequest request);

    /**
     * Find analysis request by ID
     */
    Optional<AnalysisRequest> findById(UUID id);

    /**
     * Find analysis request by ID and tenant ID
     */
    Optional<AnalysisRequest> findByIdAndTenantId(UUID id, String tenantId);

    /**
     * Find analysis request by request ID
     */
    Optional<AnalysisRequest> findByRequestId(String requestId);

    /**
     * Find analysis request by request ID and tenant ID
     */
    Optional<AnalysisRequest> findByRequestIdAndTenantId(String requestId, String tenantId);

    /**
     * Find all requests for a specific content
     */
    List<AnalysisRequest> findByContentId(String contentId);

    /**
     * Find all requests for a specific content and tenant
     */
    List<AnalysisRequest> findByContentIdAndTenantId(String contentId, String tenantId);

    /**
     * Find requests by status
     */
    List<AnalysisRequest> findByStatus(AnalysisRequest.RequestStatus status);

    /**
     * Find requests by status and tenant ID
     */
    List<AnalysisRequest> findByStatusAndTenantId(AnalysisRequest.RequestStatus status, String tenantId);

    /**
     * Find pending requests ordered by priority
     */
    List<AnalysisRequest> findByStatusOrderByPriorityDescCreatedAtAsc(AnalysisRequest.RequestStatus status);

    /**
     * Find high priority pending requests
     */
    List<AnalysisRequest> findHighPriorityPendingRequests(Integer minPriority);

    /**
     * Delete request by ID
     */
    void deleteById(UUID id);

    /**
     * Delete request by ID and tenant ID
     */
    void deleteByIdAndTenantId(UUID id, String tenantId);

    /**
     * Delete old completed requests
     */
    void deleteOldCompletedRequests(Integer daysToKeep);
}
