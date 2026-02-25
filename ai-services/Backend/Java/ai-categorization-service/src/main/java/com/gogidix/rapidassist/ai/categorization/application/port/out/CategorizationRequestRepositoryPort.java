package com.gogidix.rapidassist.ai.categorization.application.port.out;

import com.gogidix.rapidassist.ai.categorization.domain.model.CategorizationRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for CategorizationRequest aggregate.
 */
public interface CategorizationRequestRepositoryPort {

    CategorizationRequest save(String tenantId, CategorizationRequest request);

    Optional<CategorizationRequest> findById(String tenantId, UUID requestId);

    Optional<CategorizationRequest> findByContentId(String tenantId, String contentId);

    List<CategorizationRequest> findByTenantId(String tenantId);

    List<CategorizationRequest> findByStatus(String tenantId, String status);

    List<CategorizationRequest> findPendingRequests(String tenantId);

    void delete(String tenantId, UUID requestId);

    boolean exists(String tenantId, UUID requestId);
}
