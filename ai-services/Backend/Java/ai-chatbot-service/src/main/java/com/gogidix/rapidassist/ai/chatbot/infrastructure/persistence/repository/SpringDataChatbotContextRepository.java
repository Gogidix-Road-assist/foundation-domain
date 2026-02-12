package com.gogidix.rapidassist.ai.chatbot.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.chatbot.infrastructure.persistence.entity.ChatbotContextEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB Repository for ChatbotContextEntity.
 */
public interface SpringDataChatbotContextRepository extends MongoRepository<ChatbotContextEntity, String> {

    /**
     * Find context by UUID and tenant.
     */
    Optional<ChatbotContextEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Find all contexts for a session.
     */
    List<ChatbotContextEntity> findBySessionIdAndTenantId(UUID sessionId, String tenantId);

    /**
     * Find context by key and session.
     */
    Optional<ChatbotContextEntity> findBySessionIdAndTenantIdAndContextKey(
            UUID sessionId, String tenantId, String contextKey);

    /**
     * Delete all contexts for a session.
     */
    void deleteBySessionIdAndTenantId(UUID sessionId, String tenantId);

    /**
     * Delete expired contexts.
     */
            @Query("DELETE FROM ChatbotContextEntity c WHERE c.tenantId = :tenantId " +
           "AND ((c.ttl IS NOT NULL AND c.updatedAt < :expiryTime) OR " +
           "(c.ttl IS NOT NULL AND (c.updatedAt + (c.ttl || ' seconds')::interval) < :now))")
    int deleteExpiredContexts(String tenantId,
                              LocalDateTime expiryTime,
                              LocalDateTime now);
}
