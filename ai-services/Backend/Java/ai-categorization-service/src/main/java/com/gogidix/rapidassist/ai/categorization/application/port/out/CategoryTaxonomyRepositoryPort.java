package com.gogidix.rapidassist.ai.categorization.application.port.out;

import com.gogidix.rapidassist.ai.categorization.domain.model.CategoryTaxonomy;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for CategoryTaxonomy aggregate.
 */
public interface CategoryTaxonomyRepositoryPort {

    CategoryTaxonomy save(String tenantId, CategoryTaxonomy taxonomy);

    Optional<CategoryTaxonomy> findById(String tenantId, UUID taxonomyId);

    Optional<CategoryTaxonomy> findByCode(String tenantId, String code);

    List<CategoryTaxonomy> findByTenantId(String tenantId);

    List<CategoryTaxonomy> findByContentType(String tenantId, String contentType);

    List<CategoryTaxonomy> findByStatus(String tenantId, String status);

    void delete(String tenantId, UUID taxonomyId);

    boolean exists(String tenantId, UUID taxonomyId);

    long countByTenantId(String tenantId);
}
