package com.gogidix.rapidassist.ai.computervision.domain.aggregate;

import com.gogidix.rapidassist.ai.computervision.domain.model.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ImageAnalysisAggregate
 */
class ImageAnalysisAggregateTest {

    @Test
    void testAggregateInitialization() {
        ImageAnalysisAggregate aggregate = ImageAnalysisAggregate.initialize(
                "tenant-123",
                "user-123",
                "https://example.com/image.jpg",
                "/storage/image.jpg",
                ImageFormat.JPEG,
                1024000L,
                1920,
                1080,
                "OBJECT_DETECTION"
        );

        assertNotNull(aggregate.getId());
        assertEquals("tenant-123", aggregate.getTenantId());
        assertEquals("user-123", aggregate.getUserId());
        assertEquals("https://example.com/image.jpg", aggregate.getImageUrl());
        assertEquals(AnalysisStatus.PENDING, aggregate.getStatus());
        assertEquals("OBJECT_DETECTION", aggregate.getAnalysisType());
        assertTrue(aggregate.isPending());
        assertFalse(aggregate.isProcessing());
    }

    @Test
    void testAggregateLifecycle() {
        ImageAnalysisAggregate aggregate = ImageAnalysisAggregate.initialize(
                "tenant-123",
                "user-123",
                "https://example.com/image.jpg",
                "/storage/image.jpg",
                ImageFormat.JPEG,
                1024000L,
                1920,
                1080,
                "OBJECT_DETECTION"
        );

        aggregate.startProcessing();
        assertTrue(aggregate.isProcessing());
        assertEquals(AnalysisStatus.PROCESSING, aggregate.getStatus());

        aggregate.completeAnalysis(0.88);
        assertTrue(aggregate.isCompleted());
        assertEquals(0.88, aggregate.getOverallConfidence());
        assertNotNull(aggregate.getCompletedAt());
        assertNotNull(aggregate.getProcessingTimeMs());
    }

    @Test
    void testAddDetectedObjects() {
        ImageAnalysisAggregate aggregate = ImageAnalysisAggregate.initialize(
                "tenant-123",
                "user-123",
                "https://example.com/image.jpg",
                "/storage/image.jpg",
                ImageFormat.JPEG,
                1024000L,
                1920,
                1080,
                "OBJECT_DETECTION"
        );

        ObjectDetection car = ObjectDetection.builder()
                .type(DetectionType.VEHICLE)
                .label("Car")
                .confidenceScore(0.92)
                .boundingBox(new ObjectDetection.BoundingBox(100.0, 200.0, 300.0, 200.0))
                .build();

        aggregate.addDetectedObject(car);

        assertEquals(1, aggregate.getDetectedObjects().size());
        assertEquals(1, aggregate.getTotalDetectionsCount());
        assertTrue(aggregate.getDetectedObjects().get(0).isConfident(0.9));
    }

    @Test
    void testAddDetectedFaces() {
        ImageAnalysisAggregate aggregate = ImageAnalysisAggregate.initialize(
                "tenant-123",
                "user-123",
                "https://example.com/image.jpg",
                "/storage/image.jpg",
                ImageFormat.JPEG,
                1024000L,
                1920,
                1080,
                "FACE_DETECTION"
        );

        FaceDetection face = FaceDetection.builder()
                .emotion(FaceEmotion.HAPPY)
                .emotionConfidence(0.88)
                .confidenceScore(0.95)
                .age(28)
                .gender("Male")
                .build();

        aggregate.addDetectedFace(face);

        assertEquals(1, aggregate.getDetectedFaces().size());
        assertEquals(1, aggregate.getTotalDetectionsCount());
        assertTrue(aggregate.getDetectedFaces().get(0).isConfident(0.9));
    }

    @Test
    void testAddRecognizedText() {
        ImageAnalysisAggregate aggregate = ImageAnalysisAggregate.initialize(
                "tenant-123",
                "user-123",
                "https://example.com/image.jpg",
                "/storage/image.jpg",
                ImageFormat.JPEG,
                1024000L,
                1920,
                1080,
                "TEXT_RECOGNITION"
        );

        TextRecognition text = TextRecognition.builder()
                .fullText("Sample text")
                .language("en")
                .confidenceScore(0.89)
                .totalCharacters(11)
                .totalWords(2)
                .totalLines(1)
                .build();

        aggregate.addRecognizedText(text);

        assertEquals(1, aggregate.getRecognizedTexts().size());
        assertTrue(aggregate.hasExtractedText());
        assertEquals("Sample text", aggregate.getAllExtractedText().trim());
    }

    @Test
    void testAggregateFailure() {
        ImageAnalysisAggregate aggregate = ImageAnalysisAggregate.initialize(
                "tenant-123",
                "user-123",
                "https://example.com/image.jpg",
                "/storage/image.jpg",
                ImageFormat.JPEG,
                1024000L,
                1920,
                1080,
                "OBJECT_DETECTION"
        );

        aggregate.startProcessing();
        aggregate.failAnalysis("Network error");

        assertTrue(aggregate.isFailed());
        assertEquals("Network error", aggregate.getErrorMessage());
    }

    @Test
    void testImageCharacteristics() {
        ImageAnalysisAggregate aggregate = ImageAnalysisAggregate.initialize(
                "tenant-123",
                "user-123",
                "https://example.com/image.jpg",
                "/storage/image.jpg",
                ImageFormat.JPEG,
                1024000L,
                1920,
                1080,
                "OBJECT_DETECTION"
        );

        assertEquals("1920x1080", aggregate.getResolution());
        assertTrue(aggregate.isLandscape());
        assertFalse(aggregate.isPortrait());
        assertEquals(1920.0 / 1080.0, aggregate.getAspectRatio(), 0.01);
    }
}
