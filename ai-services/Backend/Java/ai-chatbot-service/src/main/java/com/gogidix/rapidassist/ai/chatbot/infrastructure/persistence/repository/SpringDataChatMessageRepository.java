package com.gogidix.rapidassist.ai.chatbot.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.chatbot.infrastructure.persistence.entity.ChatMessageEntity;
import com.gogidix.rapidassist.ai.chatbot.domain.model.MessageDirection;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB Repository for ChatMessageEntity.
 */
public interface SpringDataChatMessageRepository extends MongoRepository<ChatMessageEntity, String> {

    /**
     * Find message by UUID and tenant.
     */
    Optional<ChatMessageEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Find all messages for a session.
     */
    List<ChatMessageEntity> findBySessionIdAndTenantIdOrderBySequenceNumberAsc(UUID sessionId, String tenantId);

    /**
     * Find messages by direction for a session.
     */
    List<ChatMessageEntity> findBySessionIdAndTenantIdAndDirectionOrderBySequenceNumberAsc(
            UUID sessionId, String tenantId, MessageDirection direction);

    /**
     * Count messages for a session.
     */
    long countBySessionIdAndTenantId(UUID sessionId, String tenantId);

    /**
     * Delete all messages for a session.
     */
    void deleteBySessionIdAndTenantId(UUID sessionId, String tenantId);
}
