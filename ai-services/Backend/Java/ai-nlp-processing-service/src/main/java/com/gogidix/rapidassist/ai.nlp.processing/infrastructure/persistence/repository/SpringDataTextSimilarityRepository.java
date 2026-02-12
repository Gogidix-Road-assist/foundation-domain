package com.gogidix.rapidassist.ai.nlp.processing.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.nlp.processing.infrastructure.persistence.entity.TextSimilarityEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataTextSimilarityRepository extends MongoRepository<TextSimilarityEntity, String> {
    Optional<TextSimilarityEntity> findByUuidAndTenantId(UUID uuid, String tenantId);
    List<TextSimilarityEntity> findByTextProcessingId(UUID textProcessingId);
    List<TextSimilarityEntity> findByTenantId(String tenantId);
}
