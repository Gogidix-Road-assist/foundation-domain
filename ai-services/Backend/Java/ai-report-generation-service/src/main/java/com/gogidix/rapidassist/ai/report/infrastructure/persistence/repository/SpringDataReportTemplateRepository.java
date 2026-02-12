package com.gogidix.rapidassist.ai.report.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.report.domain.model.TemplateType;
import com.gogidix.rapidassist.ai.report.infrastructure.persistence.entity.ReportTemplateEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB Repository for ReportTemplateEntity.
 */
@Repository
public interface SpringDataReportTemplateRepository extends MongoRepository<ReportTemplateEntity, String> {

    /**
     * Find template by UUID and tenant.
     */
    Optional<ReportTemplateEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Find all templates by tenant.
     */
    List<ReportTemplateEntity> findByTenantId(String tenantId);

    /**
     * Find active templates by tenant.
     */
    List<ReportTemplateEntity> findByTenantIdAndIsActiveTrue(String tenantId);

    /**
     * Find templates by type and tenant.
     */
    List<ReportTemplateEntity> findByTemplateTypeAndTenantId(TemplateType templateType, String tenantId);

    /**
     * Find templates by name pattern and tenant.
     */
    List<ReportTemplateEntity> findByTenantIdAndNameContainingIgnoreCase(String tenantId, String namePattern);

    /**
     * Check if template exists.
     */
    boolean existsByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Delete template by UUID and tenant.
     */
    void deleteByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Count templates by tenant.
     */
    long countByTenantId(String tenantId);
}
