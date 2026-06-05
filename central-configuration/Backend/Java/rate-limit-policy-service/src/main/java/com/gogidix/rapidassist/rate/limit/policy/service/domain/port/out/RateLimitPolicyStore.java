package com.gogidix.rapidassist.rate.limit.policy.service.domain.port.out;

import com.gogidix.rapidassist.rate.limit.policy.service.domain.model.RateLimitPolicy;
import com.gogidix.rapidassist.rate.limit.policy.service.domain.model.pagination.PageRequest;
import com.gogidix.rapidassist.rate.limit.policy.service.domain.model.pagination.PageResult;

import java.util.Optional;

public interface RateLimitPolicyStore {

    RateLimitPolicy save(RateLimitPolicy policy);

    Optional<RateLimitPolicy> findById(String id);

    Optional<RateLimitPolicy> findByTenantIdAndPolicyKey(String tenantId, String policyKey);

    PageResult<RateLimitPolicy> findByTenantId(String tenantId, PageRequest pageRequest);

    PageResult<RateLimitPolicy> findByTenantIdAndEnvironment(String tenantId, String environment, PageRequest pageRequest);

    void deleteById(String id);

    boolean existsByTenantIdAndPolicyKey(String tenantId, String policyKey);
}
