package com.gogidix.rapidassist.ai.tagging.infrastructure.persistence.repository.impl;

import com.gogidix.rapidassist.ai.tagging.domain.model.ContentTag;
import com.gogidix.rapidassist.ai.tagging.domain.repository.ContentTagRepositoryPort;
import com.gogidix.rapidassist.ai.tagging.infrastructure.persistence.entity.ContentTagEntity;
import com.gogidix.rapidassist.ai.tagging.infrastructure.persistence.repository.SpringDataContentTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * MongoDB implementation of ContentTagRepositoryPort
 */
@Repository
@RequiredArgsConstructor
public class ContentTagRepositoryImpl implements ContentTagRepositoryPort {

    private final SpringDataContentTagRepository springDataRepository;

    @Override
    public ContentTag save(ContentTag contentTag) {
        ContentTagEntity entity = toEntity(contentTag);
        ContentTagEntity savedEntity = springDataRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<ContentTag> findById(UUID id) {
        return springDataRepository.findByUuid(id)
                .map(this::toDomain);
    }

    @Override
    public Optional<ContentTag> findByTenantIdAndId(String tenantId, UUID id) {
        ContentTagEntity entity = springDataRepository.findByTenantIdAndUuid(tenantId, id);
        return Optional.ofNullable(toDomain(entity));
    }

    @Override
    public List<ContentTag> findByTenantIdAndContentId(String tenantId, String contentId) {
        return springDataRepository.findByTenantIdAndContentId(tenantId, contentId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ContentTag> findByTenantIdAndContentIdAndContentType(String tenantId, String contentId, String contentType) {
        return springDataRepository.findByTenantIdAndContentIdAndContentType(tenantId, contentId, contentType).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ContentTag> findByTenantIdAndTagId(String tenantId, UUID tagId) {
        return springDataRepository.findByTenantIdAndTagId(tenantId, tagId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ContentTag> findByTenantIdAndTaggingSource(String tenantId, ContentTag.TaggingSource taggingSource) {
        return springDataRepository.findByTenantIdAndTaggingSource(tenantId, taggingSource).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ContentTag> findByTenantIdAndManuallyVerifiedFalse(String tenantId) {
        return springDataRepository.findByTenantIdAndManuallyVerifiedFalse(tenantId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByTenantIdAndContentIdAndTagId(String tenantId, String contentId, UUID tagId) {
        return springDataRepository.existsByTenantIdAndContentIdAndTagId(tenantId, contentId, tagId);
    }

    @Override
    public void deleteById(UUID id) {
        springDataRepository.deleteById(id.toString());
    }

    @Override
    public void deleteByTenantIdAndId(String tenantId, UUID id) {
        springDataRepository.deleteByTenantIdAndUuid(tenantId, id);
    }

    @Override
    public void deleteByTenantIdAndContentId(String tenantId, String contentId) {
        springDataRepository.deleteByTenantIdAndContentId(tenantId, contentId);
    }

    @Override
    public void deleteByTenantIdAndContentIdAndTagId(String tenantId, String contentId, UUID tagId) {
        springDataRepository.deleteByTenantIdAndContentIdAndTagId(tenantId, contentId, tagId);
    }

    @Override
    public long countByTenantIdAndContentId(String tenantId, String contentId) {
        return springDataRepository.countByTenantIdAndContentId(tenantId, contentId);
    }

    @Override
    public long countByTenantIdAndTagId(String tenantId, UUID tagId) {
        return springDataRepository.countByTenantIdAndTagId(tenantId, tagId);
    }

    private ContentTag toDomain(ContentTagEntity entity) {
        if (entity == null) return null;
        return ContentTag.builder()
                .id(entity.getUuid())
                .tenantId(entity.getTenantId())
                .contentId(entity.getContentId())
                .contentType(entity.getContentType())
                .tagId(entity.getTagId())
                .taggingSource(entity.getTaggingSource())
                .confidenceScore(entity.getConfidenceScore())
                .manuallyVerified(entity.getManuallyVerified())
                .taggedBy(entity.getTaggedBy())
                .taggingMetadata(entity.getTaggingMetadata())
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .version(entity.getVersion())
                .build();
    }

    private ContentTagEntity toEntity(ContentTag domain) {
        if (domain == null) return null;
        return ContentTagEntity.builder()
                .uuid(domain.getId())
                .tenantId(domain.getTenantId())
                .contentId(domain.getContentId())
                .contentType(domain.getContentType())
                .tagId(domain.getTagId())
                .taggingSource(domain.getTaggingSource())
                .confidenceScore(domain.getConfidenceScore())
                .manuallyVerified(domain.getManuallyVerified())
                .taggedBy(domain.getTaggedBy())
                .taggingMetadata(domain.getTaggingMetadata())
                .createdAt(domain.getCreatedAt())
                .createdBy(domain.getCreatedBy())
                .version(domain.getVersion())
                .build();
    }
}
