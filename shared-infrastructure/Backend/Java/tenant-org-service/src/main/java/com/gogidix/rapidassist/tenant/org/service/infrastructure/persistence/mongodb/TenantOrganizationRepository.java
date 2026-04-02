package com.gogidix.rapidassist.tenant.org.service.infrastructure.persistence.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface TenantOrganizationRepository extends MongoRepository<TenantOrganizationDocument, String> {

    Optional<TenantOrganizationDocument> findByTenantIdAndOrgId(String tenantId, String orgId);

    List<TenantOrganizationDocument> findByTenantId(String tenantId);

    List<TenantOrganizationDocument> findByTenantIdAndStatus(String tenantId, String status);

    List<TenantOrganizationDocument> findByTenantIdAndOrgType(String tenantId, String orgType);

    List<TenantOrganizationDocument> findByTenantIdAndPlanPlanType(String tenantId, String planType);

    @Query("{'tenantId': ?0, 'orgName': {$regex: ?1, $options: 'i'}}")
    List<TenantOrganizationDocument> findByTenantIdAndOrgNameRegex(String tenantId, String keyword);

    @Query("{'tenantId': ?0, 'tags': {$in: ?1}}")
    List<TenantOrganizationDocument> findByTenantIdAndTagsIn(String tenantId, Set<String> tags);

    @Query("{'tenantId': ?0, 'isActive': true, 'status': 'ACTIVE'}")
    List<TenantOrganizationDocument> findActiveByTenantId(String tenantId);

    List<TenantOrganizationDocument> findByTenantIdAndParentOrgId(String tenantId, String parentOrgId);

    @Query("{'tenantId': ?0, 'plan.endDate': {$lt: ?1}}")
    List<TenantOrganizationDocument> findByTenantIdAndPlanEndDateBefore(String tenantId, Instant expiryDate);

    @Query("{'tenantId': ?0, $or: [{'status': 'INACTIVE'}, {'lastActiveAt': {$lt: ?1}}]}")
    List<TenantOrganizationDocument> findInactiveByTenantIdSince(String tenantId, Instant since);

    long countByTenantId(String tenantId);

    long countByTenantIdAndStatus(String tenantId, String status);
}
