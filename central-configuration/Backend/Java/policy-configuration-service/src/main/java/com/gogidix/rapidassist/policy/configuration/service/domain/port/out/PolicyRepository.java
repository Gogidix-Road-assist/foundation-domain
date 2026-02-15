package com.gogidix.rapidassist.policy.configuration.service.domain.port.out;

import com.gogidix.rapidassist.policy.configuration.service.domain.model.Policy;
import java.util.List;
import java.util.Optional;

/**
 * Port interface for Policy repository operations
 */
public interface PolicyRepository {
    
    Optional<Policy> findById(String id);
    
    List<Policy> findByTenantId(String tenantId);
    
    List<Policy> findAll();
    
    Policy save(Policy policy);
    
    void deleteById(String id);
    
    boolean existsByTenantIdAndPolicyId(String tenantId, String policyId);
}
