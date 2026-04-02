package com.gogidix.rapidassist.ai.categorization.infrastructure.persistence.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.rapidassist.ai.categorization.application.port.out.CategoryRepositoryPort;
import com.gogidix.rapidassist.ai.categorization.domain.model.Category;
import com.gogidix.rapidassist.ai.categorization.domain.model.CategoryStatus;
import com.gogidix.rapidassist.ai.categorization.infrastructure.persistence.entity.CategoryEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepositoryPort {

    private final SpringDataCategoryRepository springDataRepository;
    private final ObjectMapper objectMapper;

    @Override
    public Category save(String tenantId, Category category) {
        log.info("Saving category: {} for tenant: {}", category.getId(), tenantId);
        CategoryEntity entity = toEntity(category);
        CategoryEntity saved = springDataRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Category> findById(String tenantId, UUID categoryId) {
        return springDataRepository.findByUuidAndTenantId(categoryId, tenantId)
                .map(this::toDomain);
    }

    @Override
    public Optional<Category> findByCode(String tenantId, String code) {
        return springDataRepository.findByCodeAndTenantId(code, tenantId)
                .map(this::toDomain);
    }

    @Override
    public List<Category> findByTenantId(String tenantId) {
        return springDataRepository.findByTenantId(tenantId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Category> findRootCategories(String tenantId) {
        return springDataRepository.findByTenantIdAndParentCategoryIdIsNull(tenantId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Category> findChildren(String tenantId, UUID parentCategoryId) {
        return springDataRepository.findByTenantIdAndParentCategoryId(tenantId, parentCategoryId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Category> findByStatus(String tenantId, String status) {
        return springDataRepository.findByTenantIdAndStatus(tenantId, status).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void delete(String tenantId, UUID categoryId) {
        springDataRepository.deleteByUuidAndTenantId(categoryId, tenantId);
    }

    @Override
    public boolean exists(String tenantId, UUID categoryId) {
        return springDataRepository.existsByUuidAndTenantId(categoryId, tenantId);
    }

    @Override
    public long countByTenantId(String tenantId) {
        return springDataRepository.countByTenantId(tenantId);
    }

    private CategoryEntity toEntity(Category domain) {
        return CategoryEntity.builder()
                .uuid(domain.getId())
                .tenantId(domain.getTenantId())
                .name(domain.getName())
                .code(domain.getCode())
                .description(domain.getDescription())
                .parentCategoryId(domain.getParentCategoryId())
                .level(domain.getLevel())
                .path(domain.getPath())
                .status(domain.getStatus() != null ? domain.getStatus().name() : null)
                .weight(domain.getWeight())
                .icon(domain.getIcon())
                .color(domain.getColor())
                .metadata(serializeMap(domain.getMetadata()))
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .createdBy(domain.getCreatedBy())
                .updatedBy(domain.getUpdatedBy())
                .build();
    }

    private Category toDomain(CategoryEntity entity) {
        return Category.builder()
                .id(entity.getUuid())
                .tenantId(entity.getTenantId())
                .name(entity.getName())
                .code(entity.getCode())
                .description(entity.getDescription())
                .parentCategoryId(entity.getParentCategoryId())
                .level(entity.getLevel())
                .path(entity.getPath())
                .status(entity.getStatus() != null ?
                    CategoryStatus.valueOf(entity.getStatus()) : null)
                .weight(entity.getWeight())
                .icon(entity.getIcon())
                .color(entity.getColor())
                .metadata(deserializeMap(entity.getMetadata()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .children(new java.util.ArrayList<>())
                .build();
    }

    private String serializeMap(java.util.Map<String, Object> map) {
        try {
            return map != null ? objectMapper.writeValueAsString(map) : null;
        } catch (Exception e) {
            log.error("Error serializing map", e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private java.util.Map<String, Object> deserializeMap(String json) {
        try {
            return json != null ? objectMapper.readValue(json, java.util.Map.class) : null;
        } catch (Exception e) {
            log.error("Error deserializing map", e);
            return null;
        }
    }
}
