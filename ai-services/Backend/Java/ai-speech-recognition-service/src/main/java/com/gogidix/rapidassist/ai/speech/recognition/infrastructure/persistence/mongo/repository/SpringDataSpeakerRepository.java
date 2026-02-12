package com.gogidix.rapidassist.ai.speech.recognition.infrastructure.persistence.mongo.repository;

import com.gogidix.rapidassist.ai.speech.recognition.infrastructure.persistence.mongo.entity.SpeakerEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB Repository for SpeakerEntity.
 */
@Repository
public interface SpringDataSpeakerRepository extends MongoRepository<SpeakerEntity, Long> {

    /**
     * Find entity by UUID and tenant.
     */
    Optional<SpeakerEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Find entities by speech recognition ID and tenant.
     */
    List<SpeakerEntity> findBySpeechRecognitionIdAndTenantId(UUID speechRecognitionId, String tenantId);

    /**
     * Find all entities by tenant.
     */
    List<SpeakerEntity> findByTenantId(String tenantId);

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
    Optional<SpeakerEntity> findByUuid(UUID uuid);

    /**
     * Delete entity by UUID only (for internal use).
     */
    void deleteByUuid(UUID uuid);
}
