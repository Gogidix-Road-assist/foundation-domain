package com.gogidix.rapidassist.api.gateway.infrastructure.persistence.mongodb;

import com.gogidix.rapidassist.api.gateway.domain.model.GatewayRoute;
import com.gogidix.rapidassist.api.gateway.domain.port.out.GatewayRouteStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Component
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "gateway.mongodb.enabled", havingValue = "true", matchIfMissing = false)
public class MongoGatewayRouteStore implements GatewayRouteStore {

    private static final Logger logger = LoggerFactory.getLogger(MongoGatewayRouteStore.class);

    @Autowired
    private GatewayRouteRepository repository;

    @Override
    public CompletableFuture<GatewayRoute> save(GatewayRoute route) {
        return CompletableFuture.supplyAsync(() -> {
            GatewayRouteDocument document = GatewayRouteDocument.fromDomain(route);
            GatewayRouteDocument saved = repository.save(document);
            logger.debug("Saved gateway route: {}", saved.routeId());
            return saved.toDomain();
        });
    }

    @Override
    public CompletableFuture<Optional<GatewayRoute>> findById(String id) {
        return CompletableFuture.supplyAsync(() -> {
            return repository.findById(id)
                .map(GatewayRouteDocument::toDomain);
        });
    }

    @Override
    public CompletableFuture<Optional<GatewayRoute>> findByRouteId(String tenantId, String routeId) {
        return CompletableFuture.supplyAsync(() -> {
            return repository.findByTenantIdAndRouteId(tenantId, routeId)
                .map(GatewayRouteDocument::toDomain);
        });
    }

    @Override
    public CompletableFuture<List<GatewayRoute>> findByTenant(String tenantId) {
        return CompletableFuture.supplyAsync(() -> {
            return repository.findByTenantId(tenantId).stream()
                .map(GatewayRouteDocument::toDomain)
                .toList();
        });
    }

    @Override
    public CompletableFuture<List<GatewayRoute>> findByService(String tenantId, String serviceId) {
        return CompletableFuture.supplyAsync(() -> {
            return repository.findByTenantIdAndServiceId(tenantId, serviceId).stream()
                .map(GatewayRouteDocument::toDomain)
                .toList();
        });
    }

    @Override
    public CompletableFuture<List<GatewayRoute>> findByPath(String tenantId, String pathPattern) {
        return CompletableFuture.supplyAsync(() -> {
            return repository.findByTenantIdAndPathRegex(tenantId, pathPattern).stream()
                .map(GatewayRouteDocument::toDomain)
                .toList();
        });
    }

    @Override
    public CompletableFuture<List<GatewayRoute>> findByStatus(String tenantId, GatewayRoute.RouteStatus status) {
        return CompletableFuture.supplyAsync(() -> {
            return repository.findByTenantIdAndStatus(tenantId, status.name()).stream()
                .map(GatewayRouteDocument::toDomain)
                .toList();
        });
    }

    @Override
    public CompletableFuture<List<GatewayRoute>> findByEnabled(String tenantId, boolean enabled) {
        return CompletableFuture.supplyAsync(() -> {
            return repository.findByTenantIdAndEnabled(tenantId, enabled).stream()
                .map(GatewayRouteDocument::toDomain)
                .toList();
        });
    }

    @Override
    public CompletableFuture<List<GatewayRoute>> findByTags(String tenantId, List<String> tags) {
        return CompletableFuture.supplyAsync(() -> {
            return repository.findByTenantIdAndTagsIn(tenantId, tags).stream()
                .map(GatewayRouteDocument::toDomain)
                .toList();
        });
    }

    @Override
    public CompletableFuture<List<GatewayRoute>> searchByKeyword(String tenantId, String keyword) {
        return CompletableFuture.supplyAsync(() -> {
            return repository.searchByTenantIdAndKeyword(tenantId, keyword).stream()
                .map(GatewayRouteDocument::toDomain)
                .toList();
        });
    }

    @Override
    public CompletableFuture<List<GatewayRoute>> findAllOrdered(String tenantId) {
        return CompletableFuture.supplyAsync(() -> {
            return repository.findByTenantIdOrderByOrderAsc(tenantId).stream()
                .map(GatewayRouteDocument::toDomain)
                .toList();
        });
    }

    @Override
    public CompletableFuture<Boolean> deleteById(String id) {
        return CompletableFuture.supplyAsync(() -> {
            repository.deleteById(id);
            return true;
        });
    }

    @Override
    public CompletableFuture<Boolean> deleteByRouteId(String tenantId, String routeId) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<GatewayRouteDocument> document = repository.findByTenantIdAndRouteId(tenantId, routeId);
            if (document.isPresent()) {
                repository.delete(document.get());
                return true;
            }
            return false;
        });
    }

    @Override
    public CompletableFuture<Long> countByTenant(String tenantId) {
        return CompletableFuture.supplyAsync(() -> {
            return repository.count();
        });
    }
}
