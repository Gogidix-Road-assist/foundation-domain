package com.gogidix.rapidassist.ai.speech.recognition.infrastructure.persistence.mongo.repository;

import com.gogidix.rapidassist.ai.speech.recognition.infrastructure.persistence.mongo.entity.TranscriptionEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB Repository for TranscriptionEntity.
 */
@Repository
public interface SpringDataTranscriptionRepository extends MongoRepository<TranscriptionEntity, Long> {

    /**
     * Find entity by UUID and tenant.
     */
    Optional<TranscriptionEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Find entities by speech recognition ID and tenant.
     */
    List<TranscriptionEntity> findBySpeechRecognitionIdAndTenantId(UUID speechRecognitionId, String tenantId);

    /**
     * Find all entities by tenant.
     */
    List<TranscriptionEntity> findByTenantId(String tenantId);

    /**
     * Delete entities by speech recognition ID and tenant.
     */
    void deleteBySpeechRecognitionIdAndTenantId(UUID speechRecognitionId, String tenantId);

    /**
     * Delete entity by UUID and tenant.
     */
    void deleteByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Find entity by UUID only (for internal use).
     */
    Optional<TranscriptionEntity> findByUuid(UUID uuid);

    /**
     * Delete entity by UUID only (for internal use).
     */
    void deleteByUuid(UUID uuid);
}
