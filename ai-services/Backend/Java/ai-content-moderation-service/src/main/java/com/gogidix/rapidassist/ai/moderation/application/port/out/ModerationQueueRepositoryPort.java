package com.gogidix.rapidassist.ai.moderation.application.port.out;

import com.gogidix.rapidassist.ai.moderation.domain.model.ModerationQueue;

import java.util.List;
import java.util.Optional;

/**
 * Repository port for ModerationQueue aggregate.
 * Defines the contract for moderation queue persistence operations.
 */
public interface ModerationQueueRepositoryPort {

    /**
     * Save a queue item
     */
    ModerationQueue save(ModerationQueue queue);

    /**
     * Find queue item by ID
     */
    Optional<ModerationQueue> findById(String id);

    /**
     * Find queue item by content ID
     */
    Optional<ModerationQueue> findByContentId(String contentId);

    /**
     * Find all pending items for a tenant
     */
    List<ModerationQueue> findPendingByTenantId(String tenantId);

    /**
     * Find items by status for a tenant
     */
    List<ModerationQueue> findByTenantIdAndStatus(String tenantId, ModerationQueue.QueueStatus status);

    /**
     * Find items assigned to a reviewer
     */
    List<ModerationQueue> findByAssignedTo(String reviewerId);

    /**
     * Delete queue item by ID
     */
    void deleteById(String id);
}
