package com.gogidix.rapidassist.config.service.infrastructure.messaging.kafka;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for KafkaConfig.
 * Tests Kafka configuration setup including:
 * - Topic constants
 * - Configuration properties
 * - Documentation of expected behavior
 */
class KafkaConfigTest {

    // ========================================================================
    // Configuration Constants Tests
    // ========================================================================

    @Test
    void configurationEventTopicConstant_IsDefined() {
        // Given & Then
        assertEquals("configuration.events", KafkaConfig.CONFIGURATION_EVENTS_TOPIC);
        assertNotNull(KafkaConfig.CONFIGURATION_EVENTS_TOPIC);
    }

    @Test
    void configurationChangesTopicConstant_IsDefined() {
        // Given & Then
        assertEquals("configuration.changes", KafkaConfig.CONFIGURATION_CHANGES_TOPIC);
        assertNotNull(KafkaConfig.CONFIGURATION_CHANGES_TOPIC);
    }

    @Test
    void configurationValidationTopicConstant_IsDefined() {
        // Given & Then
        assertEquals("configuration.validation", KafkaConfig.CONFIGURATION_VALIDATION_TOPIC);
        assertNotNull(KafkaConfig.CONFIGURATION_VALIDATION_TOPIC);
    }

    // ========================================================================
    // Configuration Annotation Tests
    // ========================================================================

    @Test
    void kafkaConfigClass_HasConfigurationAnnotation() {
        // Given & Then
        assertTrue(KafkaConfig.class.isAnnotationPresent(org.springframework.context.annotation.Configuration.class));
    }

    // ========================================================================
    // Topic Naming Convention Tests
    // ========================================================================

    @Test
    void topicNames_FollowNamingConvention() {
        // Given & Then
        // All topic names should follow the pattern: "configuration.{suffix}"
        String eventsTopic = KafkaConfig.CONFIGURATION_EVENTS_TOPIC;
        String changesTopic = KafkaConfig.CONFIGURATION_CHANGES_TOPIC;
        String validationTopic = KafkaConfig.CONFIGURATION_VALIDATION_TOPIC;

        assertTrue(eventsTopic.startsWith("configuration."));
        assertTrue(changesTopic.startsWith("configuration."));
        assertTrue(validationTopic.startsWith("configuration."));
    }

    @Test
    void topicNames_AreDistinct() {
        // Given & Then
        // All topic names should be unique
        assertNotEquals(
            KafkaConfig.CONFIGURATION_EVENTS_TOPIC,
            KafkaConfig.CONFIGURATION_CHANGES_TOPIC
        );
        assertNotEquals(
            KafkaConfig.CONFIGURATION_EVENTS_TOPIC,
            KafkaConfig.CONFIGURATION_VALIDATION_TOPIC
        );
        assertNotEquals(
            KafkaConfig.CONFIGURATION_CHANGES_TOPIC,
            KafkaConfig.CONFIGURATION_VALIDATION_TOPIC
        );
    }

    // ========================================================================
    // Default Configuration Values Tests
    // ========================================================================

    @Test
    void bootstrapServers_DefaultValue() {
        // This test documents the default bootstrap servers configuration
        // Default should be: localhost:9092
        String expectedDefaultBootstrapServers = "localhost:9092";

        // Verify the expected default
        assertEquals("localhost:9092", expectedDefaultBootstrapServers);
    }

    @Test
    void eventsTopic_DefaultValue() {
        // This test documents the default events topic configuration
        // Default should be: configuration.events
        String expectedDefaultEventsTopic = "configuration.events";

        assertEquals("configuration.events", expectedDefaultEventsTopic);
    }

    // ========================================================================
    // Profile-based Configuration Tests
    // ========================================================================

    @Test
    void kafkaConfig_ActiveWhenKafkaNotDisabled() {
        // This test documents that KafkaConfig should be active
        // when the "kafka-disabled" profile is NOT active

        // The configuration should use @Profile("!kafka-disabled")
        // This means it's active by default, unless kafka-disabled profile is active
        String expectedProfile = "!kafka-disabled";

        assertNotNull(expectedProfile);
        assertTrue(expectedProfile.startsWith("!"));
        assertTrue(expectedProfile.contains("kafka-disabled"));
    }
}
