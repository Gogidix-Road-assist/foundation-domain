package com.gogidix.rapidassist.ai.nlp.processing.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.nlp.processing.infrastructure.persistence.entity.LanguageDetectionEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataLanguageDetectionRepository extends MongoRepository<LanguageDetectionEntity, String> {
    Optional<LanguageDetectionEntity> findByUuidAndTenantId(UUID uuid, String tenantId);
    List<LanguageDetectionEntity> findByTextProcessingId(UUID textProcessingId);
    List<LanguageDetectionEntity> findByTenantId(String tenantId);
}
