package com.gogidix.rapidassist.ai.tagging.infrastructure.persistence.repository.impl;

import com.gogidix.rapidassist.ai.tagging.domain.model.TagCategory;
import com.gogidix.rapidassist.ai.tagging.domain.repository.TagCategoryRepositoryPort;
import com.gogidix.rapidassist.ai.tagging.infrastructure.persistence.entity.TagCategoryEntity;
import com.gogidix.rapidassist.ai.tagging.infrastructure.persistence.repository.SpringDataTagCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * MongoDB implementation of TagCategoryRepositoryPort
 */
@Repository
@RequiredArgsConstructor
public class TagCategoryRepositoryImpl implements TagCategoryRepositoryPort {

    private final SpringDataTagCategoryRepository springDataRepository;

    @Override
    public TagCategory save(TagCategory category) {
        TagCategoryEntity entity = toEntity(category);
        TagCategoryEntity savedEntity = springDataRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<TagCategory> findById(UUID id) {
        return springDataRepository.findByUuid(id)
                .map(this::toDomain);
    }

    @Override
    public Optional<TagCategory> findByTenantIdAndId(String tenantId, UUID id) {
        TagCategoryEntity entity = springDataRepository.findByTenantIdAndUuid(tenantId, id);
        return Optional.ofNullable(toDomain(entity));
    }

    @Override
    public List<TagCategory> findByTenantId(String tenantId) {
        return springDataRepository.findByTenantId(tenantId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<TagCategory> findByTenantIdAndStatus(String tenantId, TagCategory.CategoryStatus status) {
        return springDataRepository.findByTenantIdAndStatus(tenantId, status).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<TagCategory> findByTenantIdOrderByDisplayOrderAsc(String tenantId) {
        return springDataRepository.findByTenantIdOrderByDisplayOrderAsc(tenantId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByTenantIdAndName(String tenantId, String name) {
        return springDataRepository.existsByTenantIdAndName(tenantId, name);
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
    public long countByTenantId(String tenantId) {
        return springDataRepository.countByTenantId(tenantId);
    }

    @Override
    public long countByTenantIdAndStatus(String tenantId, TagCategory.CategoryStatus status) {
        return springDataRepository.countByTenantIdAndStatus(tenantId, status);
    }

    private TagCategory toDomain(TagCategoryEntity entity) {
        if (entity == null) return null;
        return TagCategory.builder()
                .id(entity.getUuid())
                .tenantId(entity.getTenantId())
                .name(entity.getName())
                .description(entity.getDescription())
                .icon(entity.getIcon())
                .displayOrder(entity.getDisplayOrder())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .version(entity.getVersion())
                .build();
    }

    private TagCategoryEntity toEntity(TagCategory domain) {
        if (domain == null) return null;
        return TagCategoryEntity.builder()
                .uuid(domain.getId())
                .tenantId(domain.getTenantId())
                .name(domain.getName())
                .description(domain.getDescription())
                .icon(domain.getIcon())
                .displayOrder(domain.getDisplayOrder())
                .status(domain.getStatus())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .createdBy(domain.getCreatedBy())
                .updatedBy(domain.getUpdatedBy())
                .version(domain.getVersion())
                .build();
    }
}
