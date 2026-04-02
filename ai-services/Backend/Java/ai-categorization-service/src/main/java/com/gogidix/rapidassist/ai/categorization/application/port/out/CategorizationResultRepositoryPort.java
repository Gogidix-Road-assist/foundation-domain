package com.gogidix.rapidassist.ai.categorization.application.port.out;

import com.gogidix.rapidassist.ai.categorization.domain.model.CategorizationResult;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for CategorizationResult aggregate.
 */
public interface CategorizationResultRepositoryPort {

    CategorizationResult save(String tenantId, CategorizationResult result);

    Optional<CategorizationResult> findById(String tenantId, UUID resultId);

    Optional<CategorizationResult> findByRequestId(String tenantId, UUID requestId);

    List<CategorizationResult> findByContentId(String tenantId, String contentId);

    List<CategorizationResult> findByTenantId(String tenantId);

    List<CategorizationResult> findByStatus(String tenantId, String status);

    void delete(String tenantId, UUID resultId);

    boolean exists(String tenantId, UUID resultId);
}
