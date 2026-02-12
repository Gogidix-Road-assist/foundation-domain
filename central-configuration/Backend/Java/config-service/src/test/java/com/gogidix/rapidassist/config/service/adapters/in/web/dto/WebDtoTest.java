package com.gogidix.rapidassist.config.service.adapters.in.web.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gogidix.rapidassist.config.service.adapters.in.web.dto.request.BulkUpdateRequest;
import com.gogidix.rapidassist.config.service.adapters.in.web.dto.request.ConfigurationUpdateRequest;
import com.gogidix.rapidassist.config.service.adapters.in.web.dto.request.CreateConfigurationRequest;
import com.gogidix.rapidassist.config.service.adapters.in.web.dto.request.UpdateConfigurationRequest;
import com.gogidix.rapidassist.config.service.adapters.in.web.dto.request.ValidateValueRequest;
import com.gogidix.rapidassist.config.service.adapters.in.web.dto.response.ValidationResult;
import com.gogidix.rapidassist.config.service.adapters.in.web.dto.response.ValueValidationResult;
import com.gogidix.rapidassist.config.service.domain.model.Configuration;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Web Layer DTOs.
 * Validates DTO structure, serialization, and business rules.
 */
class WebDtoTest {

    private final ObjectMapper objectMapper;

    WebDtoTest() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Nested
    class CreateConfigurationRequestTests {

        @Test
        void createConfigurationRequest_WithValidData_CreatesSuccessfully() {
            CreateConfigurationRequest request = new CreateConfigurationRequest(
                    "tenant-1",
                    "api.timeout",
                    "production",
                    "default",
                    "30",
                    Configuration.ConfigurationDataType.STRING,
                    false,
                    false,
                    null,
                    "API timeout",
                    Set.of("api", "timeout"),
                    Map.of("owner", "team-a"),
                    null,
                    "Initial setup"
            );

            assertEquals("tenant-1", request.tenantId());
            assertEquals("api.timeout", request.configKey());
            assertEquals("production", request.environment());
            assertEquals("default", request.namespace());
            assertEquals("30", request.value());
            assertEquals(Configuration.ConfigurationDataType.STRING, request.dataType());
            assertFalse(request.encrypted());
            assertFalse(request.required());
        }

        @Test
        void createConfigurationRequest_WithAllOptionalFields_CreatesSuccessfully() {
            CreateConfigurationRequest request = new CreateConfigurationRequest(
                    "tenant-1",
                    "api.timeout",
                    "production",
                    "default",
                    30000,
                    Configuration.ConfigurationDataType.NUMBER,
                    false,
                    true,
                    30000,
                    "API timeout in ms",
                    Set.of("api", "performance", "timeout"),
                    Map.of("owner", "team-api", "sensitivity", "high", "category", "performance"),
                    null,
                    "Initial configuration with all fields"
            );

            assertEquals(30000, request.value());
            assertEquals(30000, request.defaultValue());
            assertTrue(request.tags().size() >= 3);
            assertTrue(request.metadata().size() >= 3);
        }

        @Test
        void createConfigurationRequest_WithEncryptedString_CreatesSuccessfully() {
            CreateConfigurationRequest request = new CreateConfigurationRequest(
                    "tenant-1",
                    "db.password",
                    "production",
                    "database",
                    "secret123",
                    Configuration.ConfigurationDataType.STRING,
                    true,
                    true,
                    null,
                    "Database password",
                    Set.of("security", "database"),
                    Map.of(),
                    null,
                    "Add encrypted password"
            );

            assertTrue(request.encrypted());
            assertEquals(Configuration.ConfigurationDataType.STRING, request.dataType());
        }

        @Test
        void createConfigurationRequest_WithEncryptedObject_ThrowsException() {
            assertThrows(IllegalArgumentException.class, () ->
                    new CreateConfigurationRequest(
                            "tenant-1",
                            "config.data",
                            "production",
                            "default",
                            Map.of("key", "value"),
                            Configuration.ConfigurationDataType.OBJECT,
                            true,
                            false,
                            null,
                            null,
                            Set.of(),
                            Map.of(),
                            null,
                            null
                    )
            );
        }

        @Test
        void createConfigurationRequest_WithEncryptedArray_ThrowsException() {
            assertThrows(IllegalArgumentException.class, () ->
                    new CreateConfigurationRequest(
                            "tenant-1",
                            "config.list",
                            "production",
                            "default",
                            Set.of("a", "b"),
                            Configuration.ConfigurationDataType.ARRAY,
                            true,
                            false,
                            null,
                            null,
                            Set.of(),
                            Map.of(),
                            null,
                            null
                    )
            );
        }

        @Test
        void createConfigurationRequest_Serialization_DeserializesCorrectly() throws JsonProcessingException {
            CreateConfigurationRequest request = new CreateConfigurationRequest(
                    "tenant-1",
                    "api.timeout",
                    "production",
                    "default",
                    "30",
                    Configuration.ConfigurationDataType.STRING,
                    false,
                    false,
                    null,
                    null,
                    Set.of(),
                    Map.of(),
                    null,
                    null
            );

            String json = objectMapper.writeValueAsString(request);
            CreateConfigurationRequest deserialized = objectMapper.readValue(json, CreateConfigurationRequest.class);

            assertEquals(request.tenantId(), deserialized.tenantId());
            assertEquals(request.configKey(), deserialized.configKey());
            assertEquals(request.environment(), deserialized.environment());
            assertEquals(request.namespace(), deserialized.namespace());
        }
    }

    @Nested
    class UpdateConfigurationRequestTests {

        @Test
        void updateConfigurationRequest_WithRequiredFields_CreatesSuccessfully() {
            UpdateConfigurationRequest request = new UpdateConfigurationRequest(
                    "60",
                    "Increase timeout",
                    false
            );

            assertEquals("60", request.value());
            assertEquals("Increase timeout", request.reason());
            assertFalse(request.forceUpdate());
        }

        @Test
        void updateConfigurationRequest_WithForceUpdate_CreatesSuccessfully() {
            UpdateConfigurationRequest request = new UpdateConfigurationRequest(
                    "90",
                    "Forced timeout increase",
                    true
            );

            assertEquals("90", request.value());
            assertTrue(request.forceUpdate());
        }

        @Test
        void updateConfigurationRequest_WithNullReason_CreatesSuccessfully() {
            UpdateConfigurationRequest request = new UpdateConfigurationRequest(
                    "60",
                    null,
                    false
            );

            assertNull(request.reason());
        }

        @Test
        void updateConfigurationRequest_Serialization_DeserializesCorrectly() throws JsonProcessingException {
            UpdateConfigurationRequest request = new UpdateConfigurationRequest(
                    "60",
                    "Update timeout",
                    true
            );

            String json = objectMapper.writeValueAsString(request);
            UpdateConfigurationRequest deserialized = objectMapper.readValue(json, UpdateConfigurationRequest.class);

            assertEquals(request.value(), deserialized.value());
            assertEquals(request.reason(), deserialized.reason());
            assertEquals(request.forceUpdate(), deserialized.forceUpdate());
        }
    }

    @Nested
    class BulkUpdateRequestTests {

        @Test
        void bulkUpdateRequest_WithMultipleUpdates_CreatesSuccessfully() {
            List<ConfigurationUpdateRequest> updates = List.of(
                    new ConfigurationUpdateRequest("api.timeout", "production", "default", "60"),
                    new ConfigurationUpdateRequest("api.retry", "production", "default", "5"),
                    new ConfigurationUpdateRequest("cache.enabled", "production", "cache", "true")
            );

            BulkUpdateRequest request = new BulkUpdateRequest(
                    "tenant-1",
                    updates,
                    "Bulk update API configurations"
            );

            assertEquals("tenant-1", request.tenantId());
            assertEquals(3, request.updates().size());
            assertEquals("Bulk update API configurations", request.reason());
        }

        @Test
        void bulkUpdateRequest_WithSingleUpdate_CreatesSuccessfully() {
            BulkUpdateRequest request = new BulkUpdateRequest(
                    "tenant-1",
                    List.of(new ConfigurationUpdateRequest("api.timeout", "production", "default", "60")),
                    "Single update"
            );

            assertEquals(1, request.updates().size());
        }

        @Test
        void bulkUpdateRequest_WithNullReason_CreatesSuccessfully() {
            BulkUpdateRequest request = new BulkUpdateRequest(
                    "tenant-1",
                    List.of(new ConfigurationUpdateRequest("api.timeout", "production", "default", "60")),
                    null
            );

            assertNull(request.reason());
        }

        @Test
        void bulkUpdateRequest_Serialization_DeserializesCorrectly() throws JsonProcessingException {
            BulkUpdateRequest request = new BulkUpdateRequest(
                    "tenant-1",
                    List.of(new ConfigurationUpdateRequest("api.timeout", "production", "default", "60")),
                    "Bulk update"
            );

            String json = objectMapper.writeValueAsString(request);
            BulkUpdateRequest deserialized = objectMapper.readValue(json, BulkUpdateRequest.class);

            assertEquals(request.tenantId(), deserialized.tenantId());
            assertEquals(request.updates().size(), deserialized.updates().size());
            assertEquals(request.reason(), deserialized.reason());
        }
    }

    @Nested
    class ConfigurationUpdateRequestTests {

        @Test
        void configurationUpdateRequest_WithAllFields_CreatesSuccessfully() {
            ConfigurationUpdateRequest request = new ConfigurationUpdateRequest(
                    "api.timeout",
                    "production",
                    "default",
                    "60"
            );

            assertEquals("api.timeout", request.configKey());
            assertEquals("production", request.environment());
            assertEquals("default", request.namespace());
            assertEquals("60", request.value());
        }

        @Test
        void configurationUpdateRequest_WithDifferentDataTypes_CreatesSuccessfully() {
            ConfigurationUpdateRequest stringRequest = new ConfigurationUpdateRequest(
                    "config.string", "prod", "default", "value"
            );

            ConfigurationUpdateRequest intRequest = new ConfigurationUpdateRequest(
                    "config.int", "prod", "default", "123"
            );

            ConfigurationUpdateRequest boolRequest = new ConfigurationUpdateRequest(
                    "config.bool", "prod", "default", "true"
            );

            assertEquals("value", stringRequest.value());
            assertEquals("123", intRequest.value());
            assertEquals("true", boolRequest.value());
        }

        @Test
        void configurationUpdateRequest_Serialization_DeserializesCorrectly() throws JsonProcessingException {
            ConfigurationUpdateRequest request = new ConfigurationUpdateRequest(
                    "api.timeout",
                    "production",
                    "default",
                    "60"
            );

            String json = objectMapper.writeValueAsString(request);
            ConfigurationUpdateRequest deserialized = objectMapper.readValue(json, ConfigurationUpdateRequest.class);

            assertEquals(request.configKey(), deserialized.configKey());
            assertEquals(request.environment(), deserialized.environment());
            assertEquals(request.namespace(), deserialized.namespace());
            assertEquals(request.value(), deserialized.value());
        }
    }

    @Nested
    class ValidateValueRequestTests {

        @Test
        void validateValueRequest_WithValueOnly_CreatesSuccessfully() {
            ValidateValueRequest request = new ValidateValueRequest(
                    "30",
                    null
            );

            assertEquals("30", request.value());
            assertNull(request.schema());
        }

        @Test
        void validateValueRequest_WithSchema_CreatesSuccessfully() {
            ValidateValueRequest request = new ValidateValueRequest(
                    "30",
                    null
            );

            assertEquals("30", request.value());
            assertNull(request.schema());
        }

        @Test
        void validateValueRequest_Serialization_DeserializesCorrectly() throws JsonProcessingException {
            ValidateValueRequest request = new ValidateValueRequest(
                    "30",
                    null
            );

            String json = objectMapper.writeValueAsString(request);
            ValidateValueRequest deserialized = objectMapper.readValue(json, ValidateValueRequest.class);

            assertEquals(request.value(), deserialized.value());
            assertEquals(request.schema(), deserialized.schema());
        }
    }

    @Nested
    class ValidationResultTests {

        @Test
        void validationResult_Valid_CreatesSuccessfully() {
            ValidationResult result = new ValidationResult(true, Instant.now());

            assertTrue(result.valid());
            assertNotNull(result.validatedAt());
        }

        @Test
        void validationResult_Invalid_CreatesSuccessfully() {
            ValidationResult result = new ValidationResult(false, Instant.now());

            assertFalse(result.valid());
            assertNotNull(result.validatedAt());
        }

        @Test
        void validationResult_Serialization_DeserializesCorrectly() throws JsonProcessingException {
            Instant now = Instant.now();
            ValidationResult result = new ValidationResult(true, now);

            String json = objectMapper.writeValueAsString(result);
            ValidationResult deserialized = objectMapper.readValue(json, ValidationResult.class);

            assertEquals(result.valid(), deserialized.valid());
            assertEquals(result.validatedAt(), deserialized.validatedAt());
        }
    }

    @Nested
    class ValueValidationResultTests {

        @Test
        void valueValidationResult_ValidWithoutErrors_CreatesSuccessfully() {
            ValueValidationResult result = new ValueValidationResult(true, Set.of());

            assertTrue(result.valid());
            assertTrue(result.errors().isEmpty());
        }

        @Test
        void valueValidationResult_InvalidWithErrors_CreatesSuccessfully() {
            ValueValidationResult result = new ValueValidationResult(
                    false,
                    Set.of("Value must be a number", "Value must be positive")
            );

            assertFalse(result.valid());
            assertEquals(2, result.errors().size());
        }

        @Test
        void valueValidationResult_Serialization_DeserializesCorrectly() throws JsonProcessingException {
            ValueValidationResult result = new ValueValidationResult(
                    false,
                    Set.of("Error 1", "Error 2")
            );

            String json = objectMapper.writeValueAsString(result);
            ValueValidationResult deserialized = objectMapper.readValue(json, ValueValidationResult.class);

            assertEquals(result.valid(), deserialized.valid());
            assertEquals(result.errors(), deserialized.errors());
        }
    }

    @Nested
    class EdgeCaseTests {

        @Test
        void createConfigurationRequest_WithEmptyStrings_CreatesSuccessfully() {
            CreateConfigurationRequest request = new CreateConfigurationRequest(
                    "",
                    "",
                    "",
                    "",
                    "",
                    Configuration.ConfigurationDataType.STRING,
                    false,
                    false,
                    null,
                    "",
                    Set.of(),
                    Map.of(),
                    null,
                    ""
            );

            assertEquals("", request.tenantId());
            assertEquals("", request.configKey());
        }

        @Test
        void createConfigurationRequest_WithComplexMetadata_CreatesSuccessfully() {
            Map<String, Object> complexMetadata = Map.of(
                    "owner", "team-a",
                    "priority", 1,
                    "tags", Set.of("critical", "security"),
                    "nested", Map.of("key", "value")
            );

            CreateConfigurationRequest request = new CreateConfigurationRequest(
                    "tenant-1",
                    "api.config",
                    "production",
                    "default",
                    "value",
                    Configuration.ConfigurationDataType.STRING,
                    false,
                    false,
                    null,
                    null,
                    Set.of(),
                    complexMetadata,
                    null,
                    null
            );

            assertEquals(complexMetadata, request.metadata());
            assertEquals(4, request.metadata().size());
        }

        @Test
        void createConfigurationRequest_WithAllDataTypes_CreatesSuccessfully() {
            CreateConfigurationRequest stringRequest = new CreateConfigurationRequest(
                    "tenant-1", "str.config", "prod", "default", "value",
                    Configuration.ConfigurationDataType.STRING, false, false, null,
                    null, Set.of(), Map.of(), null, null
            );

            CreateConfigurationRequest numberRequest = new CreateConfigurationRequest(
                    "tenant-1", "num.config", "prod", "default", 123,
                    Configuration.ConfigurationDataType.NUMBER, false, false, null,
                    null, Set.of(), Map.of(), null, null
            );

            CreateConfigurationRequest boolRequest = new CreateConfigurationRequest(
                    "tenant-1", "bool.config", "prod", "default", true,
                    Configuration.ConfigurationDataType.BOOLEAN, false, false, null,
                    null, Set.of(), Map.of(), null, null
            );

            CreateConfigurationRequest jsonRequest = new CreateConfigurationRequest(
                    "tenant-1", "json.config", "prod", "default", "{\"key\":\"value\"}",
                    Configuration.ConfigurationDataType.JSON, false, false, null,
                    null, Set.of(), Map.of(), null, null
            );

            assertEquals(Configuration.ConfigurationDataType.STRING, stringRequest.dataType());
            assertEquals(Configuration.ConfigurationDataType.NUMBER, numberRequest.dataType());
            assertEquals(Configuration.ConfigurationDataType.BOOLEAN, boolRequest.dataType());
            assertEquals(Configuration.ConfigurationDataType.JSON, jsonRequest.dataType());
        }
    }
}
