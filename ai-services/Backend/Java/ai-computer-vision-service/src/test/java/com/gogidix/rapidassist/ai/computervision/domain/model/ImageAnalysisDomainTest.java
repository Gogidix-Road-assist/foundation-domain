package com.gogidix.rapidassist.ai.computervision.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for ImageAnalysis domain model
 * Tests all business logic, getters, setters, and state transitions
 */
@DisplayName("ImageAnalysis Domain Model Tests")
class ImageAnalysisDomainTest {

    @Test
    @DisplayName("Should create ImageAnalysis with builder")
    void shouldCreateImageAnalysisWithBuilder() {
        // Given
        UUID id = UUID.randomUUID();
        String tenantId = "tenant-123";
        String userId = "user-456";
        String imageUrl = "https://example.com/image.jpg";
        ImageFormat format = ImageFormat.JPEG;
        AnalysisStatus status = AnalysisStatus.PENDING;

        // When
        ImageAnalysis analysis = ImageAnalysis.builder()
                .id(id)
                .tenantId(tenantId)
                .userId(userId)
                .imageUrl(imageUrl)
                .format(format)
                .status(status)
                .width(1920)
                .height(1080)
                .fileSize(1024000L)
                .createdAt(LocalDateTime.now())
                .build();

        // Then
        assertNotNull(analysis);
        assertEquals(id, analysis.getId());
        assertEquals(tenantId, analysis.getTenantId());
        assertEquals(userId, analysis.getUserId());
        assertEquals(imageUrl, analysis.getImageUrl());
        assertEquals(format, analysis.getFormat());
        assertEquals(status, analysis.getStatus());
        assertEquals(1920, analysis.getWidth());
        assertEquals(1080, analysis.getHeight());
        assertEquals(1024000L, analysis.getFileSize());
    }

    @Test
    @DisplayName("Should identify pending analysis correctly")
    void shouldIdentifyPendingAnalysis() {
        // Given
        ImageAnalysis analysis = ImageAnalysis.builder()
                .status(AnalysisStatus.PENDING)
                .build();

        // When & Then
        assertTrue(analysis.isPending());
        assertFalse(analysis.isProcessing());
        assertFalse(analysis.isCompleted());
        assertFalse(analysis.isFailed());
    }

    @Test
    @DisplayName("Should identify processing analysis correctly")
    void shouldIdentifyProcessingAnalysis() {
        // Given
        ImageAnalysis analysis = ImageAnalysis.builder()
                .status(AnalysisStatus.PROCESSING)
                .build();

        // When & Then
        assertTrue(analysis.isProcessing());
        assertFalse(analysis.isPending());
        assertFalse(analysis.isCompleted());
        assertFalse(analysis.isFailed());
    }

    @Test
    @DisplayName("Should identify completed analysis correctly")
    void shouldIdentifyCompletedAnalysis() {
        // Given
        ImageAnalysis analysis = ImageAnalysis.builder()
                .status(AnalysisStatus.COMPLETED)
                .build();

        // When & Then
        assertTrue(analysis.isCompleted());
        assertFalse(analysis.isPending());
        assertFalse(analysis.isProcessing());
        assertFalse(analysis.isFailed());
    }

    @Test
    @DisplayName("Should identify failed analysis correctly")
    void shouldIdentifyFailedAnalysis() {
        // Given
        ImageAnalysis analysis = ImageAnalysis.builder()
                .status(AnalysisStatus.FAILED)
                .errorMessage("Processing failed")
                .build();

        // When & Then
        assertTrue(analysis.isFailed());
        assertFalse(analysis.isPending());
        assertFalse(analysis.isProcessing());
        assertFalse(analysis.isCompleted());
        assertEquals("Processing failed", analysis.getErrorMessage());
    }

    @Test
    @DisplayName("Should return VERY_LOW confidence level when score is null")
    void shouldReturnVeryLowConfidenceWhenNull() {
        // Given
        ImageAnalysis analysis = ImageAnalysis.builder()
                .confidenceScore(null)
                .build();

        // When
        ConfidenceLevel level = analysis.getConfidenceLevel();

        // Then
        assertEquals(ConfidenceLevel.VERY_LOW, level);
    }

    @Test
    @DisplayName("Should return HIGH confidence level for score 0.85")
    void shouldReturnHighConfidenceFor85Score() {
        // Given
        ImageAnalysis analysis = ImageAnalysis.builder()
                .confidenceScore(0.85)
                .build();

        // When
        ConfidenceLevel level = analysis.getConfidenceLevel();

        // Then
        assertEquals(ConfidenceLevel.HIGH, level);
    }

    @Test
    @DisplayName("Should return MEDIUM confidence level for score 0.65")
    void shouldReturnMediumConfidenceFor65Score() {
        // Given
        ImageAnalysis analysis = ImageAnalysis.builder()
                .confidenceScore(0.65)
                .build();

        // When
        ConfidenceLevel level = analysis.getConfidenceLevel();

        // Then
        assertEquals(ConfidenceLevel.MEDIUM, level);
    }

    @Test
    @DisplayName("Should return VERY_HIGH confidence level for score 0.95")
    void shouldReturnVeryHighConfidenceFor95Score() {
        // Given
        ImageAnalysis analysis = ImageAnalysis.builder()
                .confidenceScore(0.95)
                .build();

        // When
        ConfidenceLevel level = analysis.getConfidenceLevel();

        // Then
        assertEquals(ConfidenceLevel.VERY_HIGH, level);
    }

    @Test
    @DisplayName("Should check high confidence with threshold correctly")
    void shouldCheckHighConfidenceWithThreshold() {
        // Given
        ImageAnalysis analysis = ImageAnalysis.builder()
                .confidenceScore(0.85)
                .build();

        // When & Then
        assertTrue(analysis.hasHighConfidence(0.80));
        assertTrue(analysis.hasHighConfidence(0.85));
        assertFalse(analysis.hasHighConfidence(0.90));
    }

    @Test
    @DisplayName("Should return false for high confidence when score is null")
    void shouldReturnFalseForHighConfidenceWhenNull() {
        // Given
        ImageAnalysis analysis = ImageAnalysis.builder()
                .confidenceScore(null)
                .build();

        // When & Then
        assertFalse(analysis.hasHighConfidence(0.50));
    }

    @Test
    @DisplayName("Should calculate processing duration correctly")
    void shouldCalculateProcessingDuration() {
        // Given
        LocalDateTime createdAt = LocalDateTime.now().minusSeconds(5);
        LocalDateTime completedAt = LocalDateTime.now();

        ImageAnalysis analysis = ImageAnalysis.builder()
                .createdAt(createdAt)
                .completedAt(completedAt)
                .build();

        // When
        Long duration = analysis.getProcessingDuration();

        // Then
        assertNotNull(duration);
        assertTrue(duration >= 4900 && duration <= 5100); // Approximately 5 seconds
    }

    @Test
    @DisplayName("Should return null for processing duration when dates are null")
    void shouldReturnNullForProcessingDurationWhenDatesNull() {
        // Given
        ImageAnalysis analysis = ImageAnalysis.builder().build();

        // When
        Long duration = analysis.getProcessingDuration();

        // Then
        assertNull(duration);
    }

    @Test
    @DisplayName("Should mark analysis as processing from pending")
    void shouldMarkAsProcessingFromPending() {
        // Given
        ImageAnalysis analysis = ImageAnalysis.builder()
                .status(AnalysisStatus.PENDING)
                .build();

        // When
        analysis.markAsProcessing();

        // Then
        assertEquals(AnalysisStatus.PROCESSING, analysis.getStatus());
        assertNotNull(analysis.getUpdatedAt());
    }

    @Test
    @DisplayName("Should throw exception when marking as processing from completed")
    void shouldThrowExceptionWhenMarkingAsProcessingFromCompleted() {
        // Given
        ImageAnalysis analysis = ImageAnalysis.builder()
                .status(AnalysisStatus.COMPLETED)
                .build();

        // When & Then
        assertThrows(IllegalStateException.class, analysis::markAsProcessing);
    }

    @Test
    @DisplayName("Should mark analysis as completed from processing")
    void shouldMarkAsCompletedFromProcessing() {
        // Given
        LocalDateTime createdAt = LocalDateTime.now().minusSeconds(2);
        ImageAnalysis analysis = ImageAnalysis.builder()
                .status(AnalysisStatus.PROCESSING)
                .createdAt(createdAt)
                .build();

        // When
        analysis.markAsCompleted(0.92);

        // Then
        assertEquals(AnalysisStatus.COMPLETED, analysis.getStatus());
        assertEquals(0.92, analysis.getConfidenceScore());
        assertNotNull(analysis.getCompletedAt());
        assertNotNull(analysis.getUpdatedAt());
        assertNotNull(analysis.getProcessingTimeMs());
    }

    @Test
    @DisplayName("Should throw exception when marking as completed from pending")
    void shouldThrowExceptionWhenMarkingAsCompletedFromPending() {
        // Given
        ImageAnalysis analysis = ImageAnalysis.builder()
                .status(AnalysisStatus.PENDING)
                .build();

        // When & Then
        assertThrows(IllegalStateException.class, () -> analysis.markAsCompleted(0.85));
    }

    @Test
    @DisplayName("Should mark analysis as failed from processing")
    void shouldMarkAsFailedFromProcessing() {
        // Given
        ImageAnalysis analysis = ImageAnalysis.builder()
                .status(AnalysisStatus.PROCESSING)
                .build();

        // When
        analysis.markAsFailed("Network error");

        // Then
        assertEquals(AnalysisStatus.FAILED, analysis.getStatus());
        assertEquals("Network error", analysis.getErrorMessage());
        assertNotNull(analysis.getCompletedAt());
        assertNotNull(analysis.getUpdatedAt());
    }

    @Test
    @DisplayName("Should mark analysis as failed from pending")
    void shouldMarkAsFailedFromPending() {
        // Given
        ImageAnalysis analysis = ImageAnalysis.builder()
                .status(AnalysisStatus.PENDING)
                .build();

        // When
        analysis.markAsFailed("Invalid input");

        // Then
        assertEquals(AnalysisStatus.FAILED, analysis.getStatus());
        assertEquals("Invalid input", analysis.getErrorMessage());
    }

    @Test
    @DisplayName("Should throw exception when marking as failed from completed")
    void shouldThrowExceptionWhenMarkingAsFailedFromCompleted() {
        // Given
        ImageAnalysis analysis = ImageAnalysis.builder()
                .status(AnalysisStatus.COMPLETED)
                .build();

        // When & Then
        assertThrows(IllegalStateException.class, () -> analysis.markAsFailed("Error"));
    }

    @Test
    @DisplayName("Should get resolution as string")
    void shouldGetResolutionAsString() {
        // Given
        ImageAnalysis analysis = ImageAnalysis.builder()
                .width(1920)
                .height(1080)
                .build();

        // When
        String resolution = analysis.getResolution();

        // Then
        assertEquals("1920x1080", resolution);
    }

    @Test
    @DisplayName("Should return Unknown resolution when dimensions are null")
    void shouldReturnUnknownResolutionWhenNull() {
        // Given
        ImageAnalysis analysis = ImageAnalysis.builder().build();

        // When
        String resolution = analysis.getResolution();

        // Then
        assertEquals("Unknown", resolution);
    }

    @Test
    @DisplayName("Should calculate aspect ratio correctly")
    void shouldCalculateAspectRatio() {
        // Given
        ImageAnalysis analysis = ImageAnalysis.builder()
                .width(1920)
                .height(1080)
                .build();

        // When
        double ratio = analysis.getAspectRatio();

        // Then
        assertEquals(1.7777777777777777, ratio, 0.001);
    }

    @Test
    @DisplayName("Should return 0 for aspect ratio when height is 0")
    void shouldReturn0ForAspectRatioWhenHeightIs0() {
        // Given
        ImageAnalysis analysis = ImageAnalysis.builder()
                .width(1920)
                .height(0)
                .build();

        // When
        double ratio = analysis.getAspectRatio();

        // Then
        assertEquals(0.0, ratio);
    }

    @Test
    @DisplayName("Should return 0 for aspect ratio when dimensions are null")
    void shouldReturn0ForAspectRatioWhenNull() {
        // Given
        ImageAnalysis analysis = ImageAnalysis.builder().build();

        // When
        double ratio = analysis.getAspectRatio();

        // Then
        assertEquals(0.0, ratio);
    }

    @Test
    @DisplayName("Should identify landscape orientation correctly")
    void shouldIdentifyLandscapeOrientation() {
        // Given
        ImageAnalysis analysis = ImageAnalysis.builder()
                .width(1920)
                .height(1080)
                .build();

        // When & Then
        assertTrue(analysis.isLandscape());
        assertFalse(analysis.isPortrait());
    }

    @Test
    @DisplayName("Should identify portrait orientation correctly")
    void shouldIdentifyPortraitOrientation() {
        // Given
        ImageAnalysis analysis = ImageAnalysis.builder()
                .width(1080)
                .height(1920)
                .build();

        // When & Then
        assertTrue(analysis.isPortrait());
        assertFalse(analysis.isLandscape());
    }

    @Test
    @DisplayName("Should return false for landscape and portrait when dimensions are null")
    void shouldReturnFalseForLandscapeAndPortraitWhenNull() {
        // Given
        ImageAnalysis analysis = ImageAnalysis.builder().build();

        // When & Then
        assertFalse(analysis.isLandscape());
        assertFalse(analysis.isPortrait());
    }

    @Test
    @DisplayName("Should identify square as neither landscape nor portrait")
    void shouldIdentifySquareAsNeitherLandscapeNorPortrait() {
        // Given
        ImageAnalysis analysis = ImageAnalysis.builder()
                .width(1080)
                .height(1080)
                .build();

        // When & Then
        assertFalse(analysis.isLandscape());
        assertFalse(analysis.isPortrait());
    }

    @Test
    @DisplayName("Should handle metadata correctly")
    void shouldHandleMetadataCorrectly() {
        // Given
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("key1", "value1");
        metadata.put("key2", 123);

        ImageAnalysis analysis = ImageAnalysis.builder()
                .metadata(metadata)
                .build();

        // When & Then
        assertEquals(2, analysis.getMetadata().size());
        assertEquals("value1", analysis.getMetadata().get("key1"));
        assertEquals(123, analysis.getMetadata().get("key2"));
    }

    @Test
    @DisplayName("Should create ImageAnalysis with no-args constructor")
    void shouldCreateWithNoArgsConstructor() {
        // When
        ImageAnalysis analysis = new ImageAnalysis();

        // Then
        assertNotNull(analysis);
    }

    @Test
    @DisplayName("Should create ImageAnalysis with all-args constructor")
    void shouldCreateWithAllArgsConstructor() {
        // Given
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        Map<String, Object> metadata = new HashMap<>();

        // When
        ImageAnalysis analysis = new ImageAnalysis(
                id, "tenant1", "user1", "url", "path", ImageFormat.JPEG,
                100L, 800, 600, AnalysisStatus.PENDING, "TYPE", 0.85,
                "error", metadata, now, now, now, "creator", "updater", 100L
        );

        // Then
        assertNotNull(analysis);
        assertEquals(id, analysis.getId());
        assertEquals("tenant1", analysis.getTenantId());
    }

    @Test
    @DisplayName("Should use Lombok setters correctly")
    void shouldUseSettersCorrectly() {
        // Given
        ImageAnalysis analysis = new ImageAnalysis();

        // When
        analysis.setId(UUID.randomUUID());
        analysis.setTenantId("tenant-123");
        analysis.setUserId("user-456");
        analysis.setStatus(AnalysisStatus.COMPLETED);
        analysis.setConfidenceScore(0.95);

        // Then
        assertNotNull(analysis.getId());
        assertEquals("tenant-123", analysis.getTenantId());
        assertEquals("user-456", analysis.getUserId());
        assertEquals(AnalysisStatus.COMPLETED, analysis.getStatus());
        assertEquals(0.95, analysis.getConfidenceScore());
    }
}
