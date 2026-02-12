package com.gogidix.rapidassist.ai.chatbot.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.chatbot.infrastructure.persistence.entity.ConversationFlowEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB Repository for ConversationFlowEntity.
 */
public interface SpringDataConversationFlowRepository extends MongoRepository<ConversationFlowEntity, String> {

    /**
     * Find flow by UUID and tenant.
     */
    Optional<ConversationFlowEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Find all flows for a session.
     */
    List<ConversationFlowEntity> findBySessionIdAndTenantIdOrderByCreatedAtAsc(UUID sessionId, String tenantId);

    /**
     * Delete all flows for a session.
     */
    void deleteBySessionIdAndTenantId(UUID sessionId, String tenantId);
}
