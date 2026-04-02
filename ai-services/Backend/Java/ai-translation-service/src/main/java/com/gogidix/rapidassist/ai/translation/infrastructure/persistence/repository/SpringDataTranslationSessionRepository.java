package com.gogidix.rapidassist.ai.translation.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.translation.infrastructure.persistence.entity.TranslationSessionEntity;
import com.gogidix.rapidassist.ai.translation.domain.aggregate.TranslationSession;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB Repository for TranslationSessionEntity.
 */
public interface SpringDataTranslationSessionRepository extends MongoRepository<TranslationSessionEntity, String> {

    /**
     * Find session by UUID and tenant.
     */
    Optional<TranslationSessionEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Find session by session ID string and tenant.
     */
    Optional<TranslationSessionEntity> findBySessionIdAndTenantId(String sessionId, String tenantId);

    /**
     * Find all sessions by tenant.
     */
    List<TranslationSessionEntity> findByTenantId(String tenantId);

    /**
     * Find sessions by user ID and tenant.
     */
    List<TranslationSessionEntity> findByUserIdAndTenantId(String userId, String tenantId);

    /**
     * Find sessions by status and tenant.
     */
    List<TranslationSessionEntity> findByStatusAndTenantId(TranslationSession.SessionStatus status, String tenantId);

    /**
     * Find sessions by session type and tenant.
     */
    List<TranslationSessionEntity> findBySessionTypeAndTenantId(TranslationSession.SessionType sessionType, String tenantId);

    /**
     * Check if session exists.
     */
    boolean existsByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Delete session by UUID and tenant.
     */
    void deleteByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Count sessions by tenant.
     */
    long countByTenantId(String tenantId);

    /**
     * Find idle sessions (last activity before threshold) for tenant.
     */
    @Query("{ 'tenantId': ?0, 'lastActivityAt': { $lt: ?1 } }")
    List<TranslationSessionEntity> findIdleSessions(String tenantId, LocalDateTime threshold);
}
