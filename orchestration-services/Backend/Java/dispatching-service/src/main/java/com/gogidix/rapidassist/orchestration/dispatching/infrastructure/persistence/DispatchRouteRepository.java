package com.gogidix.rapidassist.orchestration.dispatching.infrastructure.persistence;

import java.time.LocalDateTime;

import com.gogidix.rapidassist.orchestration.dispatching.domain.model.DispatchRoute;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DispatchRouteRepository extends MongoRepository<DispatchRoute, String> {

    Optional<DispatchRoute> findByDispatchId(String dispatchId);

    List<DispatchRoute> findByStatus(DispatchRoute.RouteStatus status);

    List<DispatchRoute> findByTenantId(String tenantId);

    List<DispatchRoute> findByStatusAndStartedAtBefore(
        DispatchRoute.RouteStatus status,
        LocalDateTime cutoffTime
    );

    void deleteByDispatchId(String dispatchId);
}
