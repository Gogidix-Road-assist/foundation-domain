package com.gogidix.rapidassist.ai.nlp.processing.domain.port.out;

import com.gogidix.rapidassist.ai.nlp.processing.domain.model.TextSimilarity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TextSimilarityRepositoryPort {
    TextSimilarity save(TextSimilarity entity);
    Optional<TextSimilarity> findById(UUID id);
    Optional<TextSimilarity> findByIdAndTenantId(UUID id, String tenantId);
    List<TextSimilarity> findByTextProcessingId(UUID textProcessingId);
    List<TextSimilarity> findByTenantId(String tenantId);
    void deleteById(UUID id);
}
