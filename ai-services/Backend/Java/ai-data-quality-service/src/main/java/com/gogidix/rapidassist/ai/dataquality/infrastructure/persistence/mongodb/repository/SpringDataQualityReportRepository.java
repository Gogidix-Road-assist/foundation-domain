package com.gogidix.rapidassist.ai.dataquality.infrastructure.persistence.mongodb.repository;

import com.gogidix.rapidassist.ai.dataquality.infrastructure.persistence.mongodb.entity.DataQualityReportEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB repository for DataQualityReportEntity
 */
@Repository
public interface SpringDataQualityReportRepository extends MongoRepository<DataQualityReportEntity, UUID> {

    List<DataQualityReportEntity> findByTenantId(String tenantId);

    List<DataQualityReportEntity> findByTenantIdAndReportType(String tenantId, String reportType);

    @Query("{ 'tenantId': ?0, 'reportPeriodStart': { $gte: ?1 }, 'reportPeriodEnd': { $lte: ?2 } }")
    List<DataQualityReportEntity> findByTenantIdAndReportPeriodBetween(String tenantId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("{ 'tenantId': ?0 }")
    List<DataQualityReportEntity> findRecentReports(String tenantId);

    @Query("{ 'tenantId': ?0, 'reportType': ?1 }")
    Optional<DataQualityReportEntity> findLatestByTenantIdAndType(String tenantId, String reportType);

    @Query("{ 'tenantId': ?0, 'id': ?1 }")
    Optional<DataQualityReportEntity> findByTenantIdAndId(String tenantId, UUID id);

    @Query(value = "{ 'tenantId': ?0 }", count = true)
    long countByTenantId(String tenantId);
}
