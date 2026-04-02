package com.gogidix.rapidassist.ai.categorization.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.categorization.infrastructure.persistence.entity.CategoryEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataCategoryRepository extends MongoRepository<CategoryEntity, String> {

    List<CategoryEntity> findByTenantId(String tenantId);

    Optional<CategoryEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    Optional<CategoryEntity> findByCodeAndTenantId(String code, String tenantId);

    List<CategoryEntity> findByTenantIdAndParentCategoryIdIsNull(String tenantId);

    List<CategoryEntity> findByTenantIdAndParentCategoryId(String tenantId, UUID parentCategoryId);

    List<CategoryEntity> findByTenantIdAndStatus(String tenantId, String status);

    boolean existsByUuidAndTenantId(UUID uuid, String tenantId);

    long countByTenantId(String tenantId);

    void deleteByUuidAndTenantId(UUID uuid, String tenantId);
}
