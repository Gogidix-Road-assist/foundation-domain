package com.gogidix.rapidassist.common.domain.models.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Test configuration for JPA tests.
 * Explicitly specifies entity and repository scanning paths.
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.gogidix.rapidassist.common.domain.models.repository")
@EntityScan(basePackages = "com.gogidix.rapidassist.common.domain.models.business")
public class TestJpaConfig {
}
