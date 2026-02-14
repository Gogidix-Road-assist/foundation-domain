package com.gogidix.rapidassist.ai.nlp.processing.domain.port.out;

import com.gogidix.rapidassist.ai.nlp.processing.domain.model.LanguageDetection;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LanguageDetectionRepositoryPort {
    LanguageDetection save(LanguageDetection entity);
    Optional<LanguageDetection> findById(UUID id);
    Optional<LanguageDetection> findByIdAndTenantId(UUID id, String tenantId);
    List<LanguageDetection> findByTextProcessingId(UUID textProcessingId);
    List<LanguageDetection> findByTenantId(String tenantId);
    void deleteById(UUID id);
}
