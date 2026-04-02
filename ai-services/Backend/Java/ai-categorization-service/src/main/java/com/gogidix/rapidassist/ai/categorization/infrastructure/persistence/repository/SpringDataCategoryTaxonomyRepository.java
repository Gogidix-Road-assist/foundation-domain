package com.gogidix.rapidassist.ai.categorization.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.categorization.infrastructure.persistence.entity.CategoryTaxonomyEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataCategoryTaxonomyRepository extends MongoRepository<CategoryTaxonomyEntity, String> {

    List<CategoryTaxonomyEntity> findByTenantId(String tenantId);

    Optional<CategoryTaxonomyEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    Optional<CategoryTaxonomyEntity> findByCodeAndTenantId(String code, String tenantId);

    List<CategoryTaxonomyEntity> findByTenantIdAndContentType(String tenantId, String contentType);

    List<CategoryTaxonomyEntity> findByTenantIdAndStatus(String tenantId, String status);

    boolean existsByUuidAndTenantId(UUID uuid, String tenantId);

    long countByTenantId(String tenantId);

    void deleteByUuidAndTenantId(UUID uuid, String tenantId);
}
