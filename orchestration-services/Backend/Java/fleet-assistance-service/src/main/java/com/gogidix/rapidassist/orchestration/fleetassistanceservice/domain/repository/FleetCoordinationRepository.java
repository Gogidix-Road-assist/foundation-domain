package com.gogidix.rapidassist.orchestration.fleetassistanceservice.domain.repository;

import com.gogidix.rapidassist.orchestration.fleetassistanceservice.domain.model.FleetCoordination;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for FleetCoordination entities
 * ALL queries MUST filter by tenantId for multi-tenancy
 */
@Repository
public interface FleetCoordinationRepository extends MongoRepository<FleetCoordination, String> {

    // Find by tenant and coordination ID
    Optional<FleetCoordination> findByCoordinationIdAndTenantId(String coordinationId, String tenantId);

    // Find by tenant and fleet
    List<FleetCoordination> findByFleetIdAndTenantIdOrderByCoordinationDateDesc(String fleetId, String tenantId);

    // Find by tenant and fleet and date range
    List<FleetCoordination> findByFleetIdAndTenantIdAndCoordinationDateBetweenOrderByCoordinationDateDesc(
            String fleetId, String tenantId, Instant startDate, Instant endDate);

    // Find by tenant and coordination type
    List<FleetCoordination> findByCoordinationTypeAndTenantIdOrderByCoordinationDateDesc(String coordinationType, String tenantId);

    // Find by tenant
    List<FleetCoordination> findByTenantIdOrderByCoordinationDateDesc(String tenantId);

    // Find latest coordination by tenant and fleet
    Optional<FleetCoordination> findFirstByFleetIdAndTenantIdOrderByCoordinationDateDesc(String fleetId, String tenantId);
}
