package com.gogidix.rapidassist.ai.gateway.application.port.out;

import com.gogidix.rapidassist.ai.gateway.domain.model.RequestLog;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RequestLogRepositoryPort {
    RequestLog save(RequestLog entity);
    Optional<RequestLog> findById(UUID id);
    Optional<RequestLog> findByIdAndTenantId(UUID id, String tenantId);
    List<RequestLog> findByTenantId(String tenantId);
    List<RequestLog> findByTenantIdAndRequestId(String tenantId, String requestId);
    List<RequestLog> findByTenantIdAndRouteId(String tenantId, String routeId);
    List<RequestLog> findByTenantIdAndServiceId(String tenantId, String serviceId);
    List<RequestLog> findByTenantIdAndCreatedAtBetween(String tenantId, LocalDateTime startDate, LocalDateTime endDate);
    List<RequestLog> findByTenantIdAndStatusCode(String tenantId, Integer statusCode);
    List<RequestLog> findByTenantIdAndSuccess(String tenantId, Boolean success);
    void deleteById(UUID id);
    void deleteByIdAndTenantId(UUID id, String tenantId);
    void deleteByTenantIdAndCreatedAtBefore(String tenantId, LocalDateTime date);
    long countByTenantId(String tenantId);
    long countByTenantIdAndSuccess(String tenantId, Boolean success);
}
