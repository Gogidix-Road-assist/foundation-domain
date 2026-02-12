package com.gogidix.rapidassist.orchestration.fleetassistanceservice.domain.repository;

import com.gogidix.rapidassist.orchestration.fleetassistanceservice.domain.model.AssistanceHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

/**
 * Repository interface for AssistanceHistory entities
 * ALL queries MUST filter by tenantId for multi-tenancy
 */
@Repository
public interface AssistanceHistoryRepository extends MongoRepository<AssistanceHistory, String> {

    // Find by tenant and request
    List<AssistanceHistory> findByRequestIdAndTenantId(String requestId, String tenantId);

    // Find by tenant and fleet
    List<AssistanceHistory> findByFleetIdAndTenantIdOrderByTimestampDesc(String fleetId, String tenantId);

    // Find by tenant and vehicle
    List<AssistanceHistory> findByVehicleIdAndTenantIdOrderByTimestampDesc(String vehicleId, String tenantId);

    // Find by tenant and service type
    List<AssistanceHistory> findByServiceTypeAndTenantIdOrderByTimestampDesc(String serviceType, String tenantId);

    // Find by tenant and action
    List<AssistanceHistory> findByActionAndTenantIdOrderByTimestampDesc(String action, String tenantId);

    // Find by tenant and date range
    List<AssistanceHistory> findByTenantIdAndTimestampBetweenOrderByTimestampDesc(String tenantId, Instant startDate, Instant endDate);

    // Count by tenant and fleet
    long countByFleetIdAndTenantId(String fleetId, String tenantId);
}
