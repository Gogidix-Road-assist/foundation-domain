package com.gogidix.rapidassist.ai.speech.recognition.application.port.out;

import com.gogidix.rapidassist.ai.speech.recognition.domain.model.Transcription;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for Transcription operations.
 * Defines the contract for persistence operations.
 */
public interface TranscriptionRepositoryPort {

    Transcription save(Transcription entity);
    Optional<Transcription> findById(UUID id);
    Optional<Transcription> findByIdAndTenantId(UUID id, String tenantId);
    List<Transcription> findBySpeechRecognitionIdAndTenantId(UUID speechRecognitionId, String tenantId);
    List<Transcription> findByTenantId(String tenantId);
    void deleteById(UUID id);
    void deleteByIdAndTenantId(UUID id, String tenantId);
    void deleteBySpeechRecognitionIdAndTenantId(UUID speechRecognitionId, String tenantId);
}
