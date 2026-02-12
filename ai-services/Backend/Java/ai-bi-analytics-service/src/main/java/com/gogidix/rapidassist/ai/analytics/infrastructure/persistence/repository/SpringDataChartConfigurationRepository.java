package com.gogidix.rapidassist.ai.analytics.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.analytics.infrastructure.persistence.entity.ChartConfigurationEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data MongoDB repository for ChartConfigurationEntity
 */
@Repository
public interface SpringDataChartConfigurationRepository extends MongoRepository<ChartConfigurationEntity, String> {

    List<ChartConfigurationEntity> findByTenantId(String tenantId);

    List<ChartConfigurationEntity> findByDashboardId(String dashboardId);

    List<ChartConfigurationEntity> findByTenantIdAndIsActive(String tenantId, Boolean isActive);

    boolean existsById(String id);
}
