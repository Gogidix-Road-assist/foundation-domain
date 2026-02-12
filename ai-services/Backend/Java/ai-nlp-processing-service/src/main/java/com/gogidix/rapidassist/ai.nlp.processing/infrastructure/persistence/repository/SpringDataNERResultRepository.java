package com.gogidix.rapidassist.ai.nlp.processing.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.nlp.processing.infrastructure.persistence.entity.NERResultEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataNERResultRepository extends MongoRepository<NERResultEntity, String> {
    Optional<NERResultEntity> findByUuidAndTenantId(UUID uuid, String tenantId);
    List<NERResultEntity> findByTextProcessingId(UUID textProcessingId);
    List<NERResultEntity> findByTenantId(String tenantId);
}
