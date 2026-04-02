package com.gogidix.rapidassist.ai.contentanalysis.infrastructure.config;

import com.gogidix.rapidassist.ai.contentanalysis.domain.tenant.TenantContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Optional;

/**
 * MongoDB Configuration
 * Configures MongoDB with tenant-aware auditing and repository scanning
 */
@Slf4j
@Configuration
@EnableMongoRepositories(basePackages = {
    "com.gogidix.rapidassist.ai.contentanalysis.infrastructure.persistence.repository"
})
@EnableMongoAuditing
public class MongoConfig {

    /**
     * Auditor aware for tracking who created/modified entities
     */
    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> {
            String tenantId = TenantContext.getTenantId();
            if (tenantId != null) {
                return Optional.of("system:" + tenantId);
            }
            return Optional.of("system");
        };
    }
}
