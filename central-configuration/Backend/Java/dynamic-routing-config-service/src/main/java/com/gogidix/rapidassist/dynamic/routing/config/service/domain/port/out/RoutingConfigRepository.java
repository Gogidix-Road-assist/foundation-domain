package com.gogidix.rapidassist.dynamic.routing.config.service.domain.port.out;

import com.gogidix.rapidassist.dynamic.routing.config.service.domain.model.RoutingConfig;
import java.util.List;
import java.util.Optional;

/**
 * Port interface for RoutingConfig repository operations
 */
public interface RoutingConfigRepository {
    
    Optional<RoutingConfig> findById(String id);
    
    List<RoutingConfig> findByTenantId(String tenantId);
    
    List<RoutingConfig> findByTenantIdAndActive(String tenantId, boolean active);
    
    List<RoutingConfig> findAll();
    
    RoutingConfig save(RoutingConfig routingConfig);
    
    void deleteById(String id);
    
    boolean existsByTenantIdAndRouteKey(String tenantId, String routeKey);
}
