package com.gogidix.rapidassist.ai.tagging.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.tagging.domain.model.Tag;
import com.gogidix.rapidassist.ai.tagging.infrastructure.persistence.entity.TagEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataTagRepository extends MongoRepository<TagEntity, String> {

    Optional<TagEntity> findByUuid(UUID uuid);
    TagEntity findByTenantIdAndUuid(String tenantId, UUID uuid);
    List<TagEntity> findByTenantId(String tenantId);
    List<TagEntity> findByTenantIdAndStatus(String tenantId, Tag.TagStatus status);
    List<TagEntity> findByTenantIdAndCategoryId(String tenantId, UUID categoryId);
    List<TagEntity> findByTenantIdAndNameContainingIgnoreCase(String tenantId, String name);
    List<TagEntity> findMostUsedTags(String tenantId);
    boolean existsByTenantIdAndName(String tenantId, String name);
    void deleteByTenantIdAndUuid(String tenantId, UUID uuid);
    long countByTenantId(String tenantId);
    long countByTenantIdAndStatus(String tenantId, Tag.TagStatus status);
}
