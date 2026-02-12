package com.gogidix.rapidassist.orchestration.fleetassistanceservice.domain.repository;

import com.gogidix.rapidassist.orchestration.fleetassistanceservice.domain.model.FleetProvider;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for FleetProvider entities
 * ALL queries MUST filter by tenantId for multi-tenancy
 */
@Repository
public interface FleetProviderRepository extends MongoRepository<FleetProvider, String> {

    // Find by tenant and provider ID
    Optional<FleetProvider> findByProviderIdAndTenantIdAndDeletedAtIsNull(String providerId, String tenantId);

    // Find all active providers by tenant
    List<FleetProvider> findByTenantIdAndIsActiveTrueAndDeletedAtIsNull(String tenantId);

    // Find by tenant and service types
    List<FleetProvider> findByServiceTypesContainingAndTenantIdAndIsActiveTrueAndDeletedAtIsNull(String serviceType, String tenantId);

    // Find by tenant
    List<FleetProvider> findByTenantIdAndDeletedAtIsNull(String tenantId);

    // Find providers by rating threshold
    List<FleetProvider> findByRatingGreaterThanEqualAndTenantIdAndIsActiveTrueAndDeletedAtIsNull(Double rating, String tenantId);

    // Find providers near location (geospatial query)
    @Query("{ 'tenantId': ?0, 'isActive': true, 'deletedAt': null, 'coverageArea.centerLatitude': { $gte: ?1, $lte: ?2 }, 'coverageArea.centerLongitude': { $gte: ?3, $lte: ?4 } }")
    List<FleetProvider> findProvidersInArea(String tenantId, Double minLat, Double maxLat, Double minLon, Double maxLon);
}
