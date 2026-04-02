package com.gogidix.rapidassist.ai.speech.recognition.application.port.out;

import com.gogidix.rapidassist.ai.speech.recognition.domain.model.SpeechRecognition;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for Speech Recognition operations.
 * Defines the contract for persistence operations.
 */
public interface SpeechRecognitionRepositoryPort {

    SpeechRecognition save(SpeechRecognition entity);
    Optional<SpeechRecognition> findById(UUID id);
    Optional<SpeechRecognition> findByIdAndTenantId(UUID id, String tenantId);
    Optional<SpeechRecognition> findByRequestIdAndTenantId(String requestId, String tenantId);
    List<SpeechRecognition> findByTenantId(String tenantId);
    List<SpeechRecognition> findByUserIdAndTenantId(String userId, String tenantId);
    List<SpeechRecognition> findByStatusAndTenantId(String status, String tenantId);
    void deleteById(UUID id);
    void deleteByIdAndTenantId(UUID id, String tenantId);
    boolean existsByIdAndTenantId(UUID id, String tenantId);
}
