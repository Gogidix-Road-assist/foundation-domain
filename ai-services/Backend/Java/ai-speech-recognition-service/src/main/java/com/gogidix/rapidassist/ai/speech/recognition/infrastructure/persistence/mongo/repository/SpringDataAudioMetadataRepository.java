package com.gogidix.rapidassist.ai.speech.recognition.infrastructure.persistence.mongo.repository;

import com.gogidix.rapidassist.ai.speech.recognition.infrastructure.persistence.mongo.entity.AudioMetadataEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB Repository for AudioMetadataEntity.
 */
@Repository
public interface SpringDataAudioMetadataRepository extends MongoRepository<AudioMetadataEntity, Long> {

    /**
     * Find entity by UUID and tenant.
     */
    Optional<AudioMetadataEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Find entity by speech recognition ID and tenant.
     */
    Optional<AudioMetadataEntity> findBySpeechRecognitionIdAndTenantId(UUID speechRecognitionId, String tenantId);

    /**
     * Find all entities by tenant.
     */
    List<AudioMetadataEntity> findByTenantId(String tenantId);

    /**
     * Delete entity by speech recognition ID and tenant.
     */
    void deleteBySpeechRecognitionIdAndTenantId(UUID speechRecognitionId, String tenantId);

    /**
     * Delete entity by UUID and tenant.
     */
    void deleteByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Find entity by UUID only (for internal use).
     */
    Optional<AudioMetadataEntity> findByUuid(UUID uuid);

    /**
     * Delete entity by UUID only (for internal use).
     */
    void deleteByUuid(UUID uuid);
}
