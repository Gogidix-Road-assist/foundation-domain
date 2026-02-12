package com.gogidix.rapidassist.ai.nlp.processing.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.nlp.processing.infrastructure.persistence.entity.TokenEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataTokenRepository extends MongoRepository<TokenEntity, String> {
    List<TokenEntity> findByTextProcessingId(UUID textProcessingId);
    List<TokenEntity> findByTextProcessingIdOrderByPositionAsc(UUID textProcessingId);
    List<TokenEntity> findByTenantId(String tenantId);
    void deleteByTextProcessingId(UUID textProcessingId);
}
