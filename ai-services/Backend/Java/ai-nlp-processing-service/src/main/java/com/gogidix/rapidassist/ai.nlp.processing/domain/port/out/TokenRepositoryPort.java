package com.gogidix.rapidassist.ai.nlp.processing.domain.port.out;

import com.gogidix.rapidassist.ai.nlp.processing.domain.model.Token;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TokenRepositoryPort {
    Token save(Token entity);
    List<Token> saveAll(List<Token> entities);
    Optional<Token> findById(UUID id);
    List<Token> findByTextProcessingId(UUID textProcessingId);
    List<Token> findByTextProcessingIdOrderByPositionAsc(UUID textProcessingId);
    List<Token> findByTenantId(String tenantId);
    void deleteByTextProcessingId(UUID textProcessingId);
    void deleteById(UUID id);
}
