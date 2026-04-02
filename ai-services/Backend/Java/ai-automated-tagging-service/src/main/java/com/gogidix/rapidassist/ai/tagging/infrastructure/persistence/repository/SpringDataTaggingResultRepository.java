package com.gogidix.rapidassist.ai.tagging.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.tagging.infrastructure.persistence.entity.TaggingResultEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataTaggingResultRepository extends MongoRepository<TaggingResultEntity, String> {

    Optional<TaggingResultEntity> findByUuidAndTenantId(UUID uuid, String tenantId);
    List<TaggingResultEntity> findByTenantId(String tenantId);
    List<TaggingResultEntity> findByContentIdAndTenantId(String contentId, String tenantId);
    List<TaggingResultEntity> findByTagIdAndTenantId(UUID tagId, String tenantId);
    long countByTenantId(String tenantId);
    void deleteByUuidAndTenantId(UUID uuid, String tenantId);
}
