package com.gogidix.rapidassist.ai.nlp.processing.domain.port.out;

import com.gogidix.rapidassist.ai.nlp.processing.domain.model.TextProcessing;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TextProcessingRepositoryPort {
    TextProcessing save(TextProcessing entity);
    Optional<TextProcessing> findById(UUID id);
    Optional<TextProcessing> findByIdAndTenantId(UUID id, String tenantId);
    List<TextProcessing> findByTenantId(String tenantId);
    List<TextProcessing> findByStatus(String tenantId, TextProcessing.ProcessingStatus status);
    void deleteById(UUID id);
    void deleteByIdAndTenantId(UUID id, String tenantId);
    boolean existsByIdAndTenantId(UUID id, String tenantId);
}
