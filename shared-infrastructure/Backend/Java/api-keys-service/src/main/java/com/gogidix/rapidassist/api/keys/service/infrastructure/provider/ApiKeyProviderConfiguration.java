package com.gogidix.rapidassist.api.keys.service.infrastructure.provider;

import com.gogidix.rapidassist.api.keys.service.domain.port.out.ApiKeyProvider;
import com.gogidix.rapidassist.api.keys.service.infrastructure.provider.mongo.MongoApiKeyProvider;
import com.gogidix.rapidassist.api.keys.service.infrastructure.provider.noop.NoOpApiKeyProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@EnableConfigurationProperties(ApiKeysProperties.class)
public class ApiKeyProviderConfiguration {

    @Bean
    @ConditionalOnProperty(name = "gogidix.api-keys.provider.type", havingValue = "noop", matchIfMissing = false)
    public ApiKeyProvider noOpApiKeyProvider() {
        return new NoOpApiKeyProvider();
    }

    @Bean
    @ConditionalOnProperty(name = "gogidix.api-keys.provider.type", havingValue = "mongo", matchIfMissing = true)
    public ApiKeyProvider mongoApiKeyProvider(MongoTemplate mongoTemplate,
                                             ApiKeysProperties properties) {
        return new MongoApiKeyProvider(mongoTemplate, properties);
    }
}
