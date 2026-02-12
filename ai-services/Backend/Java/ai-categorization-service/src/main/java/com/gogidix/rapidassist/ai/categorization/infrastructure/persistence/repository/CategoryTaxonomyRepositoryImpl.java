package com.gogidix.rapidassist.ai.categorization.infrastructure.persistence.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.rapidassist.ai.categorization.application.port.out.CategoryTaxonomyRepositoryPort;
import com.gogidix.rapidassist.ai.categorization.domain.model.CategoryTaxonomy;
import com.gogidix.rapidassist.ai.categorization.domain.model.TaxonomyType;
import com.gogidix.rapidassist.ai.categorization.domain.model.TaxonomyStatus;
import com.gogidix.rapidassist.ai.categorization.infrastructure.persistence.entity.CategoryTaxonomyEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CategoryTaxonomyRepositoryImpl implements CategoryTaxonomyRepositoryPort {

    private final SpringDataCategoryTaxonomyRepository springDataRepository;
    private final ObjectMapper objectMapper;

    @Override
    public CategoryTaxonomy save(String tenantId, CategoryTaxonomy taxonomy) {
        log.info("Saving taxonomy: {} for tenant: {}", taxonomy.getId(), tenantId);
        CategoryTaxonomyEntity entity = toEntity(taxonomy);
        CategoryTaxonomyEntity saved = springDataRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<CategoryTaxonomy> findById(String tenantId, UUID taxonomyId) {
        return springDataRepository.findByUuidAndTenantId(taxonomyId, tenantId)
                .map(this::toDomain);
    }

    @Override
    public Optional<CategoryTaxonomy> findByCode(String tenantId, String code) {
        return springDataRepository.findByCodeAndTenantId(code, tenantId)
                .map(this::toDomain);
    }

    @Override
    public List<CategoryTaxonomy> findByTenantId(String tenantId) {
        return springDataRepository.findByTenantId(tenantId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<CategoryTaxonomy> findByContentType(String tenantId, String contentType) {
        return springDataRepository.findByTenantIdAndContentType(tenantId, contentType).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<CategoryTaxonomy> findByStatus(String tenantId, String status) {
        return springDataRepository.findByTenantIdAndStatus(tenantId, status).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void delete(String tenantId, UUID taxonomyId) {
        springDataRepository.deleteByUuidAndTenantId(taxonomyId, tenantId);
    }

    @Override
    public boolean exists(String tenantId, UUID taxonomyId) {
        return springDataRepository.existsByUuidAndTenantId(taxonomyId, tenantId);
    }

    @Override
    public long countByTenantId(String tenantId) {
        return springDataRepository.countByTenantId(tenantId);
    }

    private CategoryTaxonomyEntity toEntity(CategoryTaxonomy domain) {
        return CategoryTaxonomyEntity.builder()
                .uuid(domain.getId())
                .tenantId(domain.getTenantId())
                .name(domain.getName())
                .code(domain.getCode())
                .description(domain.getDescription())
                .contentType(domain.getContentType())
                .type(domain.getType() != null ? domain.getType().name() : null)
                .status(domain.getStatus() != null ? domain.getStatus().name() : null)
                .rootCategoryId(domain.getRootCategoryId())
                .maxDepth(domain.getMaxDepth())
                .allowMultipleCategories(domain.isAllowMultipleCategories())
                .requireCategorization(domain.isRequireCategorization())
                .configuration(serializeMap(domain.getConfiguration()))
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .createdBy(domain.getCreatedBy())
                .updatedBy(domain.getUpdatedBy())
                .build();
    }

    private CategoryTaxonomy toDomain(CategoryTaxonomyEntity entity) {
        return CategoryTaxonomy.builder()
                .id(entity.getUuid())
                .tenantId(entity.getTenantId())
                .name(entity.getName())
                .code(entity.getCode())
                .description(entity.getDescription())
                .contentType(entity.getContentType())
                .type(entity.getType() != null ?
                    TaxonomyType.valueOf(entity.getType()) : null)
                .status(entity.getStatus() != null ?
                    TaxonomyStatus.valueOf(entity.getStatus()) : null)
                .rootCategoryId(entity.getRootCategoryId())
                .maxDepth(entity.getMaxDepth())
                .allowMultipleCategories(entity.getAllowMultipleCategories() != null ? 
                    entity.getAllowMultipleCategories() : false)
                .requireCategorization(entity.getRequireCategorization() != null ? 
                    entity.getRequireCategorization() : false)
                .configuration(deserializeMap(entity.getConfiguration()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
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
