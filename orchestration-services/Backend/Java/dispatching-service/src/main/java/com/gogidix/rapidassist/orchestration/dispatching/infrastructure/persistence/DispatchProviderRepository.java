package com.gogidix.rapidassist.orchestration.dispatching.infrastructure.persistence;

import com.gogidix.rapidassist.orchestration.dispatching.domain.model.DispatchProvider;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DispatchProviderRepository extends MongoRepository<DispatchProvider, String> {

    Optional<DispatchProvider> findByProviderId(String providerId);

    List<DispatchProvider> findByTenantIdAndCurrentStatus(
        String tenantId,
        DispatchProvider.ProviderStatus status
    );

    List<DispatchProvider> findByTenantIdAndCurrentStatusIn(
        String tenantId,
        List<DispatchProvider.ProviderStatus> statuses
    );

    List<DispatchProvider> findByServiceTypesContaining(String serviceType);

    List<DispatchProvider> findByOrganizationId(String organizationId);

    @Query("{ 'tenantId': ?0, 'currentStatus': 'AVAILABLE', 'currentLoad': { $lt: '$maxConcurrentJobs' } }")
    List<DispatchProvider> findAvailableProvidersByTenantId(String tenantId);

    @Query("{ 'tenantId': ?0, 'currentStatus': 'AVAILABLE', 'capabilities': { $in: ?1 } }")
    List<DispatchProvider> findAvailableProvidersByCapabilities(String tenantId, List<String> capabilities);

    List<DispatchProvider> findByTenantId(String tenantId);

    List<DispatchProvider> findByUserId(String userId);
}
