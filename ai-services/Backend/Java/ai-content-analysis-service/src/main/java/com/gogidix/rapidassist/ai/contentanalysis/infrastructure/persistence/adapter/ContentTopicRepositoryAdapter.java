package com.gogidix.rapidassist.ai.contentanalysis.infrastructure.persistence.adapter;

import com.gogidix.rapidassist.ai.contentanalysis.application.port.out.ContentTopicRepositoryPort;
import com.gogidix.rapidassist.ai.contentanalysis.domain.model.ContentTopic;
import com.gogidix.rapidassist.ai.contentanalysis.infrastructure.persistence.entity.ContentTopicEntity;
import com.gogidix.rapidassist.ai.contentanalysis.infrastructure.persistence.mapper.ContentTopicPersistenceMapper;
import com.gogidix.rapidassist.ai.contentanalysis.infrastructure.persistence.repository.ContentTopicMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * MongoDB adapter for ContentTopic repository
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class ContentTopicRepositoryAdapter implements ContentTopicRepositoryPort {

    private final ContentTopicMongoRepository mongoRepository;
    private final ContentTopicPersistenceMapper mapper;

    @Override
    public ContentTopic save(ContentTopic topic) {
        log.debug("Saving content topic: {}", topic.getId());
        ContentTopicEntity entity = mapper.toEntity(topic);
        ContentTopicEntity savedEntity = mongoRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public List<ContentTopic> saveAll(List<ContentTopic> topics) {
        log.debug("Saving {} content topics", topics.size());
        List<ContentTopicEntity> entities = topics.stream()
                .map(mapper::toEntity)
                .toList();
        List<ContentTopicEntity> savedEntities = mongoRepository.saveAll(entities);
        return savedEntities.stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<ContentTopic> findById(java.util.UUID id) {
        return mongoRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<ContentTopic> findByIdAndTenantId(java.util.UUID id, String tenantId) {
        return mongoRepository.findByIdAndTenantId(id, tenantId)
                .map(mapper::toDomain);
    }

    @Override
    public List<ContentTopic> findByContentId(String contentId) {
        return mongoRepository.findByContentId(contentId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<ContentTopic> findByContentIdAndTenantId(String contentId, String tenantId) {
        return mongoRepository.findByContentIdAndTenantId(contentId, tenantId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<ContentTopic> findByTenantId(String tenantId) {
        return mongoRepository.findByTenantId(tenantId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<ContentTopic> findByRelevanceScoreGreaterThanEqual(Double threshold) {
        return mongoRepository.findByRelevanceScoreGreaterThanEqual(threshold).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<ContentTopic> findByTopicCategory(String category) {
        return mongoRepository.findByTopicCategory(category).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteByContentId(String contentId) {
        mongoRepository.deleteByContentId(contentId);
    }

    @Override
    public void deleteByContentIdAndTenantId(String contentId, String tenantId) {
        mongoRepository.deleteByContentIdAndTenantId(contentId, tenantId);
    }

    @Override
    public void deleteById(java.util.UUID id) {
        mongoRepository.deleteById(id);
    }

    @Override
    public void deleteByIdAndTenantId(java.util.UUID id, String tenantId) {
        mongoRepository.deleteByIdAndTenantId(id, tenantId);
    }
}
