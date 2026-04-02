package com.gogidix.rapidassist.ai.tagging.infrastructure.persistence.repository.impl;

import com.gogidix.rapidassist.ai.tagging.domain.model.TagSuggestion;
import com.gogidix.rapidassist.ai.tagging.domain.repository.TagSuggestionRepositoryPort;
import com.gogidix.rapidassist.ai.tagging.infrastructure.persistence.entity.TagSuggestionEntity;
import com.gogidix.rapidassist.ai.tagging.infrastructure.persistence.repository.SpringDataTagSuggestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * MongoDB implementation of TagSuggestionRepositoryPort
 */
@Repository
@RequiredArgsConstructor
public class TagSuggestionRepositoryImpl implements TagSuggestionRepositoryPort {

    private final SpringDataTagSuggestionRepository springDataRepository;

    @Override
    public TagSuggestion save(TagSuggestion suggestion) {
        TagSuggestionEntity entity = toEntity(suggestion);
        TagSuggestionEntity savedEntity = springDataRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<TagSuggestion> findById(UUID id) {
        return springDataRepository.findByUuid(id)
                .map(this::toDomain);
    }

    @Override
    public Optional<TagSuggestion> findByTenantIdAndId(String tenantId, UUID id) {
        TagSuggestionEntity entity = springDataRepository.findByTenantIdAndUuid(tenantId, id);
        return Optional.ofNullable(toDomain(entity));
    }

    @Override
    public List<TagSuggestion> findByTenantIdAndContentId(String tenantId, String contentId) {
        return springDataRepository.findByTenantIdAndContentId(tenantId, contentId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<TagSuggestion> findByTenantIdAndContentIdAndStatus(String tenantId, String contentId, TagSuggestion.SuggestionStatus status) {
        return springDataRepository.findByTenantIdAndContentIdAndStatus(tenantId, contentId, status).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<TagSuggestion> findByTenantIdAndStatus(String tenantId, TagSuggestion.SuggestionStatus status) {
        return springDataRepository.findByTenantIdAndStatus(tenantId, status).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<TagSuggestion> findHighConfidenceSuggestions(String tenantId, double minConfidence) {
        return springDataRepository.findHighConfidenceSuggestions(tenantId, minConfidence).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<TagSuggestion> findExpiredSuggestions(String tenantId) {
        return springDataRepository.findExpiredSuggestions(tenantId, LocalDateTime.now()).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
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
    public void deleteExpiredSuggestions(String tenantId) {
        springDataRepository.deleteExpiredSuggestions(tenantId, LocalDateTime.now());
    }

    @Override
    public long countByTenantId(String tenantId) {
        return springDataRepository.countByTenantId(tenantId);
    }

    @Override
    public long countByTenantIdAndStatus(String tenantId, TagSuggestion.SuggestionStatus status) {
        return springDataRepository.countByTenantIdAndStatus(tenantId, status);
    }

    private TagSuggestion toDomain(TagSuggestionEntity entity) {
        if (entity == null) return null;
        return TagSuggestion.builder()
                .id(entity.getUuid())
                .tenantId(entity.getTenantId())
                .contentId(entity.getContentId())
                .contentType(entity.getContentType())
                .suggestedTagId(entity.getSuggestedTagId())
                .confidenceScore(entity.getConfidenceScore())
                .status(entity.getStatus())
                .suggestionMetadata(entity.getSuggestionMetadata())
                .aiModelUsed(entity.getAiModelUsed())
                .createdAt(entity.getCreatedAt())
                .expiresAt(entity.getExpiresAt())
                .createdBy(entity.getCreatedBy())
                .version(entity.getVersion())
                .build();
    }

    private TagSuggestionEntity toEntity(TagSuggestion domain) {
        if (domain == null) return null;
        return TagSuggestionEntity.builder()
                .uuid(domain.getId())
                .tenantId(domain.getTenantId())
                .contentId(domain.getContentId())
                .contentType(domain.getContentType())
                .suggestedTagId(domain.getSuggestedTagId())
                .confidenceScore(domain.getConfidenceScore())
                .status(domain.getStatus())
                .suggestionMetadata(domain.getSuggestionMetadata())
                .aiModelUsed(domain.getAiModelUsed())
                .createdAt(domain.getCreatedAt())
                .expiresAt(domain.getExpiresAt())
                .createdBy(domain.getCreatedBy())
                .version(domain.getVersion())
                .build();
    }
}
