package com.gogidix.rapidassist.ai.gateway.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.gateway.application.port.out.RequestLogRepositoryPort;
import com.gogidix.rapidassist.ai.gateway.domain.model.RequestLog;
import com.gogidix.rapidassist.ai.gateway.infrastructure.persistence.entity.RequestLogEntity;
import com.gogidix.rapidassist.ai.gateway.infrastructure.persistence.mapper.RequestLogPersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of RequestLogRepositoryPort.
 * Adapters domain repository port to Spring Data MongoDB infrastructure.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class RequestLogRepositoryImpl implements RequestLogRepositoryPort {

    private final SpringDataRequestLogRepository springDataRepository;
    private final RequestLogPersistenceMapper persistenceMapper;

    @Override
    @Transactional
    public RequestLog save(RequestLog requestLog) {
        log.debug("Saving request log: {} for tenant: {}", requestLog.getId(), requestLog.getTenantId());

        RequestLogEntity entity = persistenceMapper.toEntity(requestLog);
        RequestLogEntity savedEntity = springDataRepository.save(entity);

        return persistenceMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RequestLog> findById(UUID id) {
        log.debug("Finding request log by ID: {}", id);

        return springDataRepository.findAll().stream()
                .filter(e -> e.getUuid().equals(id))
                .findFirst()
                .map(persistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RequestLog> findByIdAndTenantId(UUID id, String tenantId) {
        log.debug("Finding request log by ID: {} for tenant: {}", id, tenantId);

        return springDataRepository.findByUuidAndTenantId(id, tenantId)
                .map(persistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestLog> findByTenantId(String tenantId) {
        log.debug("Finding all request logs for tenant: {}", tenantId);

        return springDataRepository.findByTenantId(tenantId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestLog> findByTenantIdAndRequestId(String tenantId, String requestId) {
        log.debug("Finding request logs for tenant: {} and request: {}", tenantId, requestId);

        return springDataRepository.findByTenantIdAndRequestId(tenantId, requestId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestLog> findByTenantIdAndRouteId(String tenantId, String routeId) {
        log.debug("Finding request logs for tenant: {} and route: {}", tenantId, routeId);

        return springDataRepository.findByTenantIdAndRouteId(tenantId, routeId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestLog> findByTenantIdAndServiceId(String tenantId, String serviceId) {
        log.debug("Finding request logs for tenant: {} and service: {}", tenantId, serviceId);

        return springDataRepository.findByTenantIdAndServiceId(tenantId, serviceId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestLog> findByTenantIdAndCreatedAtBetween(String tenantId, LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Finding request logs for tenant: {} between {} and {}", tenantId, startDate, endDate);

        return springDataRepository.findByTenantIdAndCreatedAtBetween(tenantId, startDate, endDate).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestLog> findByTenantIdAndStatusCode(String tenantId, Integer statusCode) {
        log.debug("Finding request logs for tenant: {} with status: {}", tenantId, statusCode);

        return springDataRepository.findByTenantIdAndStatusCode(tenantId, statusCode).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestLog> findByTenantIdAndSuccess(String tenantId, Boolean success) {
        log.debug("Finding request logs for tenant: {} with success: {}", tenantId, success);

        return springDataRepository.findByTenantIdAndSuccess(tenantId, success).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        log.info("Deleting request log by ID: {}", id);

        springDataRepository.findAll().stream()
                .filter(e -> e.getUuid().equals(id))
                .findFirst()
                .ifPresent(entity -> springDataRepository.delete(entity));
    }

    @Override
    @Transactional
    public void deleteByIdAndTenantId(UUID id, String tenantId) {
        log.info("Deleting request log: {} for tenant: {}", id, tenantId);

        springDataRepository.deleteByUuidAndTenantId(id, tenantId);
    }

    @Override
    @Transactional
    public void deleteByTenantIdAndCreatedAtBefore(String tenantId, LocalDateTime date) {
        log.info("Deleting request logs for tenant: {} before {}", tenantId, date);

        springDataRepository.deleteByTenantIdAndCreatedAtBefore(tenantId, date);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByTenantId(String tenantId) {
        return springDataRepository.countByTenantId(tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByTenantIdAndSuccess(String tenantId, Boolean success) {
        return springDataRepository.countByTenantIdAndSuccess(tenantId, success);
    }
}
