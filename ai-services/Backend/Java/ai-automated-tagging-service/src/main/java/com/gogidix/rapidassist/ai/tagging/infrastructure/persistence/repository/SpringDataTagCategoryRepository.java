package com.gogidix.rapidassist.ai.tagging.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.tagging.infrastructure.persistence.entity.TagCategoryEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB repository for TagCategoryEntity
 */
@Repository
public interface SpringDataTagCategoryRepository extends MongoRepository<TagCategoryEntity, String> {

    List<TagCategoryEntity> findByTenantId(String tenantId);

    List<TagCategoryEntity> findByTenantIdAndStatus(String tenantId, com.gogidix.rapidassist.ai.tagging.domain.model.TagCategory.CategoryStatus status);

    List<TagCategoryEntity> findByTenantIdOrderByDisplayOrderAsc(String tenantId);

    boolean existsByTenantIdAndName(String tenantId, String name);

    @Query("{ 'uuid': ?0 }")
    Optional<TagCategoryEntity> findByUuid(UUID uuid);

    TagCategoryEntity findByTenantIdAndUuid(String tenantId, UUID uuid);

    void deleteByTenantIdAndUuid(String tenantId, UUID uuid);

    long countByTenantId(String tenantId);

    long countByTenantIdAndStatus(String tenantId, com.gogidix.rapidassist.ai.tagging.domain.model.TagCategory.CategoryStatus status);
}
