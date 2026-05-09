package com.gogidix.rapidassist.shared.audit.library.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Auto-configuration for audit retention scheduling.
 *
 * <p>This configuration enables Spring's scheduling support only when
 * audit retention is enabled via configuration.
 */
@Configuration
@ConditionalOnProperty(
    prefix = "audit.retention",
    name = "enabled",
    havingValue = "true"
)
@EnableScheduling
public class AuditRetentionAutoConfiguration {
}
