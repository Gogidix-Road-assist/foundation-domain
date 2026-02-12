package com.gogidix.rapidassist.orchestration.location.infrastructure.adapter.storage;

import com.gogidix.rapidassist.orchestration.location.domain.model.Route;
import com.gogidix.rapidassist.orchestration.location.domain.port.out.RouteRepositoryPort;
import com.gogidix.rapidassist.orchestration.location.infrastructure.persistence.mongo.RouteMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * MongoDB adapter for Route repository
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RouteRepositoryAdapter implements RouteRepositoryPort {

    private final RouteMongoRepository repository;

    @Override
    public Route save(Route route) {
        log.debug("Saving route: {}", route.getId());
        return repository.save(route);
    }

    @Override
    public Optional<Route> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public Optional<Route> findByReferenceId(String referenceId) {
        return repository.findByReferenceId(referenceId);
    }

    @Override
    public List<Route> findByTenantIdAndStatus(String tenantId, Route.RouteStatus status) {
        return repository.findByTenantIdAndStatus(tenantId, status);
    }

    @Override
    public List<Route> findExpiredRoutes(String tenantId) {
        return repository.findByTenantIdAndExpiresAtBefore(tenantId, LocalDateTime.now());
    }

    @Override
    public List<Route> findByTenantIdAndCreatedAtBetween(
            String tenantId,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        return repository.findByTenantIdAndCreatedAtBefore(tenantId, endTime)
                .stream()
                .filter(route -> route.getCreatedAt().isAfter(startTime))
                .toList();
    }

    @Override
    public void deleteRoutesOlderThan(String tenantId, LocalDateTime retentionDate) {
        repository.deleteByTenantIdAndCreatedAtBefore(tenantId, retentionDate);
    }

    @Override
    public void deleteExpiredRoutes(String tenantId) {
        repository.deleteByTenantIdAndExpiresAtBefore(tenantId, LocalDateTime.now());
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }
}
