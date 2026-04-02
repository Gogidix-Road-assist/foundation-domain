package com.gogidix.rapidassist.ai.computervision.application.service;

import com.gogidix.rapidassist.ai.computervision.application.command.*;
import com.gogidix.rapidassist.ai.computervision.application.dto.ImageAnalysisDto;
import com.gogidix.rapidassist.ai.computervision.application.mapper.ImageAnalysisMapper;
import com.gogidix.rapidassist.ai.computervision.application.query.GetAvailableModelsQuery;
import com.gogidix.rapidassist.ai.computervision.application.query.GetImageAnalysisQuery;
import com.gogidix.rapidassist.ai.computervision.application.query.ListImageAnalysesQuery;
import com.gogidix.rapidassist.ai.computervision.domain.aggregate.ImageAnalysisAggregate;
import com.gogidix.rapidassist.ai.computervision.domain.model.ImageFormat;
import com.gogidix.rapidassist.ai.computervision.domain.repository.ImageAnalysisRepositoryPort;
import com.gogidix.rapidassist.ai.computervision.domain.model.AnalysisStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Comprehensive unit tests for ComputerVisionApplicationService
 * Tests all use cases with mocked dependencies
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ComputerVision Application Service Tests")
class ComputerVisionApplicationServiceTest {

    @Mock
    private ImageAnalysisRepositoryPort imageAnalysisRepository;

    @Mock
    private ImageAnalysisMapper mapper;

    @InjectMocks
    private ComputerVisionApplicationService service;

    private ImageAnalysisAggregate mockAggregate;
    private ImageAnalysisDto mockDto;

    @BeforeEach
    void setUp() {
        mockAggregate = ImageAnalysisAggregate.builder()
                .id(UUID.randomUUID())
                .tenantId("tenant-123")
                .userId("user-456")
                .imageUrl("https://example.com/image.jpg")
                .status(AnalysisStatus.COMPLETED)
                .overallConfidence(0.85)
                .build();

        mockDto = ImageAnalysisDto.builder()
                .id(mockAggregate.getId())
                .tenantId(mockAggregate.getTenantId())
                .userId(mockAggregate.getUserId())
                .imageUrl(mockAggregate.getImageUrl())
                .status(mockAggregate.getStatus())
                .confidenceScore(mockAggregate.getOverallConfidence())
                .build();
    }

    @Test
    @DisplayName("Should analyze image successfully")
    void shouldAnalyzeImageSuccessfully() {
        // Given
        AnalyzeImageCommand command = AnalyzeImageCommand.builder()
                .tenantId("tenant-123")
                .userId("user-456")
                .imageUrl("https://example.com/image.jpg")
                .imageStoragePath("/path/to/image.jpg")
                .format(ImageFormat.JPEG)
                .fileSize(1024000L)
                .width(1920)
                .height(1080)
                .analysisType("OBJECT_DETECTION")
                .createdBy("user-456")
                .build();

        when(imageAnalysisRepository.save(any(ImageAnalysisAggregate.class))).thenReturn(mockAggregate);
        when(mapper.toDto(any(ImageAnalysisAggregate.class))).thenReturn(mockDto);

        // When
        ImageAnalysisDto result = service.analyzeImage(command);

        // Then
        assertNotNull(result);
        assertEquals(mockDto.getId(), result.getId());
        assertEquals(mockDto.getTenantId(), result.getTenantId());
        verify(imageAnalysisRepository, times(1)).save(any(ImageAnalysisAggregate.class));
        verify(mapper, times(1)).toDto(any(ImageAnalysisAggregate.class));
    }

    @Test
    @DisplayName("Should detect objects successfully")
    void shouldDetectObjectsSuccessfully() {
        // Given
        DetectObjectsCommand command = DetectObjectsCommand.builder()
                .tenantId("tenant-123")
                .userId("user-456")
                .imageUrl("https://example.com/image.jpg")
                .imageStoragePath("/path/to/image.jpg")
                .width(1920)
                .height(1080)
                .minConfidence(0.7)
                .maxObjects(10)
                .createdBy("user-456")
                .build();

        when(imageAnalysisRepository.save(any(ImageAnalysisAggregate.class))).thenReturn(mockAggregate);
        when(mapper.toDto(any(ImageAnalysisAggregate.class))).thenReturn(mockDto);

        // When
        ImageAnalysisDto result = service.detectObjects(command);

        // Then
        assertNotNull(result);
        assertEquals(mockDto.getId(), result.getId());
        verify(imageAnalysisRepository, times(1)).save(any(ImageAnalysisAggregate.class));
        verify(mapper, times(1)).toDto(any(ImageAnalysisAggregate.class));
    }

    @Test
    @DisplayName("Should detect faces successfully")
    void shouldDetectFacesSuccessfully() {
        // Given
        DetectFacesCommand command = DetectFacesCommand.builder()
                .tenantId("tenant-123")
                .userId("user-456")
                .imageUrl("https://example.com/image.jpg")
                .imageStoragePath("/path/to/image.jpg")
                .width(1920)
                .height(1080)
                .detectEmotions(true)
                .detectAge(true)
                .detectGender(true)
                .detectLandmarks(false)
                .minConfidence(0.7)
                .createdBy("user-456")
                .build();

        when(imageAnalysisRepository.save(any(ImageAnalysisAggregate.class))).thenReturn(mockAggregate);
        when(mapper.toDto(any(ImageAnalysisAggregate.class))).thenReturn(mockDto);

        // When
        ImageAnalysisDto result = service.detectFaces(command);

        // Then
        assertNotNull(result);
        assertEquals(mockDto.getId(), result.getId());
        verify(imageAnalysisRepository, times(1)).save(any(ImageAnalysisAggregate.class));
        verify(mapper, times(1)).toDto(any(ImageAnalysisAggregate.class));
    }

    @Test
    @DisplayName("Should extract text successfully")
    void shouldExtractTextSuccessfully() {
        // Given
        ExtractTextCommand command = ExtractTextCommand.builder()
                .tenantId("tenant-123")
                .userId("user-456")
                .imageUrl("https://example.com/image.jpg")
                .imageStoragePath("/path/to/image.jpg")
                .language("en")
                .preserveLayout(true)
                .extractWords(true)
                .minConfidence(0.7)
                .createdBy("user-456")
                .build();

        when(imageAnalysisRepository.save(any(ImageAnalysisAggregate.class))).thenReturn(mockAggregate);
        when(mapper.toDto(any(ImageAnalysisAggregate.class))).thenReturn(mockDto);

        // When
        ImageAnalysisDto result = service.extractText(command);

        // Then
        assertNotNull(result);
        assertEquals(mockDto.getId(), result.getId());
        verify(imageAnalysisRepository, times(1)).save(any(ImageAnalysisAggregate.class));
        verify(mapper, times(1)).toDto(any(ImageAnalysisAggregate.class));
    }

    @Test
    @DisplayName("Should classify image successfully")
    void shouldClassifyImageSuccessfully() {
        // Given
        ClassifyImageCommand command = ClassifyImageCommand.builder()
                .tenantId("tenant-123")
                .userId("user-456")
                .imageUrl("https://example.com/image.jpg")
                .imageStoragePath("/path/to/image.jpg")
                .width(1920)
                .height(1080)
                .topPredictions(5)
                .minConfidence(0.7)
                .modelName("resnet50")
                .createdBy("user-456")
                .build();

        when(imageAnalysisRepository.save(any(ImageAnalysisAggregate.class))).thenReturn(mockAggregate);
        when(mapper.toDto(any(ImageAnalysisAggregate.class))).thenReturn(mockDto);

        // When
        ImageAnalysisDto result = service.classifyImage(command);

        // Then
        assertNotNull(result);
        assertEquals(mockDto.getId(), result.getId());
        verify(imageAnalysisRepository, times(1)).save(any(ImageAnalysisAggregate.class));
        verify(mapper, times(1)).toDto(any(ImageAnalysisAggregate.class));
    }

    @Test
    @DisplayName("Should get analysis by ID successfully")
    void shouldGetAnalysisByIdSuccessfully() {
        // Given
        UUID analysisId = mockAggregate.getId();
        GetImageAnalysisQuery query = GetImageAnalysisQuery.builder()
                .tenantId("tenant-123")
                .id(analysisId)
                .build();

        when(imageAnalysisRepository.findById(analysisId)).thenReturn(Optional.of(mockAggregate));
        when(mapper.toDto(mockAggregate)).thenReturn(mockDto);

        // When
        ImageAnalysisDto result = service.getAnalysis(query);

        // Then
        assertNotNull(result);
        assertEquals(mockDto.getId(), result.getId());
        verify(imageAnalysisRepository, times(1)).findById(analysisId);
        verify(mapper, times(1)).toDto(mockAggregate);
    }

    @Test
    @DisplayName("Should throw exception when analysis not found")
    void shouldThrowExceptionWhenAnalysisNotFound() {
        // Given
        UUID analysisId = UUID.randomUUID();
        GetImageAnalysisQuery query = GetImageAnalysisQuery.builder()
                .tenantId("tenant-123")
                .id(analysisId)
                .build();

        when(imageAnalysisRepository.findById(analysisId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> service.getAnalysis(query));
        verify(imageAnalysisRepository, times(1)).findById(analysisId);
        verify(mapper, never()).toDto(any(ImageAnalysisAggregate.class));
    }

    @Test
    @DisplayName("Should list analyses by tenant and status")
    void shouldListAnalysesByTenantAndStatus() {
        // Given
        ListImageAnalysesQuery query = ListImageAnalysesQuery.builder()
                .tenantId("tenant-123")
                .status(AnalysisStatus.COMPLETED.name())
                .page(0)
                .size(20)
                .build();

        List<ImageAnalysisAggregate> aggregates = Arrays.asList(mockAggregate);
        when(imageAnalysisRepository.findByTenantIdAndStatus("tenant-123", AnalysisStatus.COMPLETED.name()))
                .thenReturn(aggregates);
        when(mapper.toDto(any(ImageAnalysisAggregate.class))).thenReturn(mockDto);

        // When
        Page<ImageAnalysisDto> result = service.listAnalyses(query);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(mockDto.getId(), result.getContent().get(0).getId());
        verify(imageAnalysisRepository, times(1))
                .findByTenantIdAndStatus("tenant-123", AnalysisStatus.COMPLETED.name());
        verify(mapper, times(1)).toDto(any(ImageAnalysisAggregate.class));
    }

    @Test
    @DisplayName("Should list analyses by tenant only")
    void shouldListAnalysesByTenantOnly() {
        // Given
        ListImageAnalysesQuery query = ListImageAnalysesQuery.builder()
                .tenantId("tenant-123")
                .page(0)
                .size(20)
                .build();

        List<ImageAnalysisAggregate> aggregates = Arrays.asList(mockAggregate);
        when(imageAnalysisRepository.findByTenantId("tenant-123")).thenReturn(aggregates);
        when(mapper.toDto(any(ImageAnalysisAggregate.class))).thenReturn(mockDto);

        // When
        Page<ImageAnalysisDto> result = service.listAnalyses(query);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(imageAnalysisRepository, times(1)).findByTenantId("tenant-123");
        verify(mapper, times(1)).toDto(any(ImageAnalysisAggregate.class));
    }

    @Test
    @DisplayName("Should list analyses by user ID")
    void shouldListAnalysesByUserId() {
        // Given
        ListImageAnalysesQuery query = ListImageAnalysesQuery.builder()
                .userId("user-456")
                .page(0)
                .size(20)
                .build();

        List<ImageAnalysisAggregate> aggregates = Arrays.asList(mockAggregate);
        when(imageAnalysisRepository.findByUserId("user-456")).thenReturn(aggregates);
        when(mapper.toDto(any(ImageAnalysisAggregate.class))).thenReturn(mockDto);

        // When
        Page<ImageAnalysisDto> result = service.listAnalyses(query);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(imageAnalysisRepository, times(1)).findByUserId("user-456");
        verify(mapper, times(1)).toDto(any(ImageAnalysisAggregate.class));
    }

    @Test
    @DisplayName("Should list analyses by status only")
    void shouldListAnalysesByStatusOnly() {
        // Given
        ListImageAnalysesQuery query = ListImageAnalysesQuery.builder()
                .status(AnalysisStatus.PENDING.name())
                .page(0)
                .size(20)
                .build();

        List<ImageAnalysisAggregate> aggregates = Arrays.asList(mockAggregate);
        when(imageAnalysisRepository.findByStatus(AnalysisStatus.PENDING.name())).thenReturn(aggregates);
        when(mapper.toDto(any(ImageAnalysisAggregate.class))).thenReturn(mockDto);

        // When
        Page<ImageAnalysisDto> result = service.listAnalyses(query);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(imageAnalysisRepository, times(1)).findByStatus(AnalysisStatus.PENDING.name());
        verify(mapper, times(1)).toDto(any(ImageAnalysisAggregate.class));
    }

    @Test
    @DisplayName("Should paginate results correctly")
    void shouldPaginateResultsCorrectly() {
        // Given
        List<ImageAnalysisAggregate> aggregates = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            aggregates.add(mockAggregate);
        }

        ListImageAnalysesQuery query = ListImageAnalysesQuery.builder()
                .tenantId("tenant-123")
                .page(1)
                .size(10)
                .build();

        when(imageAnalysisRepository.findByTenantId("tenant-123")).thenReturn(aggregates);
        when(mapper.toDto(any(ImageAnalysisAggregate.class))).thenReturn(mockDto);

        // When
        Page<ImageAnalysisDto> result = service.listAnalyses(query);

        // Then
        assertNotNull(result);
        assertEquals(25, result.getTotalElements());
        assertEquals(10, result.getContent().size());
        assertEquals(2, result.getTotalPages());
    }

    @Test
    @DisplayName("Should get available models")
    void shouldGetAvailableModels() {
        // Given
        GetAvailableModelsQuery query = GetAvailableModelsQuery.builder()
                .modelType("object_detection")
                .build();

        // When
        Map<String, Object> result = service.getAvailableModels(query);

        // Then
        assertNotNull(result);
        assertTrue(result.containsKey("object_detection"));
        assertTrue(result.containsKey("face_detection"));
        assertTrue(result.containsKey("text_recognition"));
        assertTrue(result.containsKey("image_classification"));
    }

    @Test
    @DisplayName("Should return all available models when no type specified")
    void shouldReturnAllModelsWhenNoTypeSpecified() {
        // Given
        GetAvailableModelsQuery query = GetAvailableModelsQuery.builder().build();

        // When
        Map<String, Object> result = service.getAvailableModels(query);

        // Then
        assertNotNull(result);
        assertEquals(4, result.size());
    }

    @Test
    @DisplayName("Should handle empty analysis list")
    void shouldHandleEmptyAnalysisList() {
        // Given
        ListImageAnalysesQuery query = ListImageAnalysesQuery.builder()
                .tenantId("tenant-123")
                .page(0)
                .size(20)
                .build();

        when(imageAnalysisRepository.findByTenantId("tenant-123")).thenReturn(Collections.emptyList());

        // When
        Page<ImageAnalysisDto> result = service.listAnalyses(query);

        // Then
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getContent().size());
        verify(mapper, never()).toDto(any(ImageAnalysisAggregate.class));
    }

    @Test
    @DisplayName("Should verify repository calls order")
    void shouldVerifyRepositoryCallsOrder() {
        // Given
        AnalyzeImageCommand command = AnalyzeImageCommand.builder()
                .tenantId("tenant-123")
                .userId("user-456")
                .imageUrl("https://example.com/image.jpg")
                .format(ImageFormat.JPEG)
                .analysisType("OBJECT_DETECTION")
                .build();

        when(imageAnalysisRepository.save(any(ImageAnalysisAggregate.class))).thenReturn(mockAggregate);
        when(mapper.toDto(any(ImageAnalysisAggregate.class))).thenReturn(mockDto);

        // When
        service.analyzeImage(command);

        // Then
        var inOrder = org.mockito.Mockito.inOrder(imageAnalysisRepository, mapper);
        inOrder.verify(imageAnalysisRepository).save(any(ImageAnalysisAggregate.class));
        inOrder.verify(mapper).toDto(any(ImageAnalysisAggregate.class));
    }
}
