package com.gogidix.rapidassist.ai.inference.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ModelVersion domain model.
 * Tests business logic, factory methods, and state transitions.
 */
@DisplayName("ModelVersion Domain Model Tests")
class ModelVersionDomainModelTest {

    private final String tenantId = "tenant-123";
    private final String modelId = "model-abc";
    private final String version = "1.0.0";
    private final String modelPath = "/models/model-abc-1.0.0.bin";
    private final String modelFormat = "ONNX";
    private final String createdBy = "admin";

    @Test
    @DisplayName("Should create ModelVersion using factory method")
    void shouldCreateModelVersionUsingFactoryMethod() {
        // When
        ModelVersion modelVersion = ModelVersion.create(
                tenantId, modelId, version, modelPath, modelFormat, createdBy
        );

        // Then
        assertNotNull(modelVersion);
        assertNotNull(modelVersion.getId());
        assertEquals(tenantId, modelVersion.getTenantId());
        assertEquals(modelId, modelVersion.getModelId());
        assertEquals(version, modelVersion.getVersion());
        assertEquals(modelPath, modelVersion.getModelPath());
        assertEquals(modelFormat, modelVersion.getModelFormat());
        assertEquals(ModelVersionStatus.STAGING, modelVersion.getStatus());
        assertEquals(createdBy, modelVersion.getCreatedBy());
        assertNotNull(modelVersion.getCreatedAt());
        assertNotNull(modelVersion.getModelConfig());
        assertNotNull(modelVersion.getPerformanceMetrics());
        assertFalse(modelVersion.getIsDefault());
        assertNull(modelVersion.getDeployedAt());
    }

    @Test
    @DisplayName("Should create ModelVersion using builder")
    void shouldCreateModelVersionUsingBuilder() {
        // When
        ModelVersion modelVersion = ModelVersion.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .modelId(modelId)
                .version(version)
                .status(ModelVersionStatus.ACTIVE)
                .modelPath(modelPath)
                .modelFormat(modelFormat)
                .isDefault(true)
                .description("Test model")
                .createdBy(createdBy)
                .createdAt(LocalDateTime.now())
                .deployedAt(LocalDateTime.now())
                .build();

        // Then
        assertNotNull(modelVersion);
        assertEquals(tenantId, modelVersion.getTenantId());
        assertEquals(ModelVersionStatus.ACTIVE, modelVersion.getStatus());
        assertTrue(modelVersion.getIsDefault());
        assertEquals("Test model", modelVersion.getDescription());
    }

    @Test
    @DisplayName("Should activate model version successfully")
    void shouldActivateModelVersionSuccessfully() {
        // Given
        ModelVersion modelVersion = ModelVersion.create(
                tenantId, modelId, version, modelPath, modelFormat, createdBy
        );
        assertEquals(ModelVersionStatus.STAGING, modelVersion.getStatus());
        assertNull(modelVersion.getDeployedAt());

        // When
        modelVersion.activate();

        // Then
        assertEquals(ModelVersionStatus.ACTIVE, modelVersion.getStatus());
        assertNotNull(modelVersion.getDeployedAt());
    }

    @Test
    @DisplayName("Should deactivate model version successfully")
    void shouldDeactivateModelVersionSuccessfully() {
        // Given
        ModelVersion modelVersion = ModelVersion.create(
                tenantId, modelId, version, modelPath, modelFormat, createdBy
        );
        modelVersion.activate();
        assertEquals(ModelVersionStatus.ACTIVE, modelVersion.getStatus());

        // When
        modelVersion.deactivate();

        // Then
        assertEquals(ModelVersionStatus.INACTIVE, modelVersion.getStatus());
    }

    @Test
    @DisplayName("Should retire model version successfully")
    void shouldRetireModelVersionSuccessfully() {
        // Given
        ModelVersion modelVersion = ModelVersion.create(
                tenantId, modelId, version, modelPath, modelFormat, createdBy
        );
        modelVersion.activate();

        // When
        modelVersion.retire();

        // Then
        assertEquals(ModelVersionStatus.RETIRED, modelVersion.getStatus());
    }

    @Test
    @DisplayName("Should check if model version is active")
    void shouldCheckIfModelVersionIsActive() {
        // Given
        ModelVersion modelVersion = ModelVersion.create(
                tenantId, modelId, version, modelPath, modelFormat, createdBy
        );

        // Then - initially not active
        assertFalse(modelVersion.isActive());

        // When
        modelVersion.activate();

        // Then - now active
        assertTrue(modelVersion.isActive());

        // When
        modelVersion.deactivate();

        // Then - no longer active
        assertFalse(modelVersion.isActive());
    }

    @Test
    @DisplayName("Should add model config successfully")
    void shouldAddModelConfigSuccessfully() {
        // Given
        ModelVersion modelVersion = ModelVersion.create(
                tenantId, modelId, version, modelPath, modelFormat, createdBy
        );

        // When
        modelVersion.addModelConfig("threshold", 0.5);
        modelVersion.addModelConfig("maxTokens", 1000);
        modelVersion.addModelConfig("temperature", 0.7);

        // Then
        assertEquals(3, modelVersion.getModelConfig().size());
        assertEquals(0.5, modelVersion.getModelConfig().get("threshold"));
        assertEquals(1000, modelVersion.getModelConfig().get("maxTokens"));
        assertEquals(0.7, modelVersion.getModelConfig().get("temperature"));
    }

    @Test
    @DisplayName("Should add performance metric successfully")
    void shouldAddPerformanceMetricSuccessfully() {
        // Given
        ModelVersion modelVersion = ModelVersion.create(
                tenantId, modelId, version, modelPath, modelFormat, createdBy
        );

        // When
        modelVersion.addPerformanceMetric("accuracy", 0.95);
        modelVersion.addPerformanceMetric("latency", 150);
        modelVersion.addPerformanceMetric("throughput", 1000);

        // Then
        assertEquals(3, modelVersion.getPerformanceMetrics().size());
        assertEquals(0.95, modelVersion.getPerformanceMetrics().get("accuracy"));
        assertEquals(150, modelVersion.getPerformanceMetrics().get("latency"));
        assertEquals(1000, modelVersion.getPerformanceMetrics().get("throughput"));
    }

    @Test
    @DisplayName("Should handle null model config when adding config")
    void shouldHandleNullModelConfigWhenAddingConfig() {
        // Given
        ModelVersion modelVersion = ModelVersion.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .modelId(modelId)
                .version(version)
                .modelConfig(null)
                .build();

        // When
        modelVersion.addModelConfig("key", "value");

        // Then
        assertNotNull(modelVersion.getModelConfig());
        assertEquals(1, modelVersion.getModelConfig().size());
        assertEquals("value", modelVersion.getModelConfig().get("key"));
    }

    @Test
    @DisplayName("Should handle null performance metrics when adding metric")
    void shouldHandleNullPerformanceMetricsWhenAddingMetric() {
        // Given
        ModelVersion modelVersion = ModelVersion.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .modelId(modelId)
                .version(version)
                .performanceMetrics(null)
                .build();

        // When
        modelVersion.addPerformanceMetric("accuracy", 0.95);

        // Then
        assertNotNull(modelVersion.getPerformanceMetrics());
        assertEquals(1, modelVersion.getPerformanceMetrics().size());
        assertEquals(0.95, modelVersion.getPerformanceMetrics().get("accuracy"));
    }

    @Test
    @DisplayName("Should create ModelVersion using all-args constructor")
    void shouldCreateModelVersionUsingAllArgsConstructor() {
        // Given
        UUID id = UUID.randomUUID();
        Map<String, Object> modelConfig = new HashMap<>();
        modelConfig.put("threshold", 0.5);

        Map<String, Object> performanceMetrics = new HashMap<>();
        performanceMetrics.put("accuracy", 0.95);

        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime deployedAt = LocalDateTime.now().plusHours(1);

        // When
        ModelVersion modelVersion = new ModelVersion(
                id, modelId, tenantId, version, ModelVersionStatus.ACTIVE,
                modelPath, modelFormat, modelConfig, performanceMetrics,
                createdAt, deployedAt, true, "Test model", createdBy, "ml,nlp"
        );

        // Then
        assertEquals(id, modelVersion.getId());
        assertEquals(modelId, modelVersion.getModelId());
        assertEquals(tenantId, modelVersion.getTenantId());
        assertEquals(version, modelVersion.getVersion());
        assertEquals(ModelVersionStatus.ACTIVE, modelVersion.getStatus());
        assertEquals(modelPath, modelVersion.getModelPath());
        assertEquals(modelFormat, modelVersion.getModelFormat());
        assertEquals(modelConfig, modelVersion.getModelConfig());
        assertEquals(performanceMetrics, modelVersion.getPerformanceMetrics());
        assertEquals(createdAt, modelVersion.getCreatedAt());
        assertEquals(deployedAt, modelVersion.getDeployedAt());
        assertTrue(modelVersion.getIsDefault());
        assertEquals("Test model", modelVersion.getDescription());
        assertEquals(createdBy, modelVersion.getCreatedBy());
        assertEquals("ml,nlp", modelVersion.getTags());
    }

    @Test
    @DisplayName("Should create ModelVersion using no-args constructor")
    void shouldCreateModelVersionUsingNoArgsConstructor() {
        // When
        ModelVersion modelVersion = new ModelVersion();

        // Then
        assertNotNull(modelVersion);
        assertNull(modelVersion.getId());
        assertNull(modelVersion.getTenantId());
        assertNull(modelVersion.getModelId());
        assertNull(modelVersion.getVersion());
        assertNull(modelVersion.getStatus());
        assertNull(modelVersion.getModelPath());
        assertNull(modelVersion.getModelFormat());
        assertNull(modelVersion.getModelConfig());
        assertNull(modelVersion.getPerformanceMetrics());
        assertNull(modelVersion.getCreatedAt());
        assertNull(modelVersion.getDeployedAt());
        assertNull(modelVersion.getIsDefault());
        assertNull(modelVersion.getDescription());
        assertNull(modelVersion.getCreatedBy());
        assertNull(modelVersion.getTags());
    }

    @Test
    @DisplayName("Should update model version fields using setters")
    void shouldUpdateModelVersionFieldsUsingSetters() {
        // Given
        ModelVersion modelVersion = new ModelVersion();
        UUID id = UUID.randomUUID();
        Map<String, Object> config = new HashMap<>();
        config.put("key", "value");

        // When
        modelVersion.setId(id);
        modelVersion.setTenantId(tenantId);
        modelVersion.setModelId(modelId);
        modelVersion.setVersion(version);
        modelVersion.setStatus(ModelVersionStatus.ACTIVE);
        modelVersion.setModelPath(modelPath);
        modelVersion.setModelFormat(modelFormat);
        modelVersion.setModelConfig(config);
        modelVersion.setPerformanceMetrics(new HashMap<>());
        modelVersion.setCreatedAt(LocalDateTime.now());
        modelVersion.setDeployedAt(LocalDateTime.now());
        modelVersion.setIsDefault(true);
        modelVersion.setDescription("Updated description");
        modelVersion.setCreatedBy(createdBy);
        modelVersion.setTags("test,tags");

        // Then
        assertEquals(id, modelVersion.getId());
        assertEquals(tenantId, modelVersion.getTenantId());
        assertEquals(modelId, modelVersion.getModelId());
        assertEquals(ModelVersionStatus.ACTIVE, modelVersion.getStatus());
        assertEquals("Updated description", modelVersion.getDescription());
        assertEquals("test,tags", modelVersion.getTags());
    }

    @Test
    @DisplayName("Should handle multiple status transitions correctly")
    void shouldHandleMultipleStatusTransitionsCorrectly() {
        // Given
        ModelVersion modelVersion = ModelVersion.create(
                tenantId, modelId, version, modelPath, modelFormat, createdBy
        );

        // Then - initial state
        assertEquals(ModelVersionStatus.STAGING, modelVersion.getStatus());

        // When - activate
        modelVersion.activate();
        assertEquals(ModelVersionStatus.ACTIVE, modelVersion.getStatus());
        assertNotNull(modelVersion.getDeployedAt());

        LocalDateTime firstDeployedAt = modelVersion.getDeployedAt();

        // When - deactivate
        modelVersion.deactivate();
        assertEquals(ModelVersionStatus.INACTIVE, modelVersion.getStatus());

        // When - reactivate
        modelVersion.activate();
        assertEquals(ModelVersionStatus.ACTIVE, modelVersion.getStatus());
        assertEquals(firstDeployedAt, modelVersion.getDeployedAt()); // Deployed at doesn't change

        // When - retire
        modelVersion.retire();
        assertEquals(ModelVersionStatus.RETIRED, modelVersion.getStatus());
    }

    @Test
    @DisplayName("Should handle model config with various data types")
    void shouldHandleModelConfigWithVariousDataTypes() {
        // Given
        ModelVersion modelVersion = ModelVersion.create(
                tenantId, modelId, version, modelPath, modelFormat, createdBy
        );

        // When
        modelVersion.addModelConfig("stringParam", "value");
        modelVersion.addModelConfig("intParam", 100);
        modelVersion.addModelConfig("doubleParam", 0.95);
        modelVersion.addModelConfig("booleanParam", true);
        modelVersion.addModelConfig("nullParam", null);

        // Then
        assertEquals(5, modelVersion.getModelConfig().size());
        assertEquals("value", modelVersion.getModelConfig().get("stringParam"));
        assertEquals(100, modelVersion.getModelConfig().get("intParam"));
        assertEquals(0.95, modelVersion.getModelConfig().get("doubleParam"));
        assertEquals(true, modelVersion.getModelConfig().get("booleanParam"));
        assertNull(modelVersion.getModelConfig().get("nullParam"));
    }

    @Test
    @DisplayName("Should handle performance metrics with various data types")
    void shouldHandlePerformanceMetricsWithVariousDataTypes() {
        // Given
        ModelVersion modelVersion = ModelVersion.create(
                tenantId, modelId, version, modelPath, modelFormat, createdBy
        );

        // When
        modelVersion.addPerformanceMetric("accuracy", 0.95);
        modelVersion.addPerformanceMetric("precision", 0.92);
        modelVersion.addPerformanceMetric("recall", 0.88);
        modelVersion.addPerformanceMetric("f1Score", 0.90);
        modelVersion.addPerformanceMetric("latencyMs", 150);
        modelVersion.addPerformanceMetric("throughput", 1000);
        modelVersion.addPerformanceMetric("modelSizeMB", 256.5);

        // Then
        assertEquals(7, modelVersion.getPerformanceMetrics().size());
        assertTrue((Double) modelVersion.getPerformanceMetrics().get("accuracy") > 0.9);
        assertTrue((Integer) modelVersion.getPerformanceMetrics().get("latencyMs") > 0);
    }

    @Test
    @DisplayName("Should verify Lombok @Data annotations")
    void shouldVerifyLombokDataAnnotations() {
        // Given
        UUID id = UUID.randomUUID();
        ModelVersion version1 = new ModelVersion();
        version1.setId(id);
        version1.setTenantId(tenantId);
        version1.setModelId(modelId);
        version1.setVersion(version);

        ModelVersion version2 = new ModelVersion();
        version2.setId(id);
        version2.setTenantId(tenantId);
        version2.setModelId(modelId);
        version2.setVersion(version);

        // Then - equals and hashCode
        assertEquals(version1, version2);
        assertEquals(version1.hashCode(), version2.hashCode());

        // Then - toString
        String toString = version1.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("ModelVersion") || toString.contains("modelId"));
    }

    @Test
    @DisplayName("Should handle enum values correctly")
    void shouldHandleEnumValuesCorrectly() {
        // Given
        ModelVersion modelVersion = ModelVersion.create(
                tenantId, modelId, version, modelPath, modelFormat, createdBy
        );

        // Then - verify all enum values
        assertEquals(4, ModelVersionStatus.values().length);
        assertEquals(ModelVersionStatus.STAGING, ModelVersionStatus.valueOf("STAGING"));
        assertEquals(ModelVersionStatus.ACTIVE, ModelVersionStatus.valueOf("ACTIVE"));
        assertEquals(ModelVersionStatus.INACTIVE, ModelVersionStatus.valueOf("INACTIVE"));
        assertEquals(ModelVersionStatus.RETIRED, ModelVersionStatus.valueOf("RETIRED"));
    }

    @Test
    @DisplayName("Should verify equality with different instances")
    void shouldVerifyEqualityWithDifferentInstances() {
        // Given
        UUID id = UUID.randomUUID();
        ModelVersion version1 = ModelVersion.builder().id(id).modelId(modelId).build();
        ModelVersion version2 = ModelVersion.builder().id(id).modelId(modelId).build();
        ModelVersion version3 = ModelVersion.builder().id(UUID.randomUUID()).modelId(modelId).build();

        // Then
        assertEquals(version1, version2);
        assertEquals(version1.hashCode(), version2.hashCode());
        assertNotEquals(version1, version3);
        assertNotEquals(version1, null);
        assertNotEquals(version1, new Object());
    }
}
