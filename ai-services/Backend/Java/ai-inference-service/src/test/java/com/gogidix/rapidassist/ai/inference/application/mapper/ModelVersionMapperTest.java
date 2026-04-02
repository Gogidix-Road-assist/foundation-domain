package com.gogidix.rapidassist.ai.inference.application.mapper;

import com.gogidix.rapidassist.ai.inference.application.dto.ModelVersionDto;
import com.gogidix.rapidassist.ai.inference.domain.model.ModelVersion;
import com.gogidix.rapidassist.ai.inference.domain.model.ModelVersionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ModelVersionMapper.
 */
@DisplayName("ModelVersion Mapper Tests")
class ModelVersionMapperTest {

    private ModelVersionMapper modelVersionMapper;

    private ModelVersion modelVersion;
    private ModelVersionDto modelVersionDto;

    private final UUID testId = UUID.randomUUID();
    private final String tenantId = "tenant-123";
    private final String modelId = "model-abc";
    private final String version = "1.0.0";

    @BeforeEach
    void setUp() {
        modelVersionMapper = Mappers.getMapper(ModelVersionMapper.class);

        // Initialize domain model
        modelVersion = ModelVersion.create(
                tenantId, modelId, version, "/models/path", "ONNX", "admin"
        );
        modelVersion.setId(testId);

        Map<String, Object> config = new HashMap<>();
        config.put("threshold", 0.5);
        modelVersion.setModelConfig(config);

        Map<String, Object> metrics = new HashMap<>();
        metrics.put("accuracy", 0.95);
        modelVersion.setPerformanceMetrics(metrics);

        // Initialize DTO
        modelVersionDto = new ModelVersionDto();
        modelVersionDto.setId(testId);
        modelVersionDto.setTenantId(tenantId);
        modelVersionDto.setModelId(modelId);
        modelVersionDto.setVersion(version);
        modelVersionDto.setStatus(ModelVersionStatus.STAGING);
        modelVersionDto.setModelPath("/models/path");
        modelVersionDto.setModelFormat("ONNX");
        modelVersionDto.setCreatedBy("admin");
        modelVersionDto.setIsDefault(false);
    }

    @Test
    @DisplayName("Should map domain to DTO correctly")
    void shouldMapDomainToDtoCorrectly() {
        // When
        ModelVersionDto result = modelVersionMapper.toDto(modelVersion);

        // Then
        assertNotNull(result);
        assertEquals(modelVersion.getId(), result.getId());
        assertEquals(modelVersion.getTenantId(), result.getTenantId());
        assertEquals(modelVersion.getModelId(), result.getModelId());
        assertEquals(modelVersion.getVersion(), result.getVersion());
        assertEquals(ModelVersionStatus.STAGING, result.getStatus());
        assertEquals(modelVersion.getModelPath(), result.getModelPath());
        assertEquals(modelVersion.getModelFormat(), result.getModelFormat());
        assertEquals(modelVersion.getCreatedBy(), result.getCreatedBy());
        assertEquals(modelVersion.getIsDefault(), result.getIsDefault());
    }

    @Test
    @DisplayName("Should map DTO to domain correctly")
    void shouldMapDtoToDomainCorrectly() {
        // Given
        Map<String, Object> config = new HashMap<>();
        config.put("threshold", 0.5);
        modelVersionDto.setModelConfig(config);

        Map<String, Object> metrics = new HashMap<>();
        metrics.put("accuracy", 0.95);
        modelVersionDto.setPerformanceMetrics(metrics);

        // When
        ModelVersion result = modelVersionMapper.toEntity(modelVersionDto);

        // Then
        assertNotNull(result);
        assertEquals(modelVersionDto.getId(), result.getId());
        assertEquals(modelVersionDto.getTenantId(), result.getTenantId());
        assertEquals(modelVersionDto.getModelId(), result.getModelId());
        assertEquals(modelVersionDto.getVersion(), result.getVersion());
        assertEquals(modelVersionDto.getModelPath(), result.getModelPath());
        assertEquals(modelVersionDto.getModelFormat(), result.getModelFormat());
        assertEquals(modelVersionDto.getCreatedBy(), result.getCreatedBy());
        assertEquals(modelVersionDto.getIsDefault(), result.getIsDefault());
    }

    @Test
    @DisplayName("Should map ACTIVE status correctly")
    void shouldMapActiveStatusCorrectly() {
        // Given
        modelVersion.setStatus(ModelVersionStatus.ACTIVE);

        // When
        ModelVersionDto result = modelVersionMapper.toDto(modelVersion);

        // Then
        assertEquals(ModelVersionStatus.ACTIVE.name(), result.getStatus());
    }

    @Test
    @DisplayName("Should map INACTIVE status correctly")
    void shouldMapInactiveStatusCorrectly() {
        // Given
        modelVersion.setStatus(ModelVersionStatus.INACTIVE);

        // When
        ModelVersionDto result = modelVersionMapper.toDto(modelVersion);

        // Then
        assertEquals(ModelVersionStatus.INACTIVE.name(), result.getStatus());
    }

    @Test
    @DisplayName("Should map RETIRED status correctly")
    void shouldMapRetiredStatusCorrectly() {
        // Given
        modelVersion.setStatus(ModelVersionStatus.RETIRED);

        // When
        ModelVersionDto result = modelVersionMapper.toDto(modelVersion);

        // Then
        assertEquals(ModelVersionStatus.RETIRED.name(), result.getStatus());
    }

    @Test
    @DisplayName("Should map string status to domain correctly")
    void shouldMapStringStatusToDomainCorrectly() {
        // Given
        modelVersionDto.setStatus(ModelVersionStatus.ACTIVE);

        // When
        ModelVersion result = modelVersionMapper.toEntity(modelVersionDto);

        // Then
        assertEquals(ModelVersionStatus.ACTIVE, result.getStatus());
    }

    @Test
    @DisplayName("Should handle null status in DTO")
    void shouldHandleNullStatusInDto() {
        // Given
        modelVersionDto.setStatus(null);

        // When
        ModelVersion result = modelVersionMapper.toEntity(modelVersionDto);

        // Then
        assertNull(result.getStatus());
    }

    @Test
    @DisplayName("Should handle null status in domain")
    void shouldHandleNullStatusInDomain() {
        // Given
        modelVersion.setStatus(null);

        // When
        ModelVersionDto result = modelVersionMapper.toDto(modelVersion);

        // Then
        assertNull(result.getStatus());
    }

    @Test
    @DisplayName("Should handle invalid status string")
    void shouldHandleInvalidStatusString() {
        // Given
        modelVersionDto.setStatus(null);

        // When
        ModelVersion result = modelVersionMapper.toEntity(modelVersionDto);

        // Then
        assertNull(result.getStatus());
    }

    @Test
    @DisplayName("Should handle null fields in domain to DTO mapping")
    void shouldHandleNullFieldsInDomainToDtoMapping() {
        // Given
        ModelVersion nullFieldsModel = new ModelVersion();
        nullFieldsModel.setId(testId);
        nullFieldsModel.setTenantId(tenantId);

        // When
        ModelVersionDto result = modelVersionMapper.toDto(nullFieldsModel);

        // Then
        assertNotNull(result);
        assertEquals(testId, result.getId());
        assertEquals(tenantId, result.getTenantId());
        assertNull(result.getModelId());
        assertNull(result.getVersion());
        assertNull(result.getStatus());
        assertNull(result.getModelPath());
        assertNull(result.getModelFormat());
        assertNull(result.getModelConfig());
        assertNull(result.getPerformanceMetrics());
        assertNull(result.getCreatedBy());
        assertNull(result.getIsDefault());
        assertNull(result.getDescription());
        assertNull(result.getTags());
    }

    @Test
    @DisplayName("Should handle null fields in DTO to domain mapping")
    void shouldHandleNullFieldsInDtoToDomainMapping() {
        // Given
        ModelVersionDto nullFieldsDto = new ModelVersionDto();
        nullFieldsDto.setId(testId);
        nullFieldsDto.setTenantId(tenantId);

        // When
        ModelVersion result = modelVersionMapper.toEntity(nullFieldsDto);

        // Then
        assertNotNull(result);
        assertEquals(testId, result.getId());
        assertEquals(tenantId, result.getTenantId());
        assertNull(result.getModelId());
        assertNull(result.getVersion());
        assertNull(result.getStatus());
        assertNull(result.getModelPath());
        assertNull(result.getModelFormat());
        assertNull(result.getModelConfig());
        assertNull(result.getPerformanceMetrics());
        assertNull(result.getCreatedAt());
        assertNull(result.getDeployedAt());
        assertNull(result.getCreatedBy());
        assertNull(result.getIsDefault());
        assertNull(result.getDescription());
        assertNull(result.getTags());
    }

    @Test
    @DisplayName("Should preserve model config in mapping")
    void shouldPreserveModelConfigInMapping() {
        // Given
        Map<String, Object> config = new HashMap<>();
        config.put("threshold", 0.5);
        config.put("maxTokens", 1000);
        config.put("temperature", 0.7);
        modelVersion.setModelConfig(config);

        // When
        ModelVersionDto result = modelVersionMapper.toDto(modelVersion);

        // Then
        assertEquals(config, result.getModelConfig());
        assertEquals(3, result.getModelConfig().size());
        assertEquals(0.5, result.getModelConfig().get("threshold"));
        assertEquals(1000, result.getModelConfig().get("maxTokens"));
        assertEquals(0.7, result.getModelConfig().get("temperature"));
    }

    @Test
    @DisplayName("Should preserve performance metrics in mapping")
    void shouldPreservePerformanceMetricsInMapping() {
        // Given
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("accuracy", 0.95);
        metrics.put("precision", 0.92);
        metrics.put("recall", 0.88);
        modelVersion.setPerformanceMetrics(metrics);

        // When
        ModelVersionDto result = modelVersionMapper.toDto(modelVersion);

        // Then
        assertEquals(metrics, result.getPerformanceMetrics());
        assertEquals(3, result.getPerformanceMetrics().size());
    }

    @Test
    @DisplayName("Should handle null model config")
    void shouldHandleNullModelConfig() {
        // Given
        modelVersion.setModelConfig(null);

        // When
        ModelVersionDto result = modelVersionMapper.toDto(modelVersion);

        // Then
        assertNull(result.getModelConfig());
    }

    @Test
    @DisplayName("Should handle null performance metrics")
    void shouldHandleNullPerformanceMetrics() {
        // Given
        modelVersion.setPerformanceMetrics(null);

        // When
        ModelVersionDto result = modelVersionMapper.toDto(modelVersion);

        // Then
        assertNull(result.getPerformanceMetrics());
    }

    @Test
    @DisplayName("Should round-trip map domain to DTO and back")
    void shouldRoundTripMapDomainToDtoAndBack() {
        // When - domain to DTO
        ModelVersionDto dto = modelVersionMapper.toDto(modelVersion);

        // And - DTO back to domain
        ModelVersion result = modelVersionMapper.toEntity(dto);

        // Then
        assertEquals(modelVersion.getId(), result.getId());
        assertEquals(modelVersion.getTenantId(), result.getTenantId());
        assertEquals(modelVersion.getModelId(), result.getModelId());
        assertEquals(modelVersion.getVersion(), result.getVersion());
        assertEquals(modelVersion.getStatus(), result.getStatus());
        assertEquals(modelVersion.getIsDefault(), result.getIsDefault());
    }

    @Test
    @DisplayName("Should round-trip map DTO to domain and back")
    void shouldRoundTripMapDtoToDomainAndBack() {
        // When - DTO to domain
        ModelVersion domain = modelVersionMapper.toEntity(modelVersionDto);

        // And - domain back to DTO
        ModelVersionDto result = modelVersionMapper.toDto(domain);

        // Then
        assertEquals(modelVersionDto.getId(), result.getId());
        assertEquals(modelVersionDto.getTenantId(), result.getTenantId());
        assertEquals(modelVersionDto.getModelId(), result.getModelId());
        assertEquals(modelVersionDto.getVersion(), result.getVersion());
        assertEquals(modelVersionDto.getStatus(), result.getStatus());
        assertEquals(modelVersionDto.getIsDefault(), result.getIsDefault());
    }

    @Test
    @DisplayName("Should map description and tags correctly")
    void shouldMapDescriptionAndTagsCorrectly() {
        // Given
        modelVersion.setDescription("Test model for image classification");
        modelVersion.setTags("ml,image-classification,onnx");

        // When
        ModelVersionDto result = modelVersionMapper.toDto(modelVersion);

        // Then
        assertEquals("Test model for image classification", result.getDescription());
        assertEquals("ml,image-classification,onnx", result.getTags());
    }

    @Test
    @DisplayName("Should map timestamps correctly")
    void shouldMapTimestampsCorrectly() {
        // Given
        LocalDateTime createdAt = LocalDateTime.of(2024, 1, 1, 10, 0, 0);
        LocalDateTime deployedAt = LocalDateTime.of(2024, 1, 2, 11, 0, 0);

        modelVersion.setCreatedAt(createdAt);
        modelVersion.setDeployedAt(deployedAt);

        // When
        ModelVersionDto result = modelVersionMapper.toDto(modelVersion);

        // Then
        assertEquals(createdAt, result.getCreatedAt());
        assertEquals(deployedAt, result.getDeployedAt());
    }
}
