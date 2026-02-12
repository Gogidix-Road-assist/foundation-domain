package com.gogidix.rapidassist.ai.dataquality.domain.repository;

import com.gogidix.rapidassist.ai.dataquality.domain.model.DataQualityReport;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for DataQualityReport aggregate
 */
public interface DataQualityReportRepositoryPort {

    DataQualityReport save(DataQualityReport report);

    Optional<DataQualityReport> findById(String tenantId, UUID id);

    List<DataQualityReport> findByTenantId(String tenantId);

    List<DataQualityReport> findByTenantIdAndReportType(String tenantId, String reportType);

    List<DataQualityReport> findByTenantIdAndReportPeriodBetween(
            String tenantId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    List<DataQualityReport> findRecentReportsByTenantId(String tenantId, int limit);

    Optional<DataQualityReport> findLatestReportByTenantIdAndType(String tenantId, String reportType);

    void deleteById(String tenantId, UUID id);

    long countByTenantId(String tenantId);
}
