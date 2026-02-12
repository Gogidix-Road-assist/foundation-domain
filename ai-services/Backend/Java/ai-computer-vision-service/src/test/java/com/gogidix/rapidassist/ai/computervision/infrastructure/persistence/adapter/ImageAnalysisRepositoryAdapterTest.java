package com.gogidix.rapidassist.ai.computervision.infrastructure.persistence.adapter;

import com.gogidix.rapidassist.ai.computervision.domain.aggregate.ImageAnalysisAggregate;
import com.gogidix.rapidassist.ai.computervision.domain.model.AnalysisStatus;
import com.gogidix.rapidassist.ai.computervision.domain.model.ImageFormat;
import com.gogidix.rapidassist.ai.computervision.infrastructure.persistence.entity.ImageAnalysisEntity;
import com.gogidix.rapidassist.ai.computervision.infrastructure.persistence.repository.ImageAnalysisMongoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Comprehensive unit tests for ImageAnalysisRepositoryAdapter
 * Tests all CRUD operations and queries
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ImageAnalysis Repository Adapter Tests")
class ImageAnalysisRepositoryAdapterTest {

    @Mock
    private ImageAnalysisMongoRepository mongoRepository;

    @InjectMocks
    private ImageAnalysisRepositoryAdapter adapter;

    private ImageAnalysisAggregate testAggregate;
    private ImageAnalysisEntity testEntity;
    private UUID testId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();

        testAggregate = ImageAnalysisAggregate.builder()
                .id(testId)
                .tenantId("tenant-123")
                .userId("user-456")
                .imageUrl("https://example.com/image.jpg")
                .imageStoragePath("/path/to/image.jpg")
                .format(ImageFormat.JPEG)
                .fileSize(1024000L)
                .width(1920)
                .height(1080)
                .status(AnalysisStatus.COMPLETED)
                .analysisType("OBJECT_DETECTION")
                .overallConfidence(0.85)
                .errorMessage(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .completedAt(LocalDateTime.now())
                .createdBy("user-456")
                .updatedBy("user-456")
                .processingTimeMs(5000L)
                .detectedObjects(new ArrayList<>())
                .detectedFaces(new ArrayList<>())
                .recognizedTexts(new ArrayList<>())
                .classifications(new ArrayList<>())
                .build();

        testEntity = ImageAnalysisEntity.builder()
                .id("entity-id")
                .uuid(testId)
                .tenantId("tenant-123")
                .userId("user-456")
                .imageUrl("https://example.com/image.jpg")
                .imageStoragePath("/path/to/image.jpg")
                .format(ImageFormat.JPEG)
                .fileSize(1024000L)
                .width(1920)
                .height(1080)
                .status(AnalysisStatus.COMPLETED)
                .analysisType("OBJECT_DETECTION")
                .overallConfidence(0.85)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .completedAt(LocalDateTime.now())
                .createdBy("user-456")
                .updatedBy("user-456")
                .processingTimeMs(5000L)
                .version(System.currentTimeMillis())
                .build();
    }

    @Test
    @DisplayName("Should save aggregate successfully")
    void shouldSaveAggregateSuccessfully() {
        // Given
        when(mongoRepository.save(any(ImageAnalysisEntity.class))).thenReturn(testEntity);

        // When
        ImageAnalysisAggregate result = adapter.save(testAggregate);

        // Then
        assertNotNull(result);
        assertEquals(testId, result.getId());
        assertEquals("tenant-123", result.getTenantId());
        assertEquals("COMPLETED", result.getStatus());
        verify(mongoRepository, times(1)).save(any(ImageAnalysisEntity.class));
    }

    @Test
    @DisplayName("Should find aggregate by ID")
    void shouldFindAggregateById() {
        // Given
        when(mongoRepository.findByUuid(testId)).thenReturn(Optional.of(testEntity));

        // When
        Optional<ImageAnalysisAggregate> result = adapter.findById(testId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testId, result.get().getId());
        assertEquals("tenant-123", result.get().getTenantId());
        verify(mongoRepository, times(1)).findByUuid(testId);
    }

    @Test
    @DisplayName("Should return empty when aggregate not found by ID")
    void shouldReturnEmptyWhenAggregateNotFoundById() {
        // Given
        when(mongoRepository.findByUuid(testId)).thenReturn(Optional.empty());

        // When
        Optional<ImageAnalysisAggregate> result = adapter.findById(testId);

        // Then
        assertFalse(result.isPresent());
        verify(mongoRepository, times(1)).findByUuid(testId);
    }

    @Test
    @DisplayName("Should find aggregate by tenant ID and ID")
    void shouldFindAggregateByTenantIdAndId() {
        // Given
        when(mongoRepository.findByTenantIdAndUuid("tenant-123", testId))
                .thenReturn(Optional.of(testEntity));

        // When
        Optional<ImageAnalysisAggregate> result = adapter.findByTenantIdAndId("tenant-123", testId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testId, result.get().getId());
        assertEquals("tenant-123", result.get().getTenantId());
        verify(mongoRepository, times(1)).findByTenantIdAndUuid("tenant-123", testId);
    }

    @Test
    @DisplayName("Should return empty when aggregate not found by tenant ID and ID")
    void shouldReturnEmptyWhenAggregateNotFoundByTenantIdAndId() {
        // Given
        when(mongoRepository.findByTenantIdAndUuid("tenant-123", testId))
                .thenReturn(Optional.empty());

        // When
        Optional<ImageAnalysisAggregate> result = adapter.findByTenantIdAndId("tenant-123", testId);

        // Then
        assertFalse(result.isPresent());
        verify(mongoRepository, times(1)).findByTenantIdAndUuid("tenant-123", testId);
    }

    @Test
    @DisplayName("Should find aggregates by tenant ID")
    void shouldFindAggregatesByTenantId() {
        // Given
        List<ImageAnalysisEntity> entities = Arrays.asList(testEntity);
        when(mongoRepository.findByTenantId("tenant-123")).thenReturn(entities);

        // When
        List<ImageAnalysisAggregate> result = adapter.findByTenantId("tenant-123");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testId, result.get(0).getId());
        verify(mongoRepository, times(1)).findByTenantId("tenant-123");
    }

    @Test
    @DisplayName("Should return empty list when no aggregates found by tenant ID")
    void shouldReturnEmptyListWhenNoAggregatesFoundByTenantId() {
        // Given
        when(mongoRepository.findByTenantId("tenant-123")).thenReturn(Collections.emptyList());

        // When
        List<ImageAnalysisAggregate> result = adapter.findByTenantId("tenant-123");

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(mongoRepository, times(1)).findByTenantId("tenant-123");
    }

    @Test
    @DisplayName("Should find aggregates by user ID")
    void shouldFindAggregatesByUserId() {
        // Given
        List<ImageAnalysisEntity> entities = Arrays.asList(testEntity);
        when(mongoRepository.findByUserId("user-456")).thenReturn(entities);

        // When
        List<ImageAnalysisAggregate> result = adapter.findByUserId("user-456");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("user-456", result.get(0).getUserId());
        verify(mongoRepository, times(1)).findByUserId("user-456");
    }

    @Test
    @DisplayName("Should find aggregates by status")
    void shouldFindAggregatesByStatus() {
        // Given
        List<ImageAnalysisEntity> entities = Arrays.asList(testEntity);
        when(mongoRepository.findByStatus(AnalysisStatus.COMPLETED.name())).thenReturn(entities);

        // When
        List<ImageAnalysisAggregate> result = adapter.findByStatus(AnalysisStatus.COMPLETED.name());

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(AnalysisStatus.COMPLETED.name(), result.get(0).getStatus());
        verify(mongoRepository, times(1)).findByStatus(AnalysisStatus.COMPLETED.name());
    }

    @Test
    @DisplayName("Should find aggregates by tenant ID and status")
    void shouldFindAggregatesByTenantIdAndStatus() {
        // Given
        List<ImageAnalysisEntity> entities = Arrays.asList(testEntity);
        when(mongoRepository.findByTenantIdAndStatus("tenant-123", AnalysisStatus.COMPLETED.name()))
                .thenReturn(entities);

        // When
        List<ImageAnalysisAggregate> result = adapter.findByTenantIdAndStatus("tenant-123", AnalysisStatus.COMPLETED.name());

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("tenant-123", result.get(0).getTenantId());
        assertEquals(AnalysisStatus.COMPLETED.name(), result.get(0).getStatus());
        verify(mongoRepository, times(1)).findByTenantIdAndStatus("tenant-123", AnalysisStatus.COMPLETED.name());
    }

    @Test
    @DisplayName("Should find aggregates by analysis type")
    void shouldFindAggregatesByAnalysisType() {
        // Given
        List<ImageAnalysisEntity> entities = Arrays.asList(testEntity);
        when(mongoRepository.findByAnalysisType("OBJECT_DETECTION")).thenReturn(entities);

        // When
        List<ImageAnalysisAggregate> result = adapter.findByAnalysisType("OBJECT_DETECTION");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("OBJECT_DETECTION", result.get(0).getAnalysisType());
        verify(mongoRepository, times(1)).findByAnalysisType("OBJECT_DETECTION");
    }

    @Test
    @DisplayName("Should delete aggregate by ID")
    void shouldDeleteAggregateById() {
        // Given
        doNothing().when(mongoRepository).deleteByUuid(testId);

        // When
        adapter.deleteById(testId);

        // Then
        verify(mongoRepository, times(1)).deleteByUuid(testId);
    }

    @Test
    @DisplayName("Should check if aggregate exists by ID")
    void shouldCheckIfAggregateExistsById() {
        // Given
        when(mongoRepository.existsByUuid(testId)).thenReturn(true);

        // When
        boolean result = adapter.existsById(testId);

        // Then
        assertTrue(result);
        verify(mongoRepository, times(1)).existsByUuid(testId);
    }

    @Test
    @DisplayName("Should return false when aggregate does not exist by ID")
    void shouldReturnFalseWhenAggregateDoesNotExistById() {
        // Given
        when(mongoRepository.existsByUuid(testId)).thenReturn(false);

        // When
        boolean result = adapter.existsById(testId);

        // Then
        assertFalse(result);
        verify(mongoRepository, times(1)).existsByUuid(testId);
    }

    @Test
    @DisplayName("Should count aggregates by tenant ID")
    void shouldCountAggregatesByTenantId() {
        // Given
        when(mongoRepository.countByTenantId("tenant-123")).thenReturn(5L);

        // When
        long result = adapter.countByTenantId("tenant-123");

        // Then
        assertEquals(5L, result);
        verify(mongoRepository, times(1)).countByTenantId("tenant-123");
    }

    @Test
    @DisplayName("Should return zero count when no aggregates for tenant")
    void shouldReturnZeroCountWhenNoAggregatesForTenant() {
        // Given
        when(mongoRepository.countByTenantId("tenant-123")).thenReturn(0L);

        // When
        long result = adapter.countByTenantId("tenant-123");

        // Then
        assertEquals(0L, result);
        verify(mongoRepository, times(1)).countByTenantId("tenant-123");
    }

    @Test
    @DisplayName("Should handle multiple aggregates in list")
    void shouldHandleMultipleAggregatesInList() {
        // Given
        ImageAnalysisEntity entity2 = ImageAnalysisEntity.builder()
                .uuid(UUID.randomUUID())
                .tenantId("tenant-123")
                .userId("user-789")
                .status(AnalysisStatus.PROCESSING)
                .build();

        List<ImageAnalysisEntity> entities = Arrays.asList(testEntity, entity2);
        when(mongoRepository.findByTenantId("tenant-123")).thenReturn(entities);

        // When
        List<ImageAnalysisAggregate> result = adapter.findByTenantId("tenant-123");

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(mongoRepository, times(1)).findByTenantId("tenant-123");
    }

    @Test
    @DisplayName("Should map entity to aggregate correctly")
    void shouldMapEntityToAggregateCorrectly() {
        // Given
        when(mongoRepository.findByUuid(testId)).thenReturn(Optional.of(testEntity));

        // When
        Optional<ImageAnalysisAggregate> result = adapter.findById(testId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testId, result.get().getId());
        assertEquals(testEntity.getTenantId(), result.get().getTenantId());
        assertEquals(testEntity.getUserId(), result.get().getUserId());
        assertEquals(testEntity.getImageUrl(), result.get().getImageUrl());
        assertEquals(testEntity.getStatus().name(), result.get().getStatus());
    }

    @Test
    @DisplayName("Should map aggregate to entity correctly")
    void shouldMapAggregateToEntityCorrectly() {
        // Given
        when(mongoRepository.save(any(ImageAnalysisEntity.class))).thenReturn(testEntity);

        // When
        ImageAnalysisAggregate result = adapter.save(testAggregate);

        // Then
        assertNotNull(result);
        verify(mongoRepository, times(1)).save(argThat(entity ->
                entity.getUuid().equals(testId) &&
                entity.getTenantId().equals("tenant-123") &&
                entity.getUserId().equals("user-456")
        ));
    }

    @Test
    @DisplayName("Should handle null values in entity to aggregate conversion")
    void shouldHandleNullValuesInEntityToAggregateConversion() {
        // Given
        ImageAnalysisEntity entityWithNulls = ImageAnalysisEntity.builder()
                .uuid(testId)
                .tenantId("tenant-123")
                .status(AnalysisStatus.PENDING)
                .errorMessage(null)
                .overallConfidence(null)
                .build();

        when(mongoRepository.findByUuid(testId)).thenReturn(Optional.of(entityWithNulls));

        // When
        Optional<ImageAnalysisAggregate> result = adapter.findById(testId);

        // Then
        assertTrue(result.isPresent());
        assertNull(result.get().getErrorMessage());
        assertNull(result.get().getOverallConfidence());
    }
}
