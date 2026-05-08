package com.gogidix.rapidassist.service.registry.discovery.infrastructure.persistence;

import com.gogidix.rapidassist.service.registry.discovery.domain.port.out.ServiceRegistryStore;
import com.gogidix.rapidassist.service.registry.discovery.infrastructure.persistence.memory.InMemoryServiceRegistryStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceRegistryPersistenceConfiguration {

    @Bean
    @ConditionalOnMissingBean(ServiceRegistryStore.class)
    public ServiceRegistryStore inMemoryServiceRegistryStore() {
        return new InMemoryServiceRegistryStore();
    }
}
