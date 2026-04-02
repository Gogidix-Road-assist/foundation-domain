package com.gogidix.rapidassist.user.profile.service.infrastructure.persistence;

import com.gogidix.rapidassist.user.profile.service.domain.port.out.UserProfileStore;
import com.gogidix.rapidassist.user.profile.service.infrastructure.persistence.memory.InMemoryUserProfileStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserProfilePersistenceConfiguration {

    @Bean
    @ConditionalOnMissingBean(UserProfileStore.class)
    public UserProfileStore inMemoryUserProfileStore() {
        return new InMemoryUserProfileStore();
    }
}
