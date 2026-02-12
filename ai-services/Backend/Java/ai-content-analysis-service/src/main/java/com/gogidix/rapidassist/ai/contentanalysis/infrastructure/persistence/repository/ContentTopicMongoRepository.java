package com.gogidix.rapidassist.ai.contentanalysis.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.contentanalysis.infrastructure.persistence.entity.ContentTopicEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB repository for ContentTopicEntity
 */
@Repository
public interface ContentTopicMongoRepository extends MongoRepository<ContentTopicEntity, UUID> {

    Optional<ContentTopicEntity> findByIdAndTenantId(UUID id, String tenantId);
    List<ContentTopicEntity> findByContentId(String contentId);
    List<ContentTopicEntity> findByContentIdAndTenantId(String contentId, String tenantId);
    List<ContentTopicEntity> findByTenantId(String tenantId);
    List<ContentTopicEntity> findByRelevanceScoreGreaterThanEqual(Double threshold);
    List<ContentTopicEntity> findByTopicCategory(String category);
    void deleteByContentId(String contentId);
    void deleteByContentIdAndTenantId(String contentId, String tenantId);
    void deleteByIdAndTenantId(UUID id, String tenantId);
}
