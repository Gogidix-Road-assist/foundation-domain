package com.gogidix.rapidassist.pricing.service.infrastructure.persistence;

import com.gogidix.rapidassist.pricing.service.domain.port.out.PriceBookStore;
import com.gogidix.rapidassist.pricing.service.infrastructure.persistence.memory.InMemoryPriceBookStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PricingPersistenceConfiguration {

    @Bean
    @ConditionalOnMissingBean(PriceBookStore.class)
    public PriceBookStore inMemoryPriceBookStore() {
        return new InMemoryPriceBookStore();
    }
}
