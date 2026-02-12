package com.gogidix.rapidassist.config.service.domain.policy;

import com.gogidix.rapidassist.config.service.domain.model.Configuration;
import com.gogidix.rapidassist.config.service.domain.model.ConfigurationDataType;
import com.gogidix.rapidassist.config.service.domain.model.ConfigurationSchema;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ConfigurationValidationPolicy Tests")
class ConfigurationValidationPolicyTest {

    @Nested
    @DisplayName("Builder Pattern Tests")
    class BuilderTests {

        @Test
        @DisplayName("Should create policy with default settings")
        void shouldCreatePolicyWithDefaults() {
            ConfigurationValidationPolicy policy = ConfigurationValidationPolicy.builder().build();

            assertNotNull(policy);
        }

        @Test
        @DisplayName("Should create policy with custom settings")
        void shouldCreatePolicyWithCustomSettings() {
            ConfigurationValidationPolicy policy = ConfigurationValidationPolicy.builder()
                    .enforceEncryptionForSensitive(false)
                    .requireApprovalForProtectedKeys(false)
                    .build();

            assertNotNull(policy);
        }

        @Test
        @DisplayName("Should create policy from existing policy")
        void shouldCreatePolicyFromExisting() {
            ConfigurationValidationPolicy original = ConfigurationValidationPolicy.builder()
                    .enforceEncryptionForSensitive(true)
                    .requireApprovalForProtectedKeys(false)
                    .build();

            ConfigurationValidationPolicy copy = ConfigurationValidationPolicy.builder(original)
                    .enforceEncryptionForSensitive(false)
                    .build();

            assertNotNull(copy);
        }
    }

    @Nested
    @DisplayName("Required Configuration Validation")
    class RequiredConfigurationTests {

        @Test
        @DisplayName("Should fail validation for required configuration without value")
        void shouldFailRequiredConfigurationWithoutValue() {
            ConfigurationValidationPolicy policy = ConfigurationValidationPolicy.builder().build();
            Configuration config = new Configuration(
                    "id1",
                    "tenant-1",
                    "test.key",
                    "dev",
                    "default",
                    1,
                    null,
                    Configuration.ConfigurationDataType.STRING,
                    false,
                    true,
                    null,
                    "test config",
                    Set.of(),
                    java.util.Map.of(),
                    null,
                    Configuration.ConfigurationStatus.ACTIVE,
                    "user1",
                    Instant.now(),
                    "user1",
                    Instant.now(),
                    null,
                    Set.of()
            );

            var result = policy.validate(config);

            assertFalse(result.isValid());
            assertTrue(result.errors().contains("Required configuration must have a value"));
        }

        @Test
        @DisplayName("Should pass validation for required configuration with value")
        void shouldPassRequiredConfigurationWithValue() {
            ConfigurationValidationPolicy policy = ConfigurationValidationPolicy.builder().build();
            Configuration config = new Configuration(
                    "id1",
                    "tenant-1",
                    "test.key",
                    "dev",
                    "default",
                    1,
                    "test-value",
                    Configuration.ConfigurationDataType.STRING,
                    false,
                    true,
                    null,
                    "test config",
                    Set.of(),
                    java.util.Map.of(),
                    null,
                    Configuration.ConfigurationStatus.ACTIVE,
                    "user1",
                    Instant.now(),
                    "user1",
                    Instant.now(),
                    null,
                    Set.of()
            );

            var result = policy.validate(config);

            assertTrue(result.isValid());
            assertTrue(result.errors().isEmpty());
        }

        @Test
        @DisplayName("Should pass validation for optional configuration without value")
        void shouldPassOptionalConfigurationWithoutValue() {
            ConfigurationValidationPolicy policy = ConfigurationValidationPolicy.builder().build();
            Configuration config = new Configuration(
                    "id1",
                    "tenant-1",
                    "test.key",
                    "dev",
                    "default",
                    1,
                    null,
                    Configuration.ConfigurationDataType.STRING,
                    false,
                    false,
                    null,
                    "test config",
                    Set.of(),
                    java.util.Map.of(),
                    null,
                    Configuration.ConfigurationStatus.ACTIVE,
                    "user1",
                    Instant.now(),
                    "user1",
                    Instant.now(),
                    null,
                    Set.of()
            );

            var result = policy.validate(config);

            assertTrue(result.isValid());
            assertTrue(result.errors().isEmpty());
        }
    }

    @Nested
    @DisplayName("Encrypted Configuration Validation")
    class EncryptedConfigurationTests {

        @Test
        @DisplayName("Should fail encrypted configuration with non-string value")
        void shouldFailEncryptedConfigurationWithNonStringValue() {
            ConfigurationValidationPolicy policy = ConfigurationValidationPolicy.builder().build();
            Configuration config = new Configuration(
                    "id1",
                    "tenant-1",
                    "test.key",
                    "dev",
                    "default",
                    1,
                    123,
                    Configuration.ConfigurationDataType.STRING,
                    true,
                    false,
                    null,
                    "test config",
                    Set.of(),
                    java.util.Map.of(),
                    null,
                    Configuration.ConfigurationStatus.ACTIVE,
                    "user1",
                    Instant.now(),
                    "user1",
                    Instant.now(),
                    null,
                    Set.of()
            );

            var result = policy.validate(config);

            assertFalse(result.isValid());
            assertTrue(result.errors().contains("Encrypted configurations must have string values"));
        }

        @Test
        @DisplayName("Should fail encrypted configuration with empty string")
        void shouldFailEncryptedConfigurationWithEmptyString() {
            ConfigurationValidationPolicy policy = ConfigurationValidationPolicy.builder().build();
            Configuration config = new Configuration(
                    "id1",
                    "tenant-1",
                    "test.key",
                    "dev",
                    "default",
                    1,
                    "",
                    Configuration.ConfigurationDataType.STRING,
                    true,
                    false,
                    null,
                    "test config",
                    Set.of(),
                    java.util.Map.of(),
                    null,
                    Configuration.ConfigurationStatus.ACTIVE,
                    "user1",
                    Instant.now(),
                    "user1",
                    Instant.now(),
                    null,
                    Set.of()
            );

            var result = policy.validate(config);

            assertFalse(result.isValid());
            assertTrue(result.errors().contains("Encrypted configurations cannot have empty values"));
        }

        @Test
        @DisplayName("Should fail encrypted configuration with blank string")
        void shouldFailEncryptedConfigurationWithBlankString() {
            ConfigurationValidationPolicy policy = ConfigurationValidationPolicy.builder().build();
            Configuration config = new Configuration(
                    "id1",
                    "tenant-1",
                    "test.key",
                    "dev",
                    "default",
                    1,
                    "   ",
                    Configuration.ConfigurationDataType.STRING,
                    true,
                    false,
                    null,
                    "test config",
                    Set.of(),
                    java.util.Map.of(),
                    null,
                    Configuration.ConfigurationStatus.ACTIVE,
                    "user1",
                    Instant.now(),
                    "user1",
                    Instant.now(),
                    null,
                    Set.of()
            );

            var result = policy.validate(config);

            assertFalse(result.isValid());
            assertTrue(result.errors().contains("Encrypted configurations cannot have empty values"));
        }

        @Test
        @DisplayName("Should pass encrypted configuration with valid string")
        void shouldPassEncryptedConfigurationWithValidString() {
            ConfigurationValidationPolicy policy = ConfigurationValidationPolicy.builder().build();
            Configuration config = new Configuration(
                    "id1",
                    "tenant-1",
                    "test.key",
                    "dev",
                    "default",
                    1,
                    "encrypted-value",
                    Configuration.ConfigurationDataType.STRING,
                    true,
                    false,
                    null,
                    "test config",
                    Set.of(),
                    java.util.Map.of(),
                    null,
                    Configuration.ConfigurationStatus.ACTIVE,
                    "user1",
                    Instant.now(),
                    "user1",
                    Instant.now(),
                    null,
                    Set.of()
            );

            var result = policy.validate(config);

            assertTrue(result.isValid());
            assertTrue(result.errors().isEmpty());
        }
    }

    @Nested
    @DisplayName("Sensitive Key Validation")
    class SensitiveKeyTests {

        @ParameterizedTest
        @ValueSource(strings = {"api.password", "db.secret", "auth.token", "service.api-key", "user.private_key"})
        @DisplayName("Should detect sensitive keys")
        void shouldDetectSensitiveKeys(String key) {
            ConfigurationValidationPolicy policy = ConfigurationValidationPolicy.builder().build();

            assertTrue(policy.isSensitiveKey(key));
        }

        @ParameterizedTest
        @ValueSource(strings = {"api.timeout", "db.host", "auth.provider", "service.name"})
        @DisplayName("Should not detect non-sensitive keys")
        void shouldNotDetectNonSensitiveKeys(String key) {
            ConfigurationValidationPolicy policy = ConfigurationValidationPolicy.builder().build();

            assertFalse(policy.isSensitiveKey(key));
        }

        @Test
        @DisplayName("Should fail validation for sensitive key without encryption when enforced")
        void shouldFailSensitiveKeyWithoutEncryptionWhenEnforced() {
            ConfigurationValidationPolicy policy = ConfigurationValidationPolicy.builder()
                    .enforceEncryptionForSensitive(true)
                    .build();

            Configuration config = new Configuration(
                    "id1",
                    "tenant-1",
                    "api.password",
                    "dev",
                    "default",
                    1,
                    "my-password",
                    Configuration.ConfigurationDataType.STRING,
                    false,
                    false,
                    null,
                    "test config",
                    Set.of(),
                    java.util.Map.of(),
                    null,
                    Configuration.ConfigurationStatus.ACTIVE,
                    "user1",
                    Instant.now(),
                    "user1",
                    Instant.now(),
                    null,
                    Set.of()
            );

            var result = policy.validate(config);

            assertFalse(result.isValid());
            assertTrue(result.errors().stream()
                    .anyMatch(error -> error.contains("sensitive") && error.contains("must be encrypted")));
        }

        @Test
        @DisplayName("Should pass validation for sensitive key without encryption when not enforced")
        void shouldPassSensitiveKeyWithoutEncryptionWhenNotEnforced() {
            ConfigurationValidationPolicy policy = ConfigurationValidationPolicy.builder()
                    .enforceEncryptionForSensitive(false)
                    .build();

            Configuration config = new Configuration(
                    "id1",
                    "tenant-1",
                    "api.password",
                    "dev",
                    "default",
                    1,
                    "my-password",
                    Configuration.ConfigurationDataType.STRING,
                    false,
                    false,
                    null,
                    "test config",
                    Set.of(),
                    java.util.Map.of(),
                    null,
                    Configuration.ConfigurationStatus.ACTIVE,
                    "user1",
                    Instant.now(),
                    "user1",
                    Instant.now(),
                    null,
                    Set.of()
            );

            var result = policy.validate(config);

            assertTrue(result.isValid());
            assertTrue(result.errors().isEmpty());
        }
    }

    @Nested
    @DisplayName("Protected Key Validation")
    class ProtectedKeyTests {

        @Test
        @DisplayName("Should detect protected keys")
        void shouldDetectProtectedKeys() {
            ConfigurationValidationPolicy policy = ConfigurationValidationPolicy.builder().build();

            assertTrue(policy.isProtectedKey("system.tenant.id"));
            assertTrue(policy.isProtectedKey("system.service.name"));
            assertTrue(policy.isProtectedKey("system.environment"));
        }

        @Test
        @DisplayName("Should be case-insensitive for protected keys")
        void shouldBeCaseInsensitiveForProtectedKeys() {
            ConfigurationValidationPolicy policy = ConfigurationValidationPolicy.builder().build();

            assertTrue(policy.isProtectedKey("SYSTEM.TENANT.ID"));
            assertTrue(policy.isProtectedKey("System.Tenant.Id"));
        }

        @Test
        @DisplayName("Should warn for protected key modification when approval required")
        void shouldWarnForProtectedKeyModification() {
            ConfigurationValidationPolicy policy = ConfigurationValidationPolicy.builder()
                    .requireApprovalForProtectedKeys(true)
                    .build();

            Configuration config = new Configuration(
                    "id1",
                    "tenant-1",
                    "system.tenant.id",
                    "dev",
                    "default",
                    1,
                    "tenant-123",
                    Configuration.ConfigurationDataType.STRING,
                    false,
                    false,
                    null,
                    "test config",
                    Set.of(),
                    java.util.Map.of(),
                    null,
                    Configuration.ConfigurationStatus.ACTIVE,
                    "user1",
                    Instant.now(),
                    "user1",
                    Instant.now(),
                    null,
                    Set.of()
            );

            var result = policy.validate(config);

            assertTrue(result.warnings().stream()
                    .anyMatch(warning -> warning.contains("protected") && warning.contains("requires approval")));
        }
    }

    @Nested
    @DisplayName("Schema Validation")
    class SchemaValidationTests {

        @Test
        @DisplayName("Should validate against schema with allowed values")
        void shouldValidateAgainstSchemaWithAllowedValues() {
            ConfigurationValidationPolicy policy = ConfigurationValidationPolicy.builder().build();
            ConfigurationSchema schema = new ConfigurationSchema.StringSchema(
                    ConfigurationDataType.STRING,
                    true,
                    "test schema",
                    Set.of("value1", "value2", "value3"),
                    java.util.Map.of(),
                    null,
                    null,
                    null,
                    null
            );

            Configuration config = new Configuration(
                    "id1",
                    "tenant-1",
                    "test.key",
                    "dev",
                    "default",
                    1,
                    "invalid-value",
                    Configuration.ConfigurationDataType.STRING,
                    false,
                    false,
                    null,
                    "test config",
                    Set.of(),
                    java.util.Map.of(),
                    schema,
                    Configuration.ConfigurationStatus.ACTIVE,
                    "user1",
                    Instant.now(),
                    "user1",
                    Instant.now(),
                    null,
                    Set.of()
            );

            var result = policy.validate(config);

            assertFalse(result.isValid());
            assertTrue(result.errors().stream()
                    .anyMatch(error -> error.contains("not in allowed values")));
        }

        @Test
        @DisplayName("Should pass validation for value in allowed list")
        void shouldPassValidationForValueInAllowedList() {
            ConfigurationValidationPolicy policy = ConfigurationValidationPolicy.builder().build();
            ConfigurationSchema schema = new ConfigurationSchema.StringSchema(
                    ConfigurationDataType.STRING,
                    true,
                    "test schema",
                    Set.of("value1", "value2", "value3"),
                    java.util.Map.of(),
                    null,
                    null,
                    null,
                    null
            );

            Configuration config = new Configuration(
                    "id1",
                    "tenant-1",
                    "test.key",
                    "dev",
                    "default",
                    1,
                    "value1",
                    Configuration.ConfigurationDataType.STRING,
                    false,
                    false,
                    null,
                    "test config",
                    Set.of(),
                    java.util.Map.of(),
                    schema,
                    Configuration.ConfigurationStatus.ACTIVE,
                    "user1",
                    Instant.now(),
                    "user1",
                    Instant.now(),
                    null,
                    Set.of()
            );

            var result = policy.validate(config);

            assertTrue(result.isValid());
        }

        @Test
        @DisplayName("Should fail when required schema constraint is not met")
        void shouldFailWhenRequiredSchemaConstraintNotMet() {
            ConfigurationValidationPolicy policy = ConfigurationValidationPolicy.builder().build();
            ConfigurationSchema schema = new ConfigurationSchema.StringSchema(
                    ConfigurationDataType.STRING,
                    true,
                    "test schema",
                    Set.of(),
                    java.util.Map.of(),
                    null,
                    null,
                    null,
                    null
            );

            Configuration config = new Configuration(
                    "id1",
                    "tenant-1",
                    "test.key",
                    "dev",
                    "default",
                    1,
                    null,
                    Configuration.ConfigurationDataType.STRING,
                    false,
                    false,
                    null,
                    "test config",
                    Set.of(),
                    java.util.Map.of(),
                    schema,
                    Configuration.ConfigurationStatus.ACTIVE,
                    "user1",
                    Instant.now(),
                    "user1",
                    Instant.now(),
                    null,
                    Set.of()
            );

            var result = policy.validate(config);

            assertFalse(result.isValid());
        }
    }

    @Nested
    @DisplayName("Production Environment Validation")
    class ProductionEnvironmentTests {

        @Test
        @DisplayName("Should warn for production config without description")
        void shouldWarnForProductionConfigWithoutDescription() {
            ConfigurationValidationPolicy policy = ConfigurationValidationPolicy.builder().build();
            Configuration config = new Configuration(
                    "id1",
                    "tenant-1",
                    "test.key",
                    "production",
                    "default",
                    1,
                    "value",
                    Configuration.ConfigurationDataType.STRING,
                    false,
                    false,
                    null,
                    null,
                    Set.of(),
                    java.util.Map.of(),
                    null,
                    Configuration.ConfigurationStatus.ACTIVE,
                    "user1",
                    Instant.now(),
                    "user1",
                    Instant.now(),
                    null,
                    Set.of()
            );

            var result = policy.validate(config);

            assertTrue(result.warnings().stream()
                    .anyMatch(warning -> warning.contains("Production") && warning.contains("descriptions")));
        }

        @Test
        @DisplayName("Should warn for production config with old validation date")
        void shouldWarnForProductionConfigWithOldValidationDate() {
            ConfigurationValidationPolicy policy = ConfigurationValidationPolicy.builder().build();
            Configuration config = new Configuration(
                    "id1",
                    "tenant-1",
                    "test.key",
                    "production",
                    "default",
                    1,
                    "value",
                    Configuration.ConfigurationDataType.STRING,
                    false,
                    false,
                    null,
                    "test config",
                    Set.of(),
                    java.util.Map.of(),
                    null,
                    Configuration.ConfigurationStatus.ACTIVE,
                    "user1",
                    Instant.now(),
                    "user1",
                    Instant.now(),
                    Instant.now().minusSeconds(86400 * 35),
                    Set.of()
            );

            var result = policy.validate(config);

            assertTrue(result.warnings().stream()
                    .anyMatch(warning -> warning.contains("validated within the last 30 days")));
        }

        @Test
        @DisplayName("Should recognize 'prod' as production environment")
        void shouldRecognizeProdAsProductionEnvironment() {
            ConfigurationValidationPolicy policy = ConfigurationValidationPolicy.builder().build();
            Configuration config = new Configuration(
                    "id1",
                    "tenant-1",
                    "test.key",
                    "prod",
                    "default",
                    1,
                    "value",
                    Configuration.ConfigurationDataType.STRING,
                    false,
                    false,
                    null,
                    null,
                    Set.of(),
                    java.util.Map.of(),
                    null,
                    Configuration.ConfigurationStatus.ACTIVE,
                    "user1",
                    Instant.now(),
                    "user1",
                    Instant.now(),
                    null,
                    Set.of()
            );

            var result = policy.validate(config);

            assertTrue(result.warnings().stream()
                    .anyMatch(warning -> warning.contains("Production")));
        }
    }

    @Nested
    @DisplayName("Data Type Validation")
    class DataTypeValidationTests {

        @Test
        @DisplayName("Should fail when value type does not match STRING data type")
        void shouldFailWhenValueTypeDoesNotMatchString() {
            ConfigurationValidationPolicy policy = ConfigurationValidationPolicy.builder().build();
            Configuration config = new Configuration(
                    "id1",
                    "tenant-1",
                    "test.key",
                    "dev",
                    "default",
                    1,
                    123,
                    Configuration.ConfigurationDataType.STRING,
                    false,
                    false,
                    null,
                    "test config",
                    Set.of(),
                    java.util.Map.of(),
                    null,
                    Configuration.ConfigurationStatus.ACTIVE,
                    "user1",
                    Instant.now(),
                    "user1",
                    Instant.now(),
                    null,
                    Set.of()
            );

            var result = policy.validate(config);

            assertFalse(result.isValid());
            assertTrue(result.errors().contains("Value must be String for data type STRING"));
        }

        @Test
        @DisplayName("Should fail when value type does not match NUMBER data type")
        void shouldFailWhenValueTypeDoesNotMatchNumber() {
            ConfigurationValidationPolicy policy = ConfigurationValidationPolicy.builder().build();
            Configuration config = new Configuration(
                    "id1",
                    "tenant-1",
                    "test.key",
                    "dev",
                    "default",
                    1,
                    "not-a-number",
                    Configuration.ConfigurationDataType.NUMBER,
                    false,
                    false,
                    null,
                    "test config",
                    Set.of(),
                    java.util.Map.of(),
                    null,
                    Configuration.ConfigurationStatus.ACTIVE,
                    "user1",
                    Instant.now(),
                    "user1",
                    Instant.now(),
                    null,
                    Set.of()
            );

            var result = policy.validate(config);

            assertFalse(result.isValid());
            assertTrue(result.errors().contains("Value must be Number for data type NUMBER"));
        }

        @Test
        @DisplayName("Should fail when value type does not match BOOLEAN data type")
        void shouldFailWhenValueTypeDoesNotMatchBoolean() {
            ConfigurationValidationPolicy policy = ConfigurationValidationPolicy.builder().build();
            Configuration config = new Configuration(
                    "id1",
                    "tenant-1",
                    "test.key",
                    "dev",
                    "default",
                    1,
                    "true",
                    Configuration.ConfigurationDataType.BOOLEAN,
                    false,
                    false,
                    null,
                    "test config",
                    Set.of(),
                    java.util.Map.of(),
                    null,
                    Configuration.ConfigurationStatus.ACTIVE,
                    "user1",
                    Instant.now(),
                    "user1",
                    Instant.now(),
                    null,
                    Set.of()
            );

            var result = policy.validate(config);

            assertFalse(result.isValid());
            assertTrue(result.errors().contains("Value must be Boolean for data type BOOLEAN"));
        }

        @Test
        @DisplayName("Should fail when value type does not match ARRAY data type")
        void shouldFailWhenValueTypeDoesNotMatchArray() {
            ConfigurationValidationPolicy policy = ConfigurationValidationPolicy.builder().build();
            Configuration config = new Configuration(
                    "id1",
                    "tenant-1",
                    "test.key",
                    "dev",
                    "default",
                    1,
                    "not-an-array",
                    Configuration.ConfigurationDataType.ARRAY,
                    false,
                    false,
                    null,
                    "test config",
                    Set.of(),
                    java.util.Map.of(),
                    null,
                    Configuration.ConfigurationStatus.ACTIVE,
                    "user1",
                    Instant.now(),
                    "user1",
                    Instant.now(),
                    null,
                    Set.of()
            );

            var result = policy.validate(config);

            assertFalse(result.isValid());
            assertTrue(result.errors().contains("Value must be Collection or Array for data type ARRAY"));
        }

        @Test
        @DisplayName("Should pass when value type matches data type")
        void shouldPassWhenValueTypeMatchesDataType() {
            ConfigurationValidationPolicy policy = ConfigurationValidationPolicy.builder().build();
            Configuration config = new Configuration(
                    "id1",
                    "tenant-1",
                    "test.key",
                    "dev",
                    "default",
                    1,
                    123,
                    Configuration.ConfigurationDataType.NUMBER,
                    false,
                    false,
                    null,
                    "test config",
                    Set.of(),
                    java.util.Map.of(),
                    null,
                    Configuration.ConfigurationStatus.ACTIVE,
                    "user1",
                    Instant.now(),
                    "user1",
                    Instant.now(),
                    null,
                    Set.of()
            );

            var result = policy.validate(config);

            assertTrue(result.isValid());
        }
    }

    @Nested
    @DisplayName("Change Validation")
    class ChangeValidationTests {

        @Test
        @DisplayName("Should fail change validation for incompatible type change")
        void shouldFailChangeValidationForIncompatibleTypeChange() {
            ConfigurationValidationPolicy policy = ConfigurationValidationPolicy.builder().build();
            Configuration oldConfig = new Configuration(
                    "id1",
                    "tenant-1",
                    "test.key",
                    "dev",
                    "default",
                    1,
                    "string-value",
                    Configuration.ConfigurationDataType.STRING,
                    false,
                    false,
                    null,
                    "test config",
                    Set.of(),
                    java.util.Map.of(),
                    null,
                    Configuration.ConfigurationStatus.ACTIVE,
                    "user1",
                    Instant.now(),
                    "user1",
                    Instant.now(),
                    null,
                    Set.of()
            );

            var result = policy.validateChange(
                    oldConfig,
                    123,
                    "test.key",
                    "production",
                    "user1"
            );

            assertFalse(result.isValid());
            assertTrue(result.errors().stream()
                    .anyMatch(error -> error.contains("Cannot change data type")));
        }

        @Test
        @DisplayName("Should pass change validation for compatible type change")
        void shouldPassChangeValidationForCompatibleTypeChange() {
            ConfigurationValidationPolicy policy = ConfigurationValidationPolicy.builder().build();
            Configuration oldConfig = new Configuration(
                    "id1",
                    "tenant-1",
                    "test.key",
                    "dev",
                    "default",
                    1,
                    100,
                    Configuration.ConfigurationDataType.NUMBER,
                    false,
                    false,
                    null,
                    "test config",
                    Set.of(),
                    java.util.Map.of(),
                    null,
                    Configuration.ConfigurationStatus.ACTIVE,
                    "user1",
                    Instant.now(),
                    "user1",
                    Instant.now(),
                    null,
                    Set.of()
            );

            var result = policy.validateChange(
                    oldConfig,
                    200,
                    "test.key",
                    "production",
                    "user1"
            );

            assertTrue(result.isValid());
        }

        @Test
        @DisplayName("Should warn for sensitive key change in production")
        void shouldWarnForSensitiveKeyChangeInProduction() {
            ConfigurationValidationPolicy policy = ConfigurationValidationPolicy.builder().build();

            var result = policy.validateChange(
                    null,
                    "new-value",
                    "api.password",
                    "production",
                    "user1"
            );

            assertTrue(result.warnings().stream()
                    .anyMatch(warning -> warning.contains("Sensitive") && warning.contains("production")));
        }
    }

    @Nested
    @DisplayName("ValidationResult Record Tests")
    class ValidationResultTests {

        @Test
        @DisplayName("Should create valid result")
        void shouldCreateValidResult() {
            var result = ConfigurationValidationPolicy.ValidationResult.valid();

            assertTrue(result.isValid());
            assertTrue(result.errors().isEmpty());
            assertTrue(result.warnings().isEmpty());
        }

        @Test
        @DisplayName("Should create invalid result with error")
        void shouldCreateInvalidResultWithError() {
            var result = ConfigurationValidationPolicy.ValidationResult.invalid("Test error");

            assertFalse(result.isValid());
            assertEquals(1, result.errors().size());
            assertTrue(result.errors().contains("Test error"));
        }

        @Test
        @DisplayName("Should create immutable error and warning sets")
        void shouldCreateImmutableErrorAndWarningSets() {
            var result = new ConfigurationValidationPolicy.ValidationResult(
                    true,
                    Set.of("error1", "error2"),
                    Set.of("warning1")
            );

            // Result sets should be immutable copies
            assertNotNull(result.errors());
            assertNotNull(result.warnings());
            assertEquals(2, result.errors().size());
            assertEquals(1, result.warnings().size());
        }
    }

    @Nested
    @DisplayName("ChangeValidationResult Record Tests")
    class ChangeValidationResultTests {

        @Test
        @DisplayName("Should create valid change result")
        void shouldCreateValidChangeResult() {
            var result = ConfigurationValidationPolicy.ChangeValidationResult.valid();

            assertTrue(result.isValid());
            assertTrue(result.errors().isEmpty());
            assertTrue(result.warnings().isEmpty());
        }

        @Test
        @DisplayName("Should create immutable change result sets")
        void shouldCreateImmutableChangeResultSets() {
            var result = new ConfigurationValidationPolicy.ChangeValidationResult(
                    false,
                    Set.of("error1"),
                    Set.of("warning1", "warning2")
            );

            assertEquals(1, result.errors().size());
            assertEquals(2, result.warnings().size());
        }
    }
}
