package com.gogidix.rapidassist.api.gateway.infrastructure.persistence.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GatewayRouteRepository extends MongoRepository<GatewayRouteDocument, String> {

    Optional<GatewayRouteDocument> findByTenantIdAndRouteId(String tenantId, String routeId);

    List<GatewayRouteDocument> findByTenantId(String tenantId);

    List<GatewayRouteDocument> findByTenantIdAndServiceId(String tenantId, String serviceId);

    List<GatewayRouteDocument> findByTenantIdAndPathRegex(String tenantId, String pathPattern);

    List<GatewayRouteDocument> findByTenantIdAndStatus(String tenantId, String status);

    List<GatewayRouteDocument> findByTenantIdAndEnabled(String tenantId, boolean enabled);

    List<GatewayRouteDocument> findByTenantIdAndTagsIn(String tenantId, List<String> tags);

    @Query("{'tenantId': ?0, $or: [{'routeId': {$regex: ?1, $options: 'i'}}, {'path': {$regex: ?1, $options: 'i'}}]}")
    List<GatewayRouteDocument> searchByTenantIdAndKeyword(String tenantId, String keyword);

    List<GatewayRouteDocument> findByTenantIdOrderByOrderAsc(String tenantId);
}
