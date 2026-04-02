package com.gogidix.rapidassist.ai.tagging.infrastructure.persistence.repository.impl;

import com.gogidix.rapidassist.ai.tagging.domain.model.Tag;
import com.gogidix.rapidassist.ai.tagging.domain.model.TagCategory;
import com.gogidix.rapidassist.ai.tagging.domain.repository.TagRepositoryPort;
import com.gogidix.rapidassist.ai.tagging.infrastructure.persistence.entity.TagEntity;
import com.gogidix.rapidassist.ai.tagging.infrastructure.persistence.repository.SpringDataTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * MongoDB implementation of TagRepositoryPort
 */
@Repository
@RequiredArgsConstructor
public class TagRepositoryImpl implements TagRepositoryPort {

    private final SpringDataTagRepository springDataTagRepository;

    @Override
    public Tag save(Tag tag) {
        TagEntity entity = toEntity(tag);
        TagEntity savedEntity = springDataTagRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Tag> findById(UUID id) {
        return springDataTagRepository.findByUuid(id)
                .map(this::toDomain);
    }

    @Override
    public Optional<Tag> findByTenantIdAndId(String tenantId, UUID id) {
        TagEntity entity = springDataTagRepository.findByTenantIdAndUuid(tenantId, id);
        return Optional.ofNullable(toDomain(entity));
    }

    @Override
    public List<Tag> findByTenantId(String tenantId) {
        return springDataTagRepository.findByTenantId(tenantId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Tag> findByTenantIdAndStatus(String tenantId, Tag.TagStatus status) {
        return springDataTagRepository.findByTenantIdAndStatus(tenantId, status).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Tag> findByTenantIdAndCategoryId(String tenantId, UUID categoryId) {
        return springDataTagRepository.findByTenantIdAndCategoryId(tenantId, categoryId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Tag> findByTenantIdAndNameContainingIgnoreCase(String tenantId, String name) {
        return springDataTagRepository.findByTenantIdAndNameContainingIgnoreCase(tenantId, name).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Tag> findMostUsedTags(String tenantId, int limit) {
        return springDataTagRepository.findMostUsedTags(tenantId).stream()
                .limit(limit)
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByTenantIdAndName(String tenantId, String name) {
        return springDataTagRepository.existsByTenantIdAndName(tenantId, name);
    }

    @Override
    public void deleteById(UUID id) {
        springDataTagRepository.deleteById(id.toString());
    }

    @Override
    public void deleteByTenantIdAndId(String tenantId, UUID id) {
        springDataTagRepository.deleteByTenantIdAndUuid(tenantId, id);
    }

    @Override
    public long countByTenantId(String tenantId) {
        return springDataTagRepository.countByTenantId(tenantId);
    }

    @Override
    public long countByTenantIdAndStatus(String tenantId, Tag.TagStatus status) {
        return springDataTagRepository.countByTenantIdAndStatus(tenantId, status);
    }

    @Override
    public long countByTenantIdAndTagId(String tenantId, UUID tagId) {
        // This would be used to count content-tag associations
        // For now, return 0 as it's not directly supported by TagEntity
        return 0;
    }

    // ============================================================
    // Mappers
    // ============================================================

    private Tag toDomain(TagEntity entity) {
        if (entity == null) {
            return null;
        }

        // For now, we'll set category to null since TagCategory is now a separate entity
        // that should be loaded separately if needed
        return Tag.builder()
                .id(entity.getUuid())
                .tenantId(entity.getTenantId())
                .name(entity.getName())
                .description(entity.getDescription())
                .color(entity.getColor())
                .category(null) // Category should be loaded separately if needed
                .status(entity.getStatus())
                .usageCount(entity.getUsageCount())
                .version(entity.getVersion())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    private TagEntity toEntity(Tag domain) {
        if (domain == null) {
            return null;
        }

        // Store category as string reference (UUID or name)
        String categoryValue = null;
        if (domain.getCategory() != null && domain.getCategory().getId() != null) {
            categoryValue = domain.getCategory().getId().toString();
        } else if (domain.getCategory() != null && domain.getCategory().getName() != null) {
            categoryValue = domain.getCategory().getName();
        }

        return TagEntity.builder()
                .uuid(domain.getId())
                .tenantId(domain.getTenantId())
                .name(domain.getName())
                .description(domain.getDescription())
                .color(domain.getColor())
                .category(categoryValue)
                .status(domain.getStatus())
                .usageCount(domain.getUsageCount())
                .version(domain.getVersion())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .createdBy(domain.getCreatedBy())
                .updatedBy(domain.getUpdatedBy())
                .build();
    }
}
