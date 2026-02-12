package com.gogidix.rapidassist.orchestration.fleetassistanceservice.domain.repository;

import com.gogidix.rapidassist.orchestration.fleetassistanceservice.domain.model.ServiceType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for ServiceType entities
 * ALL queries MUST filter by tenantId for multi-tenancy
 */
@Repository
public interface ServiceTypeRepository extends MongoRepository<ServiceType, String> {

    // Find by tenant and service type code
    Optional<ServiceType> findByServiceTypeCodeAndTenantIdAndDeletedAtIsNull(String serviceTypeCode, String tenantId);

    // Find all active service types by tenant
    List<ServiceType> findByTenantIdAndIsActiveTrueAndDeletedAtIsNull(String tenantId);

    // Find by tenant and category
    List<ServiceType> findByCategoryAndTenantIdAndDeletedAtIsNull(String category, String tenantId);

    // Find by tenant
    List<ServiceType> findByTenantIdAndDeletedAtIsNull(String tenantId);

    // Find by tenant and service name (case insensitive)
    List<ServiceType> findByServiceNameContainingIgnoreCaseAndTenantIdAndDeletedAtIsNull(String serviceName, String tenantId);
}
