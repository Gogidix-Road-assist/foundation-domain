package com.gogidix.rapidassist.ai.moderation.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.moderation.domain.model.ModerationQueue;
import com.gogidix.rapidassist.ai.moderation.infrastructure.persistence.document.ModerationQueueDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data MongoDB repository for ModerationQueueDocument
 */
@Repository
public interface SpringDataModerationQueueRepository extends MongoRepository<ModerationQueueDocument, String> {

    List<ModerationQueueDocument> findByTenantIdAndStatusOrderByPriorityDescSubmittedAtAsc(
        String tenantId,
        ModerationQueue.QueueStatus status
    );

    List<ModerationQueueDocument> findByTenantIdAndStatus(String tenantId, ModerationQueue.QueueStatus status);

    List<ModerationQueueDocument> findByAssignedTo(String reviewerId);

    Optional<ModerationQueueDocument> findByContentId(String contentId);

    List<ModerationQueueDocument> findByTenantId(String tenantId);
}
