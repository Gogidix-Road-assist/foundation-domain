package com.gogidix.rapidassist.rate.limit.policy.service.domain.port.out;

import com.gogidix.rapidassist.rate.limit.policy.service.domain.model.RateLimitPolicy;
import java.util.List;
import java.util.Optional;

/**
 * Port interface for RateLimitPolicy repository operations
 */
public interface RateLimitPolicyRepository {
    
    Optional<RateLimitPolicy> findById(String id);
    
    List<RateLimitPolicy> findByTenantId(String tenantId);
    
    List<RateLimitPolicy> findByTenantIdAndEnabled(String tenantId, boolean enabled);
    
    List<RateLimitPolicy> findAll();
    
    RateLimitPolicy save(RateLimitPolicy rateLimitPolicy);
    
    void deleteById(String id);
    
    boolean existsByTenantIdAndPolicyType(String tenantId, String policyType);
}
