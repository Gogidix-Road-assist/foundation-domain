package com.gogidix.rapidassist.orchestration.monitoringservice.infrastructure.persistence;

import com.gogidix.rapidassist.orchestration.monitoringservice.domain.model.EntityRoute;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntityRouteRepository extends MongoRepository<EntityRoute, String> {

    List<EntityRoute> findByEntityId(String entityId);
}
