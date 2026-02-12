package com.gogidix.rapidassist.orchestration.fleetassistanceservice.domain.repository;

import com.gogidix.rapidassist.orchestration.fleetassistanceservice.domain.model.FleetRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for FleetRequest entities
 * ALL queries MUST filter by tenantId for multi-tenancy
 */
@Repository
public interface FleetRequestRepository extends MongoRepository<FleetRequest, String> {

    // Find by tenant and ID (for tenant isolation)
    Optional<FleetRequest> findByRequestIdAndTenantIdAndDeletedAtIsNull(String requestId, String tenantId);

    // Find all by tenant (excludes deleted)
    List<FleetRequest> findByTenantIdAndDeletedAtIsNull(String tenantId);

    // Find by tenant and fleet
    List<FleetRequest> findByFleetIdAndTenantIdAndDeletedAtIsNull(String fleetId, String tenantId);

    // Find by tenant and status
    List<FleetRequest> findByStatusAndTenantIdAndDeletedAtIsNull(FleetRequest.RequestStatus status, String tenantId);

    // Find by tenant, fleet, and status
    List<FleetRequest> findByFleetIdAndStatusAndTenantIdAndDeletedAtIsNull(String fleetId, FleetRequest.RequestStatus status, String tenantId);

    // Find by tenant and vehicle
    List<FleetRequest> findByVehicleIdAndTenantIdAndDeletedAtIsNull(String vehicleId, String tenantId);

    // Find by tenant and assigned provider
    List<FleetRequest> findByAssignedFleetProviderIdAndTenantIdAndDeletedAtIsNull(String providerId, String tenantId);

    // Find pending requests by tenant ordered by priority
    @Query("{ 'tenantId': ?0, 'status': 'PENDING', 'deletedAt': null }")
    List<FleetRequest> findPendingRequestsByTenantId(String tenantId);

    // Find emergency requests by tenant
    @Query("{ 'tenantId': ?0, 'priority': 'EMERGENCY', 'deletedAt': null }")
    List<FleetRequest> findEmergencyRequestsByTenantId(String tenantId);

    // Find requests by date range for tenant
    @Query("{ 'tenantId': ?0, 'createdAt': { $gte: ?1, $lte: ?2 }, 'deletedAt': null }")
    List<FleetRequest> findByTenantIdAndDateRange(String tenantId, Instant startDate, Instant endDate);

    // Count by tenant and status
    long countByStatusAndTenantIdAndDeletedAtIsNull(FleetRequest.RequestStatus status, String tenantId);

    // Count by tenant and fleet
    long countByFleetIdAndTenantIdAndDeletedAtIsNull(String fleetId, String tenantId);
}
