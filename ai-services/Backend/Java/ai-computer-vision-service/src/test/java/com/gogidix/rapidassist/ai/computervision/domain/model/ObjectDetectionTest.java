package com.gogidix.rapidassist.ai.computervision.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ObjectDetection domain model
 */
class ObjectDetectionTest {

    @Test
    void testObjectDetectionCreation() {
        ObjectDetection detection = ObjectDetection.builder()
                .id(UUID.randomUUID())
                .tenantId("tenant-123")
                .type(DetectionType.VEHICLE)
                .label("Car")
                .confidenceScore(0.92)
                .boundingBox(new ObjectDetection.BoundingBox(100.0, 200.0, 300.0, 200.0))
                .color("Red")
                .description("A red car")
                .objectCount(1)
                .build();

        assertNotNull(detection);
        assertEquals(DetectionType.VEHICLE, detection.getType());
        assertEquals("Car", detection.getLabel());
        assertEquals(0.92, detection.getConfidenceScore());
        assertTrue(detection.isConfident(0.9));
    }

    @Test
    void testConfidenceLevel() {
        ObjectDetection highConfidence = ObjectDetection.builder()
                .confidenceScore(0.95)
                .build();

        assertEquals(ConfidenceLevel.VERY_HIGH, highConfidence.getConfidenceLevel());

        ObjectDetection mediumConfidence = ObjectDetection.builder()
                .confidenceScore(0.65)
                .build();

        assertEquals(ConfidenceLevel.MEDIUM, mediumConfidence.getConfidenceLevel());
    }

    @Test
    void testBoundingBox() {
        ObjectDetection.BoundingBox bbox = new ObjectDetection.BoundingBox(100.0, 200.0, 300.0, 200.0);

        assertEquals(60000.0, bbox.getArea());

        ObjectDetection.Point center = bbox.getCenter();
        assertEquals(250.0, center.getX());
        assertEquals(300.0, center.getY());
    }

    @Test
    void testLargeObjectDetection() {
        // Object with area > 50% of image
        // Image: 1920x1080 = 2,073,600
        // 50% = 1,036,800
        // Let's use 1400x800 = 1,120,000 (> 50%)
        ObjectDetection largeObject = ObjectDetection.builder()
                .boundingBox(new ObjectDetection.BoundingBox(0.0, 0.0, 1400.0, 800.0))
                .build();

        assertTrue(largeObject.isLargeObject(1920, 1080));
    }

    @Test
    void testGetCenterPoint() {
        ObjectDetection detection = ObjectDetection.builder()
                .boundingBox(new ObjectDetection.BoundingBox(100.0, 200.0, 300.0, 200.0))
                .build();

        ObjectDetection.Point center = detection.getCenterPoint();
        assertNotNull(center);
        assertEquals(250.0, center.getX());
        assertEquals(300.0, center.getY());
    }
}
