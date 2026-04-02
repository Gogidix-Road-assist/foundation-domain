package com.gogidix.rapidassist.dynamic.routing.config.service.infrastructure.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Redis configuration for caching routing rules.
 *
 * <p>Enables caching with tenant-aware cache keys.
 * Connection settings are auto-configured by Spring Boot.
 */
@Configuration
@EnableCaching
@Profile("!redis-disabled")
public class RedisConfig {
    // Spring Boot auto-configures Redis connection, cache manager, etc.
    // from application.yml properties
}
