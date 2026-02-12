package com.gogidix.rapidassist.shared.audit.library.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Configuration properties for the Shared Audit Library.
 *
 * <p>These properties can be set in application.yml or application.properties:
 *
 * <pre>
 * audit:
 *   enabled: true
 *   publisher: database  # or kafka, noop
 *   retention:
 *     enabled: true
 *     period: 90d
 *   kafka:
 *     topic: audit-events
 * </pre>
 */
@ConfigurationProperties(prefix = "audit")
public class AuditProperties {

    private boolean enabled = true;
    private PublisherType publisher = PublisherType.NOOP;
    private Retention retention = new Retention();
    private Kafka kafka = new Kafka();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public PublisherType getPublisher() {
        return publisher;
    }

    public void setPublisher(PublisherType publisher) {
        this.publisher = publisher;
    }

    public Retention getRetention() {
        return retention;
    }

    public void setRetention(Retention retention) {
        this.retention = retention;
    }

    public Kafka getKafka() {
        return kafka;
    }

    public void setKafka(Kafka kafka) {
        this.kafka = kafka;
    }

    public enum PublisherType {
        /**
         * Publishes audit events to a database using JPA.
         */
        DATABASE,

        /**
         * Publishes audit events to a Kafka topic.
         */
        KAFKA,

        /**
         * Discards all audit events (no-op implementation).
         */
        NOOP
    }

    public static class Retention {
        private boolean enabled = false;
        private Duration period = Duration.ofDays(90);

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public Duration getPeriod() {
            return period;
        }

        public void setPeriod(Duration period) {
            this.period = period;
        }
    }

    public static class Kafka {
        private String topic = "audit-events";

        public String getTopic() {
            return topic;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }
    }
}
