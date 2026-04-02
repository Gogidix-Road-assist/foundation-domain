package com.gogidix.rapidassist.ai.nlp.processing.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.nlp.processing.infrastructure.persistence.entity.TextSummaryEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataTextSummaryRepository extends MongoRepository<TextSummaryEntity, String> {
    Optional<TextSummaryEntity> findByUuidAndTenantId(UUID uuid, String tenantId);
    List<TextSummaryEntity> findByTextProcessingId(UUID textProcessingId);
    List<TextSummaryEntity> findByTenantId(String tenantId);
}
