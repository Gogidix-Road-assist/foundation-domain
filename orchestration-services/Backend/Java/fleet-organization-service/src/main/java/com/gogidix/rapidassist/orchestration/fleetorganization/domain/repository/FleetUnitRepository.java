package com.gogidix.rapidassist.orchestration.fleetorganization.domain.repository;

import com.gogidix.rapidassist.orchestration.fleetorganization.domain.model.FleetUnit;
import com.gogidix.rapidassist.orchestration.fleetorganization.domain.model.FleetUnit.UnitStatus;
import com.gogidix.rapidassist.orchestration.fleetorganization.domain.model.FleetUnit.UnitType;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository port for FleetUnit entity.
 * This is the interface that defines the contract for FleetUnit persistence operations.
 * Implemented by infrastructure layer (MongoDB).
 */
public interface FleetUnitRepository {

    /**
     * Save a fleet unit (create or update)
     */
    FleetUnit save(FleetUnit fleetUnit);

    /**
     * Find fleet unit by ID and tenant ID
     */
    Optional<FleetUnit> findByIdAndTenantId(String unitId, String tenantId);

    /**
     * Find all fleet units for a tenant
     */
    List<FleetUnit> findByTenantId(String tenantId);

    /**
     * Find fleet units by organization ID
     */
    List<FleetUnit> findByOrganizationIdAndTenantId(String organizationId, String tenantId);

    /**
     * Find fleet units by status
     */
    List<FleetUnit> findByStatusAndTenantId(UnitStatus status, String tenantId);

    /**
     * Find available units by type and tenant
     */
    List<FleetUnit> findByTypeAndStatusAndTenantId(UnitType type, UnitStatus status, String tenantId);

    /**
     * Find active units for tenant
     */
    List<FleetUnit> findByTenantIdAndIsActive(String tenantId, boolean isActive);

    /**
     * Find units by driver ID
     */
    List<FleetUnit> findByDriverIdAndTenantId(String driverId, String tenantId);

    /**
     * Find units by VIN
     */
    Optional<FleetUnit> findByVinAndTenantId(String vin, String tenantId);

    /**
     * Find units by license plate
     */
    Optional<FleetUnit> findByLicensePlateAndTenantId(String licensePlate, String tenantId);

    /**
     * Delete unit by ID and tenant
     */
    void deleteByIdAndTenantId(String unitId, String tenantId);

    /**
     * Check if unit exists
     */
    boolean existsByIdAndTenantId(String unitId, String tenantId);

    /**
     * Count units by organization
     */
    long countByOrganizationIdAndTenantId(String organizationId, String tenantId);

    /**
     * Count units by tenant
     */
    long countByTenantId(String tenantId);
}
