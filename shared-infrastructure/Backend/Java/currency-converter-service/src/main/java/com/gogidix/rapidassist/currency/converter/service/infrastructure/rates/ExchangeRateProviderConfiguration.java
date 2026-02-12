package com.gogidix.rapidassist.currency.converter.service.infrastructure.rates;

import com.gogidix.rapidassist.currency.converter.service.domain.port.out.ExchangeRateProvider;
import com.gogidix.rapidassist.currency.converter.service.infrastructure.rates.memory.InMemoryExchangeRateProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExchangeRateProviderConfiguration {

    @Bean
    @ConditionalOnMissingBean(ExchangeRateProvider.class)
    public ExchangeRateProvider inMemoryExchangeRateProvider() {
        return new InMemoryExchangeRateProvider();
    }
}
