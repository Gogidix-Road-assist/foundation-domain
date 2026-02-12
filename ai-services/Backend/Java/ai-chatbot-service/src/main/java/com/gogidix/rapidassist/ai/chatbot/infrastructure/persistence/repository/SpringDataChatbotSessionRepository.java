package com.gogidix.rapidassist.ai.chatbot.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.chatbot.infrastructure.persistence.entity.ChatbotSessionEntity;
import com.gogidix.rapidassist.ai.chatbot.domain.model.SessionStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB Repository for ChatbotSessionEntity.
 */
public interface SpringDataChatbotSessionRepository extends MongoRepository<ChatbotSessionEntity, String> {

    /**
     * Find session by UUID and tenant.
     */
    Optional<ChatbotSessionEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Find session by session ID string and tenant.
     */
    Optional<ChatbotSessionEntity> findBySessionIdAndTenantId(String sessionId, String tenantId);

    /**
     * Find all sessions by tenant.
     */
    List<ChatbotSessionEntity> findByTenantId(String tenantId);
    ChatbotSessionEntity findByTenantIdAndRequestId(String tenantId, String requestId);
    List<ChatbotSessionEntity> findAllByTenantId(String tenantId);
    boolean existsByTenantIdAndRequestId(String tenantId, String requestId);
    long countByTenantId(String tenantId);
    void deleteByTenantIdAndRequestId(String tenantId, String requestId);

    /**
     * Find sessions by user ID and tenant.
     */
    List<ChatbotSessionEntity> findByUserIdAndTenantId(String userId, String tenantId);

    /**
     * Find sessions by status and tenant.
     */
    List<ChatbotSessionEntity> findByStatusAndTenantId(SessionStatus status, String tenantId);

    /**
     * Find active sessions (status = ACTIVE) for tenant.
     * Note: Complex query removed - use service layer filtering
     */
    List<ChatbotSessionEntity> findByTenantIdAndStatusIn(String tenantId, List<SessionStatus> statuses);

    /**
     * Check if session exists.
     */
    boolean existsByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Delete session by UUID and tenant.
     */
    void deleteByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Find idle sessions (last activity before threshold) for tenant.
     */
    @Query("{ 'tenantId': ?0, 'lastActivityAt': { $lt: ?1 } }")
    List<ChatbotSessionEntity> findIdleSessions(String tenantId, LocalDateTime threshold);
}
