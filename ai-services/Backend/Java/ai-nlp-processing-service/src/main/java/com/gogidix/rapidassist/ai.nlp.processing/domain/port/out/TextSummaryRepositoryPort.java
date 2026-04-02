package com.gogidix.rapidassist.ai.nlp.processing.domain.port.out;

import com.gogidix.rapidassist.ai.nlp.processing.domain.model.TextSummary;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TextSummaryRepositoryPort {
    TextSummary save(TextSummary entity);
    Optional<TextSummary> findById(UUID id);
    Optional<TextSummary> findByIdAndTenantId(UUID id, String tenantId);
    List<TextSummary> findByTextProcessingId(UUID textProcessingId);
    List<TextSummary> findByTenantId(String tenantId);
    void deleteById(UUID id);
}
