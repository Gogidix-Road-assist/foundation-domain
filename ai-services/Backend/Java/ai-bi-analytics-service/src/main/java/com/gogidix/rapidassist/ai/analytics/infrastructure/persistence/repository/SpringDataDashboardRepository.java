package com.gogidix.rapidassist.ai.analytics.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.analytics.infrastructure.persistence.entity.DashboardEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data MongoDB repository for DashboardEntity
 */
@Repository
public interface SpringDataDashboardRepository extends MongoRepository<DashboardEntity, String> {

    List<DashboardEntity> findByTenantId(String tenantId);

    List<DashboardEntity> findByTenantIdAndIsPublic(String tenantId, Boolean isPublic);

    List<DashboardEntity> findByTenantIdAndIsActive(String tenantId, Boolean isActive);

    List<DashboardEntity> findByTenantIdAndCategory(String tenantId, String category);

    boolean existsById(String id);
}
