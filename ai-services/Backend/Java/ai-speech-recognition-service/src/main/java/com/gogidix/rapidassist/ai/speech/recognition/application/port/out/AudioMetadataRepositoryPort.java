package com.gogidix.rapidassist.ai.speech.recognition.application.port.out;

import com.gogidix.rapidassist.ai.speech.recognition.domain.model.AudioMetadata;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for AudioMetadata operations.
 * Defines the contract for persistence operations.
 */
public interface AudioMetadataRepositoryPort {

    AudioMetadata save(AudioMetadata entity);
    Optional<AudioMetadata> findById(UUID id);
    Optional<AudioMetadata> findByIdAndTenantId(UUID id, String tenantId);
    Optional<AudioMetadata> findBySpeechRecognitionIdAndTenantId(UUID speechRecognitionId, String tenantId);
    List<AudioMetadata> findByTenantId(String tenantId);
    void deleteById(UUID id);
    void deleteByIdAndTenantId(UUID id, String tenantId);
    void deleteBySpeechRecognitionIdAndTenantId(UUID speechRecognitionId, String tenantId);
}
