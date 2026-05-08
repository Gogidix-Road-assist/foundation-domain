package com.gogidix.rapidassist.config.service.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Configuration domain model.
 * Tests the core business logic encapsulated in the Configuration aggregate.
 */
@DisplayName("Configuration Domain Model Tests")
class ConfigurationTest {

    private static final String TENANT_ID = "test-tenant";
    private static final String CONFIG_KEY = "test.config";
    private static final String ENVIRONMENT = "production";
    private static final String NAMESPACE = "api-gateway";
    private static final String USER = "admin";

    @Nested
    @DisplayName("Configuration Factory Methods")
    class FactoryMethods {

        @Test
        @DisplayName("create() should create configuration with version 1")
        void create_HasVersion1() {
            Configuration config = Configuration.create(
                TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
                "test-value", Configuration.ConfigurationDataType.STRING, USER
            );

            assertEquals(1, config.version());
            assertEquals(TENANT_ID, config.tenantId());
            assertEquals(CONFIG_KEY, config.configKey());
            assertEquals(ENVIRONMENT, config.environment());
            assertEquals(NAMESPACE, config.namespace());
        }

        @Test
        @DisplayName("create() should set status to ACTIVE")
        void create_HasActiveStatus() {
            Configuration config = Configuration.create(
                TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
                "test-value", Configuration.ConfigurationDataType.STRING, USER
            );

            assertEquals(Configuration.ConfigurationStatus.ACTIVE, config.status());
        }

        @Test
        @DisplayName("create() should set timestamps")
        void create_HasTimestamps() {
            Instant before = Instant.now();
            Configuration config = Configuration.create(
                TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
                "test-value", Configuration.ConfigurationDataType.STRING, USER
            );
            Instant after = Instant.now();

            assertNotNull(config.createdAt());
            assertNotNull(config.updatedAt());
            assertFalse(config.createdAt().isBefore(before));
            assertFalse(config.createdAt().isAfter(after));
        }
    }

    @Nested
    @DisplayName("withVersion() - Version Management")
    class WithVersionTests {

        @Test
        @DisplayName("withVersion() should create new configuration with updated version")
        void withVersion_UpdatesVersion() {
            Configuration original = Configuration.create(
                TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
                "test-value", Configuration.ConfigurationDataType.STRING, USER
            );

            Configuration version2 = original.withVersion(2);

            assertEquals(2, version2.version());
            assertEquals(1, original.version()); // Original unchanged
        }

        @Test
        @DisplayName("withVersion() should preserve all other fields")
        void withVersion_PreservesOtherFields() {
            Configuration original = Configuration.create(
                TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
                "test-value", Configuration.ConfigurationDataType.STRING, USER
            );

            Configuration version2 = original.withVersion(2);

            assertEquals(original.tenantId(), version2.tenantId());
            assertEquals(original.configKey(), version2.configKey());
            assertEquals(original.environment(), version2.environment());
            assertEquals(original.namespace(), version2.namespace());
            assertEquals(original.value(), version2.value());
            assertEquals(original.dataType(), version2.dataType());
        }
    }

    @Nested
    @DisplayName("withValue() - Value Updates")
    class WithValueTests {

        @Test
        @DisplayName("withValue() should update value and timestamp")
        void withValue_UpdatesValueAndTimestamp() {
            Configuration original = Configuration.create(
                TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
                "old-value", Configuration.ConfigurationDataType.STRING, USER
            );

            Instant originalUpdatedAt = original.updatedAt();

            // Small delay to ensure timestamp difference
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // Ignore
            }

            Configuration updated = original.withValue("new-value");

            assertEquals("new-value", updated.value());
            assertTrue(updated.updatedAt().isAfter(originalUpdatedAt));
        }

        @Test
        @DisplayName("withValue() should preserve version")
        void withValue_PreservesVersion() {
            Configuration original = Configuration.create(
                TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
                "old-value", Configuration.ConfigurationDataType.STRING, USER
            );

            Configuration updated = original.withValue("new-value");

            assertEquals(original.version(), updated.version());
        }
    }

    @Nested
    @DisplayName("withStatus() - Status Transitions")
    class WithStatusTests {

        @Test
        @DisplayName("withStatus() should change status")
        void withStatus_ChangesStatus() {
            Configuration original = Configuration.create(
                TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
                "test-value", Configuration.ConfigurationDataType.STRING, USER
            );

            Configuration inactive = original.withStatus(Configuration.ConfigurationStatus.INACTIVE, USER);

            assertEquals(Configuration.ConfigurationStatus.INACTIVE, inactive.status());
            assertEquals(Configuration.ConfigurationStatus.ACTIVE, original.status());
        }

        @Test
        @DisplayName("withStatus() should update updatedBy and timestamp")
        void withStatus_UpdatesMetadata() throws InterruptedException {
            Configuration original = Configuration.create(
                TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
                "test-value", Configuration.ConfigurationDataType.STRING, "creator"
            );

            // Small delay to ensure timestamp difference
            Thread.sleep(10);

            Instant before = Instant.now();
            Configuration inactive = original.withStatus(Configuration.ConfigurationStatus.INACTIVE, "deactivator");

            assertEquals("deactivator", inactive.updatedBy());
            assertTrue(inactive.updatedAt().isAfter(original.updatedAt()));
            assertTrue(inactive.updatedAt().isAfter(before) || inactive.updatedAt().equals(before));
        }
    }

    @Nested
    @DisplayName("ConfigurationDataType Enum")
    class DataTypeTests {

        @Test
        @DisplayName("Should have all expected data types")
        void hasExpectedDataTypes() {
            Configuration.ConfigurationDataType[] types = Configuration.ConfigurationDataType.values();

            assertTrue(Set.of(types).contains(Configuration.ConfigurationDataType.STRING));
            assertTrue(Set.of(types).contains(Configuration.ConfigurationDataType.NUMBER));
            assertTrue(Set.of(types).contains(Configuration.ConfigurationDataType.BOOLEAN));
            assertTrue(Set.of(types).contains(Configuration.ConfigurationDataType.JSON));
            assertTrue(Set.of(types).contains(Configuration.ConfigurationDataType.YAML));
            assertTrue(Set.of(types).contains(Configuration.ConfigurationDataType.ARRAY));
            assertTrue(Set.of(types).contains(Configuration.ConfigurationDataType.OBJECT));
            assertTrue(Set.of(types).contains(Configuration.ConfigurationDataType.BINARY));
        }
    }

    @Nested
    @DisplayName("ConfigurationStatus Enum")
    class StatusTests {

        @Test
        @DisplayName("Should have all expected statuses")
        void hasExpectedStatuses() {
            Configuration.ConfigurationStatus[] statuses = Configuration.ConfigurationStatus.values();

            assertTrue(Set.of(statuses).contains(Configuration.ConfigurationStatus.ACTIVE));
            assertTrue(Set.of(statuses).contains(Configuration.ConfigurationStatus.INACTIVE));
            assertTrue(Set.of(statuses).contains(Configuration.ConfigurationStatus.DEPRECATED));
            assertTrue(Set.of(statuses).contains(Configuration.ConfigurationStatus.VALIDATION_FAILED));
            assertTrue(Set.of(statuses).contains(Configuration.ConfigurationStatus.PENDING_APPROVAL));
        }
    }

    @Nested
    @DisplayName("Immutability Tests")
    class ImmutabilityTests {

        @Test
        @DisplayName("withVersion() should return new instance")
        void withVersion_ReturnsNewInstance() {
            Configuration original = Configuration.create(
                TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
                "test-value", Configuration.ConfigurationDataType.STRING, USER
            );

            Configuration modified = original.withVersion(2);

            assertNotSame(original, modified);
        }

        @Test
        @DisplayName("withValue() should return new instance")
        void withValue_ReturnsNewInstance() {
            Configuration original = Configuration.create(
                TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
                "test-value", Configuration.ConfigurationDataType.STRING, USER
            );

            Configuration modified = original.withValue("new-value");

            assertNotSame(original, modified);
        }

        @Test
        @DisplayName("withStatus() should return new instance")
        void withStatus_ReturnsNewInstance() {
            Configuration original = Configuration.create(
                TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
                "test-value", Configuration.ConfigurationDataType.STRING, USER
            );

            Configuration modified = original.withStatus(Configuration.ConfigurationStatus.INACTIVE, USER);

            assertNotSame(original, modified);
        }
    }
}
