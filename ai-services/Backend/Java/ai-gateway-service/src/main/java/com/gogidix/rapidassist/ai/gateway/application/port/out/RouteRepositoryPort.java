package com.gogidix.rapidassist.ai.gateway.application.port.out;

import com.gogidix.rapidassist.ai.gateway.domain.model.Route;
import com.gogidix.rapidassist.ai.gateway.domain.model.RouteStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RouteRepositoryPort {
    Route save(Route entity);
    Optional<Route> findById(UUID id);
    Optional<Route> findByIdAndTenantId(UUID id, String tenantId);
    List<Route> findByTenantId(String tenantId);
    List<Route> findByTenantIdAndStatus(String tenantId, RouteStatus status);
    List<Route> findByServiceIdAndTenantId(String serviceId, String tenantId);
    Optional<Route> findByPathAndHttpMethodAndTenantId(String path, String httpMethod, String tenantId);
    List<Route> findByTenantIdAndStatusIn(String tenantId, List<RouteStatus> statuses);
    void deleteById(UUID id);
    void deleteByIdAndTenantId(UUID id, String tenantId);
    boolean existsByIdAndTenantId(UUID id, String tenantId);
    List<Route> findByTenantIdOrderByPriorityAsc(String tenantId);
}
