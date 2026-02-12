package com.gogidix.rapidassist.orchestration.location.infrastructure.persistence.mongo;

import com.gogidix.rapidassist.orchestration.location.domain.model.Route;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * MongoDB repository for Route
 */
public interface RouteMongoRepository extends MongoRepository<Route, String> {

    Optional<Route> findByReferenceId(String referenceId);

    List<Route> findByTenantIdAndStatus(
            String tenantId,
            Route.RouteStatus status
    );

    List<Route> findByTenantIdAndExpiresAtBefore(
            String tenantId,
            LocalDateTime now
    );

    List<Route> findByTenantIdAndCreatedAtBefore(
            String tenantId,
            LocalDateTime timestamp
    );

    void deleteByTenantIdAndExpiresAtBefore(
            String tenantId,
            LocalDateTime now
    );

    void deleteByTenantIdAndCreatedAtBefore(
            String tenantId,
            LocalDateTime timestamp
    );
}
