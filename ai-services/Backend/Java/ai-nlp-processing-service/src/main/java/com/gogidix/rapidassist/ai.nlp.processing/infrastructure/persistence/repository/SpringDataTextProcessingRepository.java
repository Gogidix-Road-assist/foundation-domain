package com.gogidix.rapidassist.ai.nlp.processing.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.nlp.processing.infrastructure.persistence.entity.TextProcessingEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataTextProcessingRepository extends MongoRepository<TextProcessingEntity, String> {
    Optional<TextProcessingEntity> findByUuid(UUID uuid);
    Optional<TextProcessingEntity> findByUuidAndTenantId(UUID uuid, String tenantId);
    List<TextProcessingEntity> findByTenantId(String tenantId);
    List<TextProcessingEntity> findByStatusAndTenantId(com.gogidix.rapidassist.ai.nlp.processing.domain.model.TextProcessing.ProcessingStatus status, String tenantId);
    boolean existsByUuidAndTenantId(UUID uuid, String tenantId);
    void deleteByUuidAndTenantId(UUID uuid, String tenantId);
}
