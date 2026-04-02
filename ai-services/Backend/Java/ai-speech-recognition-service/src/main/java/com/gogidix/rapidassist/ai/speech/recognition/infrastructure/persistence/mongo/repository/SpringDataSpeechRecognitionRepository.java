package com.gogidix.rapidassist.ai.speech.recognition.infrastructure.persistence.mongo.repository;

import com.gogidix.rapidassist.ai.speech.recognition.infrastructure.persistence.mongo.entity.SpeechRecognitionEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB Repository for SpeechRecognitionEntity.
 */
@Repository
public interface SpringDataSpeechRecognitionRepository extends MongoRepository<SpeechRecognitionEntity, Long> {

    /**
     * Find entity by UUID and tenant.
     */
    Optional<SpeechRecognitionEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Find entity by request ID and tenant.
     */
    Optional<SpeechRecognitionEntity> findByRequestIdAndTenantId(String requestId, String tenantId);

    /**
     * Find all entities by tenant.
     */
    List<SpeechRecognitionEntity> findByTenantId(String tenantId);

    /**
     * Find entities by user ID and tenant.
     */
    List<SpeechRecognitionEntity> findByUserIdAndTenantId(String userId, String tenantId);

    /**
     * Find entities by status and tenant.
     */
    List<SpeechRecognitionEntity> findByStatusAndTenantId(String status, String tenantId);

    /**
     * Check if entity exists by UUID and tenant.
     */
    boolean existsByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Delete entity by UUID and tenant.
     */
    void deleteByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Find entity by UUID only (for internal use).
     */
    Optional<SpeechRecognitionEntity> findByUuid(UUID uuid);

    /**
     * Delete entity by UUID only (for internal use).
     */
    void deleteByUuid(UUID uuid);
}
