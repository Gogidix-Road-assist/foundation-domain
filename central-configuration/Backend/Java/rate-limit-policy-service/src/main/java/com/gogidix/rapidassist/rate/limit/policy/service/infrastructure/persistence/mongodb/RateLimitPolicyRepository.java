package com.gogidix.rapidassist.rate.limit.policy.service.infrastructure.persistence.mongodb;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RateLimitPolicyRepository extends MongoRepository<RateLimitPolicyDocument, String> {

    Optional<RateLimitPolicyDocument> findByTenantIdAndPolicyKey(String tenantId, String policyKey);

    Page<RateLimitPolicyDocument> findByTenantId(String tenantId, Pageable pageable);

    Page<RateLimitPolicyDocument> findByTenantIdAndEnvironment(String tenantId, String environment, Pageable pageable);

    Page<RateLimitPolicyDocument> findByTenantIdAndEnabled(String tenantId, boolean enabled, Pageable pageable);

    Page<RateLimitPolicyDocument> findByTenantIdAndLimitType(String tenantId, String limitType, Pageable pageable);

    List<RateLimitPolicyDocument> findByTenantIdAndPolicyKeyAndEnvironment(String tenantId, String policyKey, String environment);

    boolean existsByTenantIdAndPolicyKey(String tenantId, String policyKey);

    long countByTenantId(String tenantId);

    @Query("{ 'tenantId': ?0, 'enabled': true, 'environment': ?1 }")
    List<RateLimitPolicyDocument> findActivePoliciesByTenantAndEnvironment(String tenantId, String environment);

    @Query(value = "{ 'tenantId': ?0, 'policyKey': ?1 }",
           sort = "{ 'version': -1 }")
    Optional<RateLimitPolicyDocument> findLatestVersionByTenantAndKey(String tenantId, String policyKey);

    @Query("{ 'tenantId': ?0, '$or': [ { 'policyKey': { $regex: ?1, $options: 'i' } }, { 'name': { $regex: ?1, $options: 'i' } } ] }")
    Page<RateLimitPolicyDocument> searchByTenantAndKeyword(String tenantId, String keyword, Pageable pageable);
}
