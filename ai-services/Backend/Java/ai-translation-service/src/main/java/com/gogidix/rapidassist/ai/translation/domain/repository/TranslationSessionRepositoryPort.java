package com.gogidix.rapidassist.ai.translation.domain.repository;

import com.gogidix.rapidassist.ai.translation.domain.aggregate.TranslationSession;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository Port (Hexagonal Architecture - Outbound Port).
 * Defines the contract for persisting and retrieving TranslationSession aggregates.
 * Implementations are provided by the Infrastructure layer.
 */
public interface TranslationSessionRepositoryPort {

    /**
     * Save a translation session (create or update).
     */
    TranslationSession save(String tenantId, TranslationSession session);

    /**
     * Find a session by ID and tenant.
     */
    Optional<TranslationSession> findById(String tenantId, UUID sessionId);

    /**
     * Find a session by session ID string and tenant.
     */
    Optional<TranslationSession> findBySessionId(String tenantId, String sessionId);

    /**
     * Find all sessions for a tenant.
     */
    List<TranslationSession> findByTenantId(String tenantId);

    /**
     * Find sessions by user ID and tenant.
     */
    List<TranslationSession> findByUserId(String tenantId, String userId);

    /**
     * Find sessions by status and tenant.
     */
    List<TranslationSession> findByStatus(String tenantId, String status);

    /**
     * Find active sessions for a tenant.
     */
    List<TranslationSession> findActiveSessions(String tenantId);

    /**
     * Find idle sessions (inactive for specified minutes).
     */
    List<TranslationSession> findIdleSessions(String tenantId, int idleThresholdMinutes);

    /**
     * Find sessions by session type and tenant.
     */
    List<TranslationSession> findBySessionType(String tenantId, String sessionType);

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
