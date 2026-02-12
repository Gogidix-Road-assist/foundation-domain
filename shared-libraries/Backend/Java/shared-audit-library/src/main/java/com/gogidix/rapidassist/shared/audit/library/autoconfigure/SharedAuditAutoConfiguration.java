package com.gogidix.rapidassist.shared.audit.library.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.rapidassist.shared.audit.library.application.AuditQueryService;
import com.gogidix.rapidassist.shared.audit.library.domain.port.out.AuditPublisher;
import com.gogidix.rapidassist.shared.audit.library.infrastructure.database.AuditLogRepository;
import com.gogidix.rapidassist.shared.audit.library.infrastructure.database.DatabaseAuditPublisher;
import com.gogidix.rapidassist.shared.audit.library.infrastructure.kafka.KafkaAuditPublisher;
import com.gogidix.rapidassist.shared.audit.library.infrastructure.noop.NoOpAuditPublisher;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Auto-configuration for the Shared Audit Library.
 *
 * <p>This configuration automatically sets up the appropriate AuditPublisher implementation
 * based on the available dependencies and configuration properties.
 *
 * <p>Note: MongoDB auditing (@EnableMongoAuditing) should be enabled at the application level,
 * not in this library, to avoid bean definition conflicts when multiple services use this library.
 *
 * <p>Configuration options:
 * <ul>
 *   <li>audit.enabled: Enable/disable audit logging (default: true)</li>
 *   <li>audit.publisher: Publisher type - database, kafka, or noop (default: noop)</li>
 *   <li>audit.retention.enabled: Enable automatic retention cleanup (default: false)</li>
 *   <li>audit.retention.period: Retention period (default: 90d)</li>
 *   <li>audit.kafka.topic: Kafka topic name (default: audit-events)</li>
 * </ul>
 */
@AutoConfiguration
@EnableConfigurationProperties(AuditProperties.class)
@EnableMongoRepositories(basePackages = "com.gogidix.rapidassist.shared.audit.library.infrastructure.database")
@Import(AuditRetentionAutoConfiguration.class)
public class SharedAuditAutoConfiguration {

    /**
     * NoOp audit publisher - used when no other implementation is available or enabled.
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(
        prefix = "audit",
        name = "publisher",
        havingValue = "noop",
        matchIfMissing = true
    )
    public AuditPublisher noOpAuditPublisher() {
        return new NoOpAuditPublisher();
    }

    /**
     * Database audit publisher - automatically configured when MongoDB is available.
     */
    @Bean
    @ConditionalOnClass(name = "org.springframework.data.mongodb.repository.MongoRepository")
    @ConditionalOnBean(AuditLogRepository.class)
    @ConditionalOnMissingBean
    @ConditionalOnProperty(
        prefix = "audit",
        name = "publisher",
        havingValue = "database"
    )
    public AuditPublisher databaseAuditPublisher(
            AuditLogRepository repository,
            ObjectMapper objectMapper) {
        return new DatabaseAuditPublisher(repository, objectMapper);
    }

    /**
     * Kafka audit publisher - automatically configured when Kafka is available.
     */
    @Bean
    @ConditionalOnClass(name = "org.springframework.kafka.core.KafkaTemplate")
    @ConditionalOnBean(KafkaTemplate.class)
    @ConditionalOnMissingBean
    @ConditionalOnProperty(
        prefix = "audit",
        name = "publisher",
        havingValue = "kafka"
    )
    public AuditPublisher kafkaAuditPublisher(
            KafkaTemplate<String, Object> kafkaTemplate,
            ObjectMapper objectMapper,
            AuditProperties properties) {
        // Create a typed KafkaTemplate wrapper
        @SuppressWarnings("unchecked")
        KafkaTemplate<String, com.gogidix.rapidassist.shared.audit.library.domain.model.AuditEventEnvelope> typedTemplate =
                (KafkaTemplate<String, com.gogidix.rapidassist.shared.audit.library.domain.model.AuditEventEnvelope>)
                (Object) kafkaTemplate;

        return new KafkaAuditPublisher(
                typedTemplate,
                objectMapper,
                properties.getKafka().getTopic()
        );
    }

    /**
     * Jackson ObjectMapper for JSON serialization.
     */
    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper auditObjectMapper() {
        return new ObjectMapper();
    }

    /**
     * Audit query service - automatically configured when database publisher is used.
     */
    @Bean
    @ConditionalOnClass(name = "org.springframework.data.mongodb.repository.MongoRepository")
    @ConditionalOnBean(AuditLogRepository.class)
    @ConditionalOnProperty(
        prefix = "audit",
        name = "publisher",
        havingValue = "database"
    )
    public AuditQueryService auditQueryService(AuditLogRepository repository) {
        return new AuditQueryService(repository);
    }
}
