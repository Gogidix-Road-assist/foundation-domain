package com.gogidix.rapidassist.orchestration.monitoringservice.infrastructure.persistence;

import com.gogidix.rapidassist.orchestration.monitoringservice.domain.model.EntityProvider;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EntityProviderRepository extends MongoRepository<EntityProvider, String> {

    Optional<EntityProvider> findByProviderId(String providerId);

    List<EntityProvider> findByTenantIdAndStatus(String tenantId, EntityProvider.ProviderStatus status);

    List<EntityProvider> findByTenantId(String tenantId);

    List<EntityProvider> findByTenantIdAndCurrentStatus(String tenantId, EntityProvider.ProviderStatus status);
}
