package com.gogidix.rapidassist.ai.tagging.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.tagging.infrastructure.persistence.entity.ContentTagEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB repository for ContentTagEntity
 */
@Repository
public interface SpringDataContentTagRepository extends MongoRepository<ContentTagEntity, String> {

    List<ContentTagEntity> findByTenantIdAndContentId(String tenantId, String contentId);

    List<ContentTagEntity> findByTenantIdAndContentIdAndContentType(String tenantId, String contentId, String contentType);

    List<ContentTagEntity> findByTenantIdAndTagId(String tenantId, UUID tagId);

    List<ContentTagEntity> findByTenantIdAndTaggingSource(String tenantId, com.gogidix.rapidassist.ai.tagging.domain.model.ContentTag.TaggingSource taggingSource);

    List<ContentTagEntity> findByTenantIdAndManuallyVerifiedFalse(String tenantId);

    boolean existsByTenantIdAndContentIdAndTagId(String tenantId, String contentId, UUID tagId);

    @Query("{ 'uuid': ?0 }")
    Optional<ContentTagEntity> findByUuid(UUID uuid);

    ContentTagEntity findByTenantIdAndUuid(String tenantId, UUID uuid);

    void deleteByTenantIdAndUuid(String tenantId, UUID uuid);

    void deleteByTenantIdAndContentId(String tenantId, String contentId);

    void deleteByTenantIdAndContentIdAndTagId(String tenantId, String contentId, UUID tagId);

    long countByTenantIdAndContentId(String tenantId, String contentId);

    long countByTenantIdAndTagId(String tenantId, UUID tagId);
}
