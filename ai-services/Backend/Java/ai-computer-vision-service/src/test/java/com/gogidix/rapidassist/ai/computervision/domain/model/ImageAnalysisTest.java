package com.gogidix.rapidassist.ai.computervision.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ImageAnalysis domain model
 */
class ImageAnalysisTest {

    @Test
    void testImageAnalysisCreation() {
        ImageAnalysis analysis = ImageAnalysis.builder()
                .id(UUID.randomUUID())
                .tenantId("tenant-123")
                .userId("user-123")
                .imageUrl("https://example.com/image.jpg")
                .format(ImageFormat.JPEG)
                .fileSize(1024000L)
                .width(1920)
                .height(1080)
                .status(AnalysisStatus.PENDING)
                .analysisType("OBJECT_DETECTION")
                .createdAt(LocalDateTime.now())
                .build();

        assertNotNull(analysis);
        assertEquals("tenant-123", analysis.getTenantId());
        assertEquals("user-123", analysis.getUserId());
        assertEquals(ImageFormat.JPEG, analysis.getFormat());
        assertEquals(1920, analysis.getWidth());
        assertEquals(1080, analysis.getHeight());
        assertEquals(AnalysisStatus.PENDING, analysis.getStatus());
    }

    @Test
    void testImageAnalysisStatusTransitions() {
        ImageAnalysis analysis = ImageAnalysis.builder()
                .id(UUID.randomUUID())
                .tenantId("tenant-123")
                .status(AnalysisStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        assertTrue(analysis.isPending());
        assertFalse(analysis.isProcessing());
        assertFalse(analysis.isCompleted());

        analysis.markAsProcessing();
        assertTrue(analysis.isProcessing());
        assertFalse(analysis.isPending());

        analysis.markAsCompleted(0.85);
        assertTrue(analysis.isCompleted());
        assertFalse(analysis.isProcessing());
        assertEquals(0.85, analysis.getConfidenceScore());
    }

    @Test
    void testImageAnalysisFailure() {
        ImageAnalysis analysis = ImageAnalysis.builder()
                .id(UUID.randomUUID())
                .tenantId("tenant-123")
                .status(AnalysisStatus.PROCESSING)
                .createdAt(LocalDateTime.now())
                .build();

        analysis.markAsFailed("Processing error occurred");
        assertTrue(analysis.isFailed());
        assertEquals("Processing error occurred", analysis.getErrorMessage());
        assertNotNull(analysis.getCompletedAt());
    }

    @Test
    void testImageResolution() {
        ImageAnalysis landscape = ImageAnalysis.builder()
                .id(UUID.randomUUID())
                .width(1920)
                .height(1080)
                .build();

        assertEquals("1920x1080", landscape.getResolution());
        assertTrue(landscape.isLandscape());
        assertFalse(landscape.isPortrait());
        assertEquals(1920.0 / 1080.0, landscape.getAspectRatio(), 0.01);

        ImageAnalysis portrait = ImageAnalysis.builder()
                .id(UUID.randomUUID())
                .width(1080)
                .height(1920)
                .build();

        assertEquals("1080x1920", portrait.getResolution());
        assertFalse(portrait.isLandscape());
        assertTrue(portrait.isPortrait());
    }

    @Test
    void testConfidenceLevel() {
        ImageAnalysis highConfidence = ImageAnalysis.builder()
                .id(UUID.randomUUID())
                .confidenceScore(0.92)
                .build();

        assertEquals(ConfidenceLevel.VERY_HIGH, highConfidence.getConfidenceLevel());
        assertTrue(highConfidence.hasHighConfidence(0.9));

        ImageAnalysis lowConfidence = ImageAnalysis.builder()
                .id(UUID.randomUUID())
                .confidenceScore(0.45)
                .build();

        assertEquals(ConfidenceLevel.LOW, lowConfidence.getConfidenceLevel());
        assertFalse(lowConfidence.hasHighConfidence(0.9));
    }
}
