package com.gogidix.rapidassist.ai.moderation.application.port.out;

import com.gogidix.rapidassist.ai.moderation.domain.model.ModerationResult;

import java.util.List;
import java.util.Optional;

/**
 * Repository port for ModerationResult aggregate.
 * Defines the contract for moderation result persistence operations.
 */
public interface ModerationResultRepositoryPort {

    /**
     * Save a moderation result
     */
    ModerationResult save(ModerationResult result);

    /**
     * Find result by ID
     */
    Optional<ModerationResult> findById(String id);

    /**
     * Find result by content ID
     */
    Optional<ModerationResult> findByContentId(String contentId);

    /**
     * Find all results for a tenant
     */
    List<ModerationResult> findByTenantId(String tenantId);

    /**
     * Find results by status for a tenant
     */
    List<ModerationResult> findByTenantIdAndStatus(String tenantId, ModerationResult.ModerationStatus status);

    /**
     * Find recent results for a tenant
     */
    List<ModerationResult> findRecentByTenantId(String tenantId, int limit);

    /**
     * Delete result by ID
     */
    void deleteById(String id);
}
