package com.gogidix.rapidassist.ai.inference.application.service;

import com.gogidix.rapidassist.ai.inference.application.command.CreateModelVersionCommand;
import com.gogidix.rapidassist.ai.inference.application.command.DeployModelVersionCommand;
import com.gogidix.rapidassist.ai.inference.application.dto.ModelVersionDto;
import com.gogidix.rapidassist.ai.inference.application.mapper.ModelVersionMapper;
import com.gogidix.rapidassist.ai.inference.application.port.out.ModelVersionRepositoryPort;
import com.gogidix.rapidassist.ai.inference.domain.model.ModelVersion;
import com.gogidix.rapidassist.ai.inference.domain.model.ModelVersionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ModelVersionApplicationService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ModelVersion Application Service Tests")
class ModelVersionApplicationServiceTest {

    @Mock
    private ModelVersionRepositoryPort modelVersionRepository;

    @Mock
    private ModelVersionMapper modelVersionMapper;

    @InjectMocks
    private ModelVersionApplicationService modelVersionApplicationService;

    private ModelVersion modelVersion;
    private ModelVersionDto modelVersionDto;
    private final String tenantId = "tenant-123";
    private final String modelId = "model-abc";
    private final UUID testId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        modelVersion = ModelVersion.create(
                tenantId, modelId, "1.0.0", "/models/path", "ONNX", "admin"
        );
        modelVersion.setId(testId);

        modelVersionDto = new ModelVersionDto();
        modelVersionDto.setId(testId);
        modelVersionDto.setTenantId(tenantId);
        modelVersionDto.setModelId(modelId);
        modelVersionDto.setVersion("1.0.0");
        modelVersionDto.setStatus(ModelVersionStatus.STAGING);
    }

    @Test
    @DisplayName("Should create model version successfully")
    void shouldCreateModelVersionSuccessfully() {
        // Given
        CreateModelVersionCommand command = new CreateModelVersionCommand();
        command.setTenantId(tenantId);
        command.setModelId(modelId);
        command.setVersion("1.0.0");
        command.setModelPath("/models/path");
        command.setModelFormat("ONNX");
        command.setCreatedBy("admin");

        when(modelVersionRepository.save(any(ModelVersion.class))).thenReturn(modelVersion);
        when(modelVersionMapper.toDto(modelVersion)).thenReturn(modelVersionDto);

        // When
        ModelVersionDto result = modelVersionApplicationService.createModelVersion(command);

        // Then
        assertNotNull(result);
        assertEquals(tenantId, result.getTenantId());
        assertEquals(modelId, result.getModelId());

        verify(modelVersionRepository).save(any(ModelVersion.class));
        verify(modelVersionMapper).toDto(modelVersion);
    }

    @Test
    @DisplayName("Should get model version by ID")
    void shouldGetModelVersionById() {
        // Given
        when(modelVersionRepository.findById(testId)).thenReturn(Optional.of(modelVersion));
        when(modelVersionMapper.toDto(modelVersion)).thenReturn(modelVersionDto);

        // When
        ModelVersionDto result = modelVersionApplicationService.getModelVersionById(testId);

        // Then
        assertNotNull(result);
        assertEquals(testId, result.getId());

        verify(modelVersionRepository).findById(testId);
        verify(modelVersionMapper).toDto(modelVersion);
    }

    @Test
    @DisplayName("Should return null when model version not found")
    void shouldReturnNullWhenModelVersionNotFound() {
        // Given
        when(modelVersionRepository.findById(testId)).thenReturn(Optional.empty());

        // When
        ModelVersionDto result = modelVersionApplicationService.getModelVersionById(testId);

        // Then
        assertNull(result);
        verify(modelVersionRepository).findById(testId);
        verify(modelVersionMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("Should get model versions by tenant ID")
    void shouldGetModelVersionsByTenantId() {
        // Given
        ModelVersion version2 = ModelVersion.create(
                tenantId, modelId, "2.0.0", "/models/path2", "ONNX", "admin"
        );
        version2.setId(UUID.randomUUID());

        ModelVersionDto dto2 = new ModelVersionDto();
        dto2.setId(version2.getId());
        dto2.setVersion("2.0.0");

        when(modelVersionRepository.findByTenantId(tenantId))
                .thenReturn(Arrays.asList(modelVersion, version2));
        when(modelVersionMapper.toDto(modelVersion)).thenReturn(modelVersionDto);
        when(modelVersionMapper.toDto(version2)).thenReturn(dto2);

        // When
        List<ModelVersionDto> result = modelVersionApplicationService.getModelVersionsByTenant(tenantId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

        verify(modelVersionRepository).findByTenantId(tenantId);
        verify(modelVersionMapper, times(2)).toDto(any(ModelVersion.class));
    }

    @Test
    @DisplayName("Should get model versions by model ID")
    void shouldGetModelVersionsByModelId() {
        // Given
        ModelVersion version2 = ModelVersion.create(
                tenantId, modelId, "2.0.0", "/models/path2", "ONNX", "admin"
        );

        when(modelVersionRepository.findByModelId(modelId))
                .thenReturn(Arrays.asList(modelVersion, version2));
        when(modelVersionMapper.toDto(modelVersion)).thenReturn(modelVersionDto);

        ModelVersionDto dto2 = new ModelVersionDto();
        dto2.setId(UUID.randomUUID());
        dto2.setVersion("2.0.0");
        when(modelVersionMapper.toDto(version2)).thenReturn(dto2);

        // When
        List<ModelVersionDto> result = modelVersionApplicationService.getModelVersions(modelId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

        verify(modelVersionRepository).findByModelId(modelId);
    }

    @Test
    @DisplayName("Should deploy model version successfully")
    void shouldDeployModelVersionSuccessfully() {
        // Given
        DeployModelVersionCommand command = new DeployModelVersionCommand();
        command.setTenantId(tenantId);
        command.setModelId(modelId);
        command.setVersion("1.0.0");

        when(modelVersionRepository.findByModelIdAndVersion(modelId, "1.0.0"))
                .thenReturn(Optional.of(modelVersion));
        when(modelVersionRepository.save(any(ModelVersion.class))).thenReturn(modelVersion);
        when(modelVersionMapper.toDto(modelVersion)).thenReturn(modelVersionDto);

        // When
        ModelVersionDto result = modelVersionApplicationService.deployModelVersion(command);

        // Then
        assertNotNull(result);
        verify(modelVersionRepository).findByModelIdAndVersion(modelId, "1.0.0");
        verify(modelVersionRepository).save(any(ModelVersion.class));
        verify(modelVersionMapper).toDto(modelVersion);
    }

    @Test
    @DisplayName("Should retire model version successfully")
    void shouldRetireModelVersionSuccessfully() {
        // Given
        when(modelVersionRepository.findById(testId)).thenReturn(Optional.of(modelVersion));
        when(modelVersionRepository.save(any(ModelVersion.class))).thenReturn(modelVersion);

        // When
        modelVersionApplicationService.retireModelVersion(testId);

        // Then
        verify(modelVersionRepository).findById(testId);
        verify(modelVersionRepository).save(any(ModelVersion.class));
    }

    @Test
    @DisplayName("Should get default model version")
    void shouldGetDefaultModelVersion() {
        // Given
        when(modelVersionRepository.findDefaultVersionByModelId(modelId))
                .thenReturn(Optional.of(modelVersion));
        when(modelVersionMapper.toDto(modelVersion)).thenReturn(modelVersionDto);

        // When
        ModelVersionDto result = modelVersionApplicationService.getDefaultModelVersion(modelId);

        // Then
        assertNotNull(result);
        verify(modelVersionRepository).findDefaultVersionByModelId(modelId);
    }

    @Test
    @DisplayName("Should return empty list when no model versions found for tenant")
    void shouldReturnEmptyListWhenNoModelVersionsFoundForTenant() {
        // Given
        when(modelVersionRepository.findByTenantId(tenantId)).thenReturn(Arrays.asList());

        // When
        List<ModelVersionDto> result = modelVersionApplicationService.getModelVersionsByTenant(tenantId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(modelVersionRepository).findByTenantId(tenantId);
        verify(modelVersionMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("Should handle deploy command for non-existent version")
    void shouldHandleDeployCommandForNonExistentVersion() {
        // Given
        DeployModelVersionCommand command = new DeployModelVersionCommand();
        command.setTenantId(tenantId);
        command.setModelId(modelId);
        command.setVersion("1.0.0");

        when(modelVersionRepository.findByModelIdAndVersion(modelId, "1.0.0"))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            modelVersionApplicationService.deployModelVersion(command);
        });

        verify(modelVersionRepository).findByModelIdAndVersion(modelId, "1.0.0");
        verify(modelVersionRepository, never()).save(any());
    }
}
