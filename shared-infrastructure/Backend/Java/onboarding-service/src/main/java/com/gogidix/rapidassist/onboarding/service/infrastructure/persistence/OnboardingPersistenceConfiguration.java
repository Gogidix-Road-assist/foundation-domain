package com.gogidix.rapidassist.onboarding.service.infrastructure.persistence;

import com.gogidix.rapidassist.onboarding.service.domain.port.out.OnboardingStore;
import com.gogidix.rapidassist.onboarding.service.infrastructure.persistence.memory.InMemoryOnboardingStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OnboardingPersistenceConfiguration {

    @Bean
    @ConditionalOnMissingBean(OnboardingStore.class)
    public OnboardingStore inMemoryOnboardingStore() {
        return new InMemoryOnboardingStore();
    }
}
