package com.gogidix.rapidassist.ai.chatbot.domain.repository;

import com.gogidix.rapidassist.ai.chatbot.domain.aggregate.ChatbotSession;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository Port (Hexagonal Architecture - Outbound Port).
 * Defines the contract for persisting and retrieving ChatbotSession aggregates.
 * Implementations are provided by the Infrastructure layer.
 */
public interface ChatbotSessionRepositoryPort {

    /**
     * Save a chatbot session (create or update).
     */
    ChatbotSession save(String tenantId, ChatbotSession session);

    /**
     * Find a session by ID and tenant.
     */
    Optional<ChatbotSession> findById(String tenantId, UUID sessionId);

    /**
     * Find a session by session ID string and tenant.
     */
    Optional<ChatbotSession> findBySessionId(String tenantId, String sessionId);

    /**
     * Find all sessions for a tenant.
     */
    List<ChatbotSession> findByTenantId(String tenantId);

    /**
     * Find sessions by user ID and tenant.
     */
    List<ChatbotSession> findByUserId(String tenantId, String userId);

    /**
     * Find sessions by status and tenant.
     */
    List<ChatbotSession> findByStatus(String tenantId, String status);

    /**
     * Find active sessions for a tenant.
     */
    List<ChatbotSession> findActiveSessions(String tenantId);

    /**
     * Find idle sessions (inactive for specified minutes).
     */
    List<ChatbotSession> findIdleSessions(String tenantId, int idleThresholdMinutes);

    /**
     * Delete a session by ID and tenant.
     */
    void delete(String tenantId, UUID sessionId);

    /**
     * Check if a session exists.
     */
    boolean exists(String tenantId, UUID sessionId);

    /**
     * Count sessions by tenant.
     */
    long countByTenantId(String tenantId);
}
