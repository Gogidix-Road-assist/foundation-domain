package com.gogidix.rapidassist.analytics.domain.port.out;

import com.gogidix.rapidassist.analytics.domain.model.Analytics;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for Analytics operations.
 * Following hexagonal architecture principles.
 */
public interface AnalyticsRepositoryPort {

    Analytics save(String tenantId, Analytics analytics);

    Optional<Analytics> findById(String tenantId, UUID id);

    List<Analytics> findByTenantId(String tenantId);

    List<Analytics> findByTenantIdAndStatus(String tenantId, String status);

    List<Analytics> findByTenantIdAndAnalyticsType(String tenantId, String analyticsType);

    List<Analytics> findByTenantIdAndDataSource(String tenantId, String dataSource);

    List<Analytics> findByTenantIdAndTimeRange(String tenantId, LocalDateTime startTime, LocalDateTime endTime);

    boolean exists(String tenantId, UUID id);

    void delete(String tenantId, UUID id);

    List<Analytics> findPendingComputations(String tenantId, LocalDateTime before);
}
