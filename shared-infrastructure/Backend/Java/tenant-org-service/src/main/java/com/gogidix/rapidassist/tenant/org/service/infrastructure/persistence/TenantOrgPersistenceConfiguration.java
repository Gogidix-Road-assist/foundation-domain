package com.gogidix.rapidassist.tenant.org.service.infrastructure.persistence;

import com.gogidix.rapidassist.tenant.org.service.domain.port.out.TenantOrganizationStore;
import com.gogidix.rapidassist.tenant.org.service.infrastructure.persistence.memory.InMemoryTenantOrganizationStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TenantOrgPersistenceConfiguration {

    @Bean
    @ConditionalOnMissingBean(TenantOrganizationStore.class)
    public TenantOrganizationStore inMemoryTenantOrganizationStore() {
        return new InMemoryTenantOrganizationStore();
    }
}
