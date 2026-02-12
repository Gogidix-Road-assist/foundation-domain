package com.gogidix.rapidassist.ai.gateway.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.gateway.application.port.out.RouteRepositoryPort;
import com.gogidix.rapidassist.ai.gateway.domain.model.Route;
import com.gogidix.rapidassist.ai.gateway.domain.model.RouteStatus;
import com.gogidix.rapidassist.ai.gateway.infrastructure.persistence.entity.RouteEntity;
import com.gogidix.rapidassist.ai.gateway.infrastructure.persistence.mapper.RoutePersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of RouteRepositoryPort.
 * Adapters domain repository port to Spring Data MongoDB infrastructure.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class RouteRepositoryImpl implements RouteRepositoryPort {

    private final SpringDataRouteRepository springDataRepository;
    private final RoutePersistenceMapper persistenceMapper;

    @Override
    @Transactional
    public Route save(Route route) {
        log.info("Saving route: {} for tenant: {}", route.getId(), route.getTenantId());

        RouteEntity entity = persistenceMapper.toEntity(route);
        RouteEntity savedEntity = springDataRepository.save(entity);

        return persistenceMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Route> findById(UUID id) {
        log.debug("Finding route by ID: {}", id);

        return springDataRepository.findAll().stream()
                .filter(e -> e.getUuid().equals(id))
                .findFirst()
                .map(persistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Route> findByIdAndTenantId(UUID id, String tenantId) {
        log.debug("Finding route by ID: {} for tenant: {}", id, tenantId);

        return springDataRepository.findByUuidAndTenantId(id, tenantId)
                .map(persistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Route> findByTenantId(String tenantId) {
        log.debug("Finding all routes for tenant: {}", tenantId);

        return springDataRepository.findByTenantId(tenantId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Route> findByTenantIdAndStatus(String tenantId, RouteStatus status) {
        log.debug("Finding routes for tenant: {} with status: {}", tenantId, status);

        return springDataRepository.findByTenantIdAndStatus(tenantId, status).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Route> findByServiceIdAndTenantId(String serviceId, String tenantId) {
        log.debug("Finding routes for service: {} in tenant: {}", serviceId, tenantId);

        return springDataRepository.findByServiceIdAndTenantId(serviceId, tenantId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Route> findByPathAndHttpMethodAndTenantId(String path, String httpMethod, String tenantId) {
        log.debug("Finding route by path: {} and method: {} for tenant: {}", path, httpMethod, tenantId);

        return springDataRepository.findByPathAndHttpMethodAndTenantId(path, httpMethod, tenantId)
                .map(persistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Route> findByTenantIdAndStatusIn(String tenantId, List<RouteStatus> statuses) {
        log.debug("Finding routes for tenant: {} with statuses: {}", tenantId, statuses);

        return springDataRepository.findByTenantIdAndStatusIn(tenantId, statuses).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        log.info("Deleting route by ID: {}", id);

        springDataRepository.findAll().stream()
                .filter(e -> e.getUuid().equals(id))
                .findFirst()
                .ifPresent(entity -> springDataRepository.delete(entity));
    }

    @Override
    @Transactional
    public void deleteByIdAndTenantId(UUID id, String tenantId) {
        log.info("Deleting route: {} for tenant: {}", id, tenantId);

        springDataRepository.deleteByUuidAndTenantId(id, tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByIdAndTenantId(UUID id, String tenantId) {
        return springDataRepository.existsByUuidAndTenantId(id, tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Route> findByTenantIdOrderByPriorityAsc(String tenantId) {
        log.debug("Finding routes for tenant: {} ordered by priority", tenantId);

        return springDataRepository.findByTenantIdOrderByPriorityAsc(tenantId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }
}
