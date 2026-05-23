package com.gogidix.rapidassist.api.gateway.infrastructure.persistence.mongo;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "gateway.mongodb.enabled", havingValue = "true", matchIfMissing = false)
public class MongoTemplateConfig {
}
