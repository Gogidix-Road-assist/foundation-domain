package com.gogidix.rapidassist.feature.flags.service.domain.port.out;

import com.gogidix.rapidassist.feature.flags.service.domain.model.FeatureFlag;
import java.util.List;
import java.util.Optional;

/**
 * Port interface for FeatureFlag repository operations
 */
public interface FeatureFlagRepository {
    
    Optional<FeatureFlag> findById(String id);
    
    List<FeatureFlag> findByTenantId(String tenantId);
    
    List<FeatureFlag> findByTenantIdAndEnabled(String tenantId, boolean enabled);
    
    List<FeatureFlag> findAll();
    
    FeatureFlag save(FeatureFlag featureFlag);
    
    void deleteById(String id);
    
    boolean existsByTenantIdAndKey(String tenantId, String key);
}
