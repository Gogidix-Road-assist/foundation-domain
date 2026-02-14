package com.gogidix.rapidassist.ai.speech.recognition.application.port.out;

import com.gogidix.rapidassist.ai.speech.recognition.domain.model.Speaker;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for Speaker operations.
 * Defines the contract for persistence operations.
 */
public interface SpeakerRepositoryPort {

    Speaker save(Speaker entity);
    Optional<Speaker> findById(UUID id);
    Optional<Speaker> findByIdAndTenantId(UUID id, String tenantId);
    List<Speaker> findBySpeechRecognitionIdAndTenantId(UUID speechRecognitionId, String tenantId);
    List<Speaker> findByTenantId(String tenantId);
    void deleteById(UUID id);
    void deleteByIdAndTenantId(UUID id, String tenantId);
    void deleteBySpeechRecognitionIdAndTenantId(UUID speechRecognitionId, String tenantId);
}
