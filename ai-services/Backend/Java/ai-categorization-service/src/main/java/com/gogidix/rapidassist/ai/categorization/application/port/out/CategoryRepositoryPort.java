package com.gogidix.rapidassist.ai.categorization.application.port.out;

import com.gogidix.rapidassist.ai.categorization.domain.model.Category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for Category aggregate.
 */
public interface CategoryRepositoryPort {

    Category save(String tenantId, Category category);

    Optional<Category> findById(String tenantId, UUID categoryId);

    Optional<Category> findByCode(String tenantId, String code);

    List<Category> findByTenantId(String tenantId);

    List<Category> findRootCategories(String tenantId);

    List<Category> findChildren(String tenantId, UUID parentCategoryId);

    List<Category> findByStatus(String tenantId, String status);

    void delete(String tenantId, UUID categoryId);

    boolean exists(String tenantId, UUID categoryId);

    long countByTenantId(String tenantId);
}
