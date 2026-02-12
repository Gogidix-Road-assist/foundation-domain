package com.gogidix.rapidassist.config.service.domain.event;

import com.gogidix.rapidassist.config.service.domain.model.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for Configuration domain events.
 * Tests event construction, getters, and basic functionality.
 */
class ConfigurationEventTest {

    @BeforeEach
    void setUp() {
        // Setup if needed
    }

    private Configuration createTestConfiguration(String id, String tenantId, String configKey, Integer version) {
        return new Configuration(
                id,
                tenantId,
                configKey,
                "production",
                "default",
                version,
                "test-value",
                Configuration.ConfigurationDataType.STRING,
                false,
                false,
                null,
                "Test configuration",
                Set.of("test", "api"),
                Map.of("owner", "team-a", "priority", "high"),
                null,
                Configuration.ConfigurationStatus.ACTIVE,
                "test-user",
                Instant.now().minusSeconds(3600),
                "test-user",
                Instant.now().minusSeconds(1800),
                Instant.now().minusSeconds(900),
                Set.of()
        );
    }

    @Nested
    class ConfigurationCreatedEventTests {

        @Test
        void configurationCreatedEvent_WithValidData_CreatesSuccessfully() {
            // Arrange
            Configuration config = createTestConfiguration("config-id-1", "tenant-1", "api.timeout", 1);

            // Act
            ConfigurationCreatedEvent event = new ConfigurationCreatedEvent(config, "creator-user");

            // Assert
            assertNotNull(event);
            assertEquals("config-id-1", event.aggregateId());
            assertEquals("tenant-1", event.tenantId());
            assertEquals(1, event.version());
            assertEquals("api.timeout", event.configKey());
            assertEquals("production", event.environment());
            assertEquals("default", event.namespace());
            assertEquals("test-value", event.value());
            assertEquals("creator-user", event.createdBy());
            assertEquals("ConfigurationCreated", event.eventType());
            assertNotNull(event.eventId());
            assertNotNull(event.occurredAt());
        }

        @Test
        void configurationCreatedEvent_WithDifferentDataTypes_CreatesSuccessfully() {
            // Arrange & Act - String
            Configuration stringConfig = createTestConfiguration("id-1", "tenant-1", "string.config", 1)
                    .withValue("string-value");
            ConfigurationCreatedEvent stringEvent = new ConfigurationCreatedEvent(stringConfig, "user");

            assertEquals("string-value", stringEvent.value());

            // Act - Number
            Configuration numberConfig = createTestConfiguration("id-2", "tenant-1", "number.config", 1)
                    .withValue(12345);
            ConfigurationCreatedEvent numberEvent = new ConfigurationCreatedEvent(numberConfig, "user");

            assertEquals(12345, numberEvent.value());

            // Act - Boolean
            Configuration boolConfig = createTestConfiguration("id-3", "tenant-1", "bool.config", 1)
                    .withValue(true);
            ConfigurationCreatedEvent boolEvent = new ConfigurationCreatedEvent(boolConfig, "user");

            assertEquals(true, boolEvent.value());

            // Act - Object
            Map<String, Object> objectValue = Map.of("key1", "value1", "key2", 123);
            Configuration objectConfig = createTestConfiguration("id-4", "tenant-1", "object.config", 1)
                    .withValue(objectValue);
            ConfigurationCreatedEvent objectEvent = new ConfigurationCreatedEvent(objectConfig, "user");

            assertEquals(objectValue, objectEvent.value());
        }

        @Test
        void configurationCreatedEvent_WithComplexConfiguration_CapturesAllFields() {
            // Arrange
            Configuration config = createTestConfiguration("config-id", "tenant-1", "complex.config", 1);

            // Act
            ConfigurationCreatedEvent event = new ConfigurationCreatedEvent(config, "creator");

            // Assert
            assertEquals("complex.config", event.configKey());
            assertEquals("production", event.environment());
            assertEquals("default", event.namespace());
            assertEquals("test-value", event.value());
            assertTrue(config.tags().contains("test"));
            assertTrue(config.tags().contains("api"));
            assertEquals("team-a", config.metadata().get("owner"));
        }

        @Test
        void configurationCreatedEvent_Getters_ReturnCorrectValues() {
            // Arrange
            Configuration config = createTestConfiguration("config-id", "tenant-1", "api.timeout", 1);
            ConfigurationCreatedEvent event = new ConfigurationCreatedEvent(config, "test-user");

            // Act & Assert
            assertEquals("config-id", event.aggregateId());
            assertEquals("tenant-1", event.tenantId());
            assertEquals("api.timeout", event.configKey());
            assertEquals("production", event.environment());
            assertEquals("default", event.namespace());
            assertEquals("test-value", event.value());
            assertEquals("test-user", event.createdBy());
            assertEquals(1, event.version());
            assertEquals("ConfigurationCreated", event.eventType());
            assertNotNull(event.eventId());
            assertNotNull(event.occurredAt());
        }

        @Test
        void configurationCreatedEvent_ToString_ContainsEventTypeName() {
            // Arrange
            Configuration config = createTestConfiguration("config-id", "tenant-1", "api.timeout", 1);
            ConfigurationCreatedEvent event = new ConfigurationCreatedEvent(config, "creator");

            // Act
            String toString = event.toString();

            // Assert
            assertTrue(toString.contains("ConfigurationCreatedEvent"));
            assertTrue(toString.contains("config-id"));
            assertTrue(toString.contains("api.timeout"));
        }

        @Test
        void configurationCreatedEvent_MultipleInstances_HaveUniqueIds() {
            // Arrange
            Configuration config = createTestConfiguration("config-id", "tenant-1", "api.timeout", 1);

            // Act
            ConfigurationCreatedEvent event1 = new ConfigurationCreatedEvent(config, "user1");
            ConfigurationCreatedEvent event2 = new ConfigurationCreatedEvent(config, "user2");

            // Assert
            assertNotEquals(event1.eventId(), event2.eventId());
        }

        @Test
        void configurationCreatedEvent_WithNullCreatedBy_CreatesSuccessfully() {
            // Arrange
            Configuration config = createTestConfiguration("config-id", "tenant-1", "api.timeout", 1);

            // Act
            ConfigurationCreatedEvent event = new ConfigurationCreatedEvent(config, null);

            // Assert
            assertNull(event.createdBy());
        }
    }

    @Nested
    class ConfigurationUpdatedEventTests {

        @Test
        void configurationUpdatedEvent_WithValidData_CreatesSuccessfully() {
            // Arrange
            Configuration oldConfig = createTestConfiguration("config-id-1", "tenant-1", "api.timeout", 1)
                    .withValue(30);
            Configuration newConfig = createTestConfiguration("config-id-1", "tenant-1", "api.timeout", 2)
                    .withValue(60);

            // Act
            ConfigurationUpdatedEvent event = new ConfigurationUpdatedEvent(
                    oldConfig, newConfig, "updater-user", "Increase timeout for better performance"
            );

            // Assert
            assertNotNull(event);
            assertEquals("config-id-1", event.aggregateId());
            assertEquals("tenant-1", event.tenantId());
            assertEquals("api.timeout", event.configKey());
            assertEquals("production", event.environment());
            assertEquals("default", event.namespace());
            assertEquals(30, event.previousValue());
            assertEquals(60, event.newValue());
            assertEquals(1, event.previousVersion());
            assertEquals(2, event.newVersion());
            assertEquals("updater-user", event.updatedBy());
            assertEquals("Increase timeout for better performance", event.reason());
            assertEquals("ConfigurationUpdated", event.eventType());
        }

        @Test
        void configurationUpdatedEvent_WithDifferentDataTypes_CapturesCorrectValues() {
            // Arrange & Act - String to String
            Configuration oldStringConfig = createTestConfiguration("id-1", "tenant-1", "string.config", 1)
                    .withValue("old-value");
            Configuration newStringConfig = createTestConfiguration("id-1", "tenant-1", "string.config", 2)
                    .withValue("new-value");
            ConfigurationUpdatedEvent stringEvent = new ConfigurationUpdatedEvent(
                    oldStringConfig, newStringConfig, "user", "Update string"
            );

            assertEquals("old-value", stringEvent.previousValue());
            assertEquals("new-value", stringEvent.newValue());

            // Act - Number to Number
            Configuration oldNumberConfig = createTestConfiguration("id-2", "tenant-1", "number.config", 1)
                    .withValue(100);
            Configuration newNumberConfig = createTestConfiguration("id-2", "tenant-1", "number.config", 2)
                    .withValue(200);
            ConfigurationUpdatedEvent numberEvent = new ConfigurationUpdatedEvent(
                    oldNumberConfig, newNumberConfig, "user", "Update number"
            );

            assertEquals(100, numberEvent.previousValue());
            assertEquals(200, numberEvent.newValue());

            // Act - Boolean to Boolean
            Configuration oldBoolConfig = createTestConfiguration("id-3", "tenant-1", "bool.config", 1)
                    .withValue(false);
            Configuration newBoolConfig = createTestConfiguration("id-3", "tenant-1", "bool.config", 2)
                    .withValue(true);
            ConfigurationUpdatedEvent boolEvent = new ConfigurationUpdatedEvent(
                    oldBoolConfig, newBoolConfig, "user", "Toggle boolean"
            );

            assertEquals(false, boolEvent.previousValue());
            assertEquals(true, boolEvent.newValue());
        }

        @Test
        void configurationUpdatedEvent_WithTypeChange_CapturesBothTypes() {
            // Arrange
            Configuration oldConfig = createTestConfiguration("config-id", "tenant-1", "api.timeout", 1)
                    .withValue("30"); // String
            Configuration newConfig = createTestConfiguration("config-id", "tenant-1", "api.timeout", 2)
                    .withValue(30); // Number

            // Act
            ConfigurationUpdatedEvent event = new ConfigurationUpdatedEvent(
                    oldConfig, newConfig, "user", "Change type from string to number"
            );

            // Assert
            assertEquals("30", event.previousValue());
            assertEquals(30, event.newValue());
            assertEquals(1, event.previousVersion());
            assertEquals(2, event.newVersion());
        }

        @Test
        void configurationUpdatedEvent_Getters_ReturnCorrectValues() {
            // Arrange
            Configuration oldConfig = createTestConfiguration("config-id", "tenant-1", "api.timeout", 1);
            Configuration newConfig = createTestConfiguration("config-id", "tenant-1", "api.timeout", 2);
            ConfigurationUpdatedEvent event = new ConfigurationUpdatedEvent(
                    oldConfig, newConfig, "updater", "Update reason"
            );

            // Act & Assert
            assertEquals("config-id", event.aggregateId());
            assertEquals("tenant-1", event.tenantId());
            assertEquals("api.timeout", event.configKey());
            assertEquals("production", event.environment());
            assertEquals("default", event.namespace());
            assertEquals("test-value", event.previousValue());
            assertEquals("test-value", event.newValue());
            assertEquals(1, event.previousVersion());
            assertEquals(2, event.newVersion());
            assertEquals("updater", event.updatedBy());
            assertEquals("Update reason", event.reason());
            assertEquals("ConfigurationUpdated", event.eventType());
            assertNotNull(event.eventId());
            assertNotNull(event.occurredAt());
        }

        @Test
        void configurationUpdatedEvent_ToString_ContainsEventTypeName() {
            // Arrange
            Configuration oldConfig = createTestConfiguration("config-id", "tenant-1", "api.timeout", 1)
                    .withValue(30);
            Configuration newConfig = createTestConfiguration("config-id", "tenant-1", "api.timeout", 2)
                    .withValue(60);
            ConfigurationUpdatedEvent event = new ConfigurationUpdatedEvent(
                    oldConfig, newConfig, "updater", "Increase timeout"
            );

            // Act
            String toString = event.toString();

            // Assert
            assertTrue(toString.contains("ConfigurationUpdatedEvent"));
            assertTrue(toString.contains("config-id"));
            assertTrue(toString.contains("api.timeout"));
        }

        @Test
        void configurationUpdatedEvent_WithNullReason_CreatesSuccessfully() {
            // Arrange
            Configuration oldConfig = createTestConfiguration("config-id", "tenant-1", "api.timeout", 1);
            Configuration newConfig = createTestConfiguration("config-id", "tenant-1", "api.timeout", 2);

            // Act
            ConfigurationUpdatedEvent event = new ConfigurationUpdatedEvent(
                    oldConfig, newConfig, "updater", null
            );

            // Assert
            assertNull(event.reason());
        }

        @Test
        void configurationUpdatedEvent_WithComplexValues_CapturesCorrectly() {
            // Arrange
            Map<String, Object> oldValue = Map.of("key1", "value1", "key2", 123);
            Map<String, Object> newValue = Map.of("key1", "value1", "key2", 456, "key3", "new");
            Configuration oldConfig = createTestConfiguration("config-id", "tenant-1", "complex.config", 1)
                    .withValue(oldValue);
            Configuration newConfig = createTestConfiguration("config-id", "tenant-1", "complex.config", 2)
                    .withValue(newValue);

            // Act
            ConfigurationUpdatedEvent event = new ConfigurationUpdatedEvent(
                    oldConfig, newConfig, "updater", "Update complex object"
            );

            // Assert
            assertEquals(oldValue, event.previousValue());
            assertEquals(newValue, event.newValue());
        }
    }

    @Nested
    class ConfigurationDeletedEventTests {

        @Test
        void configurationDeletedEvent_WithValidData_CreatesSuccessfully() {
            // Arrange & Act
            ConfigurationDeletedEvent event = new ConfigurationDeletedEvent(
                    "config-id-1",
                    "tenant-1",
                    "api.timeout",
                    "production",
                    "default",
                    1,
                    "old-value",
                    "deleter-user",
                    "Configuration no longer needed"
            );

            // Assert
            assertNotNull(event);
            assertEquals("config-id-1", event.aggregateId());
            assertEquals("tenant-1", event.tenantId());
            assertEquals("api.timeout", event.configKey());
            assertEquals("production", event.environment());
            assertEquals("default", event.namespace());
            assertEquals(1, event.versionAtDeletion());
            assertEquals("old-value", event.valueBeforeDeletion());
            assertEquals("deleter-user", event.deletedBy());
            assertEquals("Configuration no longer needed", event.reason());
            assertEquals("ConfigurationDeleted", event.eventType());
            assertNotNull(event.eventId());
            assertNotNull(event.occurredAt());
        }

        @Test
        void configurationDeletedEvent_WithDifferentValueTypes_CapturesCorrectly() {
            // Act - String value
            ConfigurationDeletedEvent stringEvent = new ConfigurationDeletedEvent(
                    "id-1", "tenant-1", "string.config", "prod", "default",
                    1, "string-value", "user", "Delete"
            );
            assertEquals("string-value", stringEvent.valueBeforeDeletion());

            // Act - Number value
            ConfigurationDeletedEvent numberEvent = new ConfigurationDeletedEvent(
                    "id-2", "tenant-1", "number.config", "prod", "default",
                    1, 12345, "user", "Delete"
            );
            assertEquals(12345, numberEvent.valueBeforeDeletion());

            // Act - Boolean value
            ConfigurationDeletedEvent boolEvent = new ConfigurationDeletedEvent(
                    "id-3", "tenant-1", "bool.config", "prod", "default",
                    1, true, "user", "Delete"
            );
            assertEquals(true, boolEvent.valueBeforeDeletion());

            // Act - Object value
            Map<String, Object> objectValue = Map.of("key", "value", "number", 42);
            ConfigurationDeletedEvent objectEvent = new ConfigurationDeletedEvent(
                    "id-4", "tenant-1", "object.config", "prod", "default",
                    1, objectValue, "user", "Delete"
            );
            assertEquals(objectValue, objectEvent.valueBeforeDeletion());
        }

        @Test
        void configurationDeletedEvent_Getters_ReturnCorrectValues() {
            // Arrange & Act
            ConfigurationDeletedEvent event = new ConfigurationDeletedEvent(
                    "config-id", "tenant-1", "api.timeout", "production", "default",
                    5, "value-to-delete", "deleter", "Cleanup"
            );

            // Assert
            assertEquals("config-id", event.aggregateId());
            assertEquals("tenant-1", event.tenantId());
            assertEquals("api.timeout", event.configKey());
            assertEquals("production", event.environment());
            assertEquals("default", event.namespace());
            assertEquals(5, event.versionAtDeletion());
            assertEquals("value-to-delete", event.valueBeforeDeletion());
            assertEquals("deleter", event.deletedBy());
            assertEquals("Cleanup", event.reason());
            assertEquals("ConfigurationDeleted", event.eventType());
            assertNotNull(event.eventId());
            assertNotNull(event.occurredAt());
        }

        @Test
        void configurationDeletedEvent_ToString_ContainsEventTypeName() {
            // Arrange & Act
            ConfigurationDeletedEvent event = new ConfigurationDeletedEvent(
                    "config-id", "tenant-1", "api.timeout", "production", "default",
                    1, "old-value", "deleter", "Remove config"
            );

            String toString = event.toString();

            // Assert
            assertTrue(toString.contains("ConfigurationDeletedEvent"));
            assertTrue(toString.contains("config-id"));
            assertTrue(toString.contains("api.timeout"));
        }

        @Test
        void configurationDeletedEvent_WithNullReason_CreatesSuccessfully() {
            // Arrange & Act
            ConfigurationDeletedEvent event = new ConfigurationDeletedEvent(
                    "config-id", "tenant-1", "api.timeout", "production", "default",
                    1, "value", "deleter", null
            );

            // Assert
            assertNull(event.reason());
        }

        @Test
        void configurationDeletedEvent_WithNullValue_CreatesSuccessfully() {
            // Arrange & Act
            ConfigurationDeletedEvent event = new ConfigurationDeletedEvent(
                    "config-id", "tenant-1", "api.timeout", "production", "default",
                    1, null, "deleter", "Delete"
            );

            // Assert
            assertNull(event.valueBeforeDeletion());
        }

        @Test
        void configurationDeletedEvent_WithComplexValue_CapturesCorrectly() {
            // Arrange
            Map<String, Object> complexValue = Map.of(
                    "nested", Map.of("inner", "value"),
                    "array", Set.of("item1", "item2"),
                    "number", 42
            );

            // Act
            ConfigurationDeletedEvent event = new ConfigurationDeletedEvent(
                    "config-id", "tenant-1", "complex.config", "production", "default",
                    1, complexValue, "deleter", "Delete complex"
            );

            // Assert
            assertEquals(complexValue, event.valueBeforeDeletion());
        }

        @Test
        void configurationDeletedEvent_MultipleInstances_HaveUniqueIds() {
            // Act
            ConfigurationDeletedEvent event1 = new ConfigurationDeletedEvent(
                    "config-id", "tenant-1", "api.timeout", "prod", "default",
                    1, "value", "user", "Delete"
            );
            ConfigurationDeletedEvent event2 = new ConfigurationDeletedEvent(
                    "config-id", "tenant-1", "api.timeout", "prod", "default",
                    1, "value", "user", "Delete"
            );

            // Assert
            assertNotEquals(event1.eventId(), event2.eventId());
        }
    }

    @Nested
    class DomainEventBaseClassTests {

        @Test
        void domainEvent_BaseFields_ArePopulatedCorrectly() {
            // Arrange
            Configuration config = createTestConfiguration("config-id", "tenant-1", "api.timeout", 1);
            ConfigurationCreatedEvent event = new ConfigurationCreatedEvent(config, "creator");

            // Act & Assert
            assertNotNull(event.eventId());
            assertTrue(event.eventId().length() > 0);
            assertEquals("config-id", event.aggregateId());
            assertEquals("ConfigurationCreated", event.eventType());
            assertNotNull(event.occurredAt());
            assertEquals("tenant-1", event.tenantId());
            assertEquals(1, event.version());
        }

        @Test
        void domainEvent_OccurredAt_IsRecent() {
            // Arrange
            Configuration config = createTestConfiguration("config-id", "tenant-1", "api.timeout", 1);
            Instant beforeCreation = Instant.now();

            // Act
            ConfigurationCreatedEvent event = new ConfigurationCreatedEvent(config, "creator");
            Instant afterCreation = Instant.now();

            // Assert
            assertFalse(event.occurredAt().isBefore(beforeCreation));
            assertFalse(event.occurredAt().isAfter(afterCreation));
        }
    }

    @Nested
    class EdgeCaseTests {

        @Test
        void configurationCreatedEvent_WithEmptyStrings_CreatesSuccessfully() {
            // Arrange
            Configuration config = new Configuration(
                    "id", "", "", "", "", 1, "",
                    Configuration.ConfigurationDataType.STRING, false, false, null,
                    "", Set.of(), Map.of(), null,
                    Configuration.ConfigurationStatus.ACTIVE, "", Instant.now(),
                    "", Instant.now(), null, Set.of()
            );

            // Act
            ConfigurationCreatedEvent event = new ConfigurationCreatedEvent(config, "");

            // Assert
            assertEquals("", event.configKey());
            assertEquals("", event.environment());
            assertEquals("", event.namespace());
            assertEquals("", event.createdBy());
        }

        @Test
        void configurationDeletedEvent_WithEmptyStrings_CreatesSuccessfully() {
            // Act
            ConfigurationDeletedEvent event = new ConfigurationDeletedEvent(
                    "", "", "", "", "", 1, "", "", ""
            );

            // Assert
            assertEquals("", event.aggregateId());
            assertEquals("", event.tenantId());
            assertEquals("", event.configKey());
            assertEquals("", event.environment());
            assertEquals("", event.namespace());
            assertEquals("", event.valueBeforeDeletion());
            assertEquals("", event.deletedBy());
            assertEquals("", event.reason());
        }

        @Test
        void configurationCreatedEvent_WithSpecialCharactersInConfigKey_CreatesSuccessfully() {
            // Arrange
            Configuration config = createTestConfiguration("config-id", "tenant-1", "api.timeout/v2", 1);

            // Act
            ConfigurationCreatedEvent event = new ConfigurationCreatedEvent(config, "creator");

            // Assert
            assertEquals("api.timeout/v2", event.configKey());
        }

        @Test
        void configurationUpdatedEvent_WithLargeVersionNumbers_CreatesSuccessfully() {
            // Arrange
            Configuration oldConfig = createTestConfiguration("config-id", "tenant-1", "api.timeout", 999);
            Configuration newConfig = createTestConfiguration("config-id", "tenant-1", "api.timeout", 1000);

            // Act
            ConfigurationUpdatedEvent event = new ConfigurationUpdatedEvent(
                    oldConfig, newConfig, "updater", "Update"
            );

            // Assert
            assertEquals(999, event.previousVersion());
            assertEquals(1000, event.newVersion());
        }
    }

    @Nested
    class EventEqualityTests {

        @Test
        void configurationCreatedEvent_TwoEventsWithSameConfig_AreNotEqual() {
            // Arrange
            Configuration config = createTestConfiguration("config-id", "tenant-1", "api.timeout", 1);

            // Act
            ConfigurationCreatedEvent event1 = new ConfigurationCreatedEvent(config, "creator");
            ConfigurationCreatedEvent event2 = new ConfigurationCreatedEvent(config, "creator");

            // Assert - Different event IDs
            assertNotEquals(event1.eventId(), event2.eventId());
        }

        @Test
        void configurationUpdatedEvent_TwoEventsWithSameConfig_AreNotEqual() {
            // Arrange
            Configuration oldConfig = createTestConfiguration("config-id", "tenant-1", "api.timeout", 1);
            Configuration newConfig = createTestConfiguration("config-id", "tenant-1", "api.timeout", 2);

            // Act
            ConfigurationUpdatedEvent event1 = new ConfigurationUpdatedEvent(
                    oldConfig, newConfig, "updater", "Update"
            );
            ConfigurationUpdatedEvent event2 = new ConfigurationUpdatedEvent(
                    oldConfig, newConfig, "updater", "Update"
            );

            // Assert - Different event IDs
            assertNotEquals(event1.eventId(), event2.eventId());
        }

        @Test
        void configurationDeletedEvent_TwoEventsWithSameConfig_AreNotEqual() {
            // Act
            ConfigurationDeletedEvent event1 = new ConfigurationDeletedEvent(
                    "config-id", "tenant-1", "api.timeout", "prod", "default",
                    1, "value", "deleter", "Delete"
            );
            ConfigurationDeletedEvent event2 = new ConfigurationDeletedEvent(
                    "config-id", "tenant-1", "api.timeout", "prod", "default",
                    1, "value", "deleter", "Delete"
            );

            // Assert - Different event IDs
            assertNotEquals(event1.eventId(), event2.eventId());
        }
    }
}
