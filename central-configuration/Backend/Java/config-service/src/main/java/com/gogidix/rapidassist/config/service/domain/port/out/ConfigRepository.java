package com.gogidix.rapidassist.config.service.domain.port.out;

import com.gogidix.rapidassist.config.service.domain.model.Config;
import java.util.List;
import java.util.Optional;

/**
 * Port interface for Config repository operations
 */
public interface ConfigRepository {
    
    Optional<Config> findById(String id);
    
    List<Config> findByTenantId(String tenantId);
    
    List<Config> findByTenantIdAndEnvironment(String tenantId, String environment);
    
    List<Config> findAll();
    
    Config save(Config config);
    
    void deleteById(String id);
    
    boolean existsByTenantIdAndKey(String tenantId, String key);
}
