package com.gogidix.rapidassist.ai.tagging.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.tagging.infrastructure.persistence.entity.TagDefinitionEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataTagDefinitionRepository extends MongoRepository<TagDefinitionEntity, String> {

    Optional<TagDefinitionEntity> findByUuidAndTenantId(UUID uuid, String tenantId);
    List<TagDefinitionEntity> findByTenantId(String tenantId);
    List<TagDefinitionEntity> findByTenantIdAndCategory(String tenantId, String category);
    List<TagDefinitionEntity> findByTenantIdAndIsAutoApply(String tenantId, Boolean isAutoApply);
    void deleteByUuidAndTenantId(UUID uuid, String tenantId);
}
