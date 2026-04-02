package com.gogidix.rapidassist.ai.analytics.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.analytics.infrastructure.persistence.entity.AnalyticsReportEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data MongoDB repository for AnalyticsReportEntity
 */
@Repository
public interface SpringDataAnalyticsReportRepository extends MongoRepository<AnalyticsReportEntity, String> {

    List<AnalyticsReportEntity> findByTenantId(String tenantId);

    List<AnalyticsReportEntity> findByTenantIdAndStatus(String tenantId, String status);

    List<AnalyticsReportEntity> findByTenantIdAndReportType(String tenantId, String reportType);

    boolean existsById(String id);
}
