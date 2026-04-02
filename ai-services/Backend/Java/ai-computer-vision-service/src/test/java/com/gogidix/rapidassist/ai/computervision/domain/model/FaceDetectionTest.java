package com.gogidix.rapidassist.ai.computervision.domain.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for FaceDetection domain model
 */
class FaceDetectionTest {

    @Test
    void testFaceDetectionCreation() {
        FaceDetection face = FaceDetection.builder()
                .id(UUID.randomUUID())
                .tenantId("tenant-123")
                .emotion(FaceEmotion.HAPPY)
                .emotionConfidence(0.88)
                .confidenceScore(0.95)
                .age(28)
                .gender("Male")
                .boundingBox(new ObjectDetection.BoundingBox(150.0, 100.0, 200.0, 250.0))
                .landmarks(new ArrayList<>())
                .hasGlasses(false)
                .hasBeard(false)
                .smileConfidence(0.75)
                .build();

        assertNotNull(face);
        assertEquals(FaceEmotion.HAPPY, face.getEmotion());
        assertEquals(28, face.getAge());
        assertEquals("Male", face.getGender());
        assertTrue(face.isConfident(0.9));
        assertTrue(face.isSmiling(0.7));
    }

    @Test
    void testEmotionDetection() {
        FaceDetection happyFace = FaceDetection.builder()
                .emotion(FaceEmotion.HAPPY)
                .emotionConfidence(0.92)
                .build();

        assertEquals(FaceEmotion.HAPPY, happyFace.getPrimaryEmotion());
        assertTrue(happyFace.hasConfidentEmotion(0.9));
    }

    @Test
    void testAgeGroup() {
        FaceDetection child = FaceDetection.builder().age(8).build();
        assertEquals("Child", child.getAgeGroup());

        FaceDetection teenager = FaceDetection.builder().age(16).build();
        assertEquals("Teenager", teenager.getAgeGroup());

        FaceDetection adult = FaceDetection.builder().age(35).build();
        assertEquals("Adult", adult.getAgeGroup());

        FaceDetection senior = FaceDetection.builder().age(70).build();
        assertEquals("Senior", senior.getAgeGroup());
    }

    @Test
    void testSmileDetection() {
        FaceDetection smilingFace = FaceDetection.builder()
                .smileConfidence(0.85)
                .build();

        assertTrue(smilingFace.isSmiling(0.7));

        FaceDetection neutralFace = FaceDetection.builder()
                .smileConfidence(0.30)
                .build();

        assertFalse(neutralFace.isSmiling(0.7));
    }

    @Test
    void testCenterPoint() {
        FaceDetection face = FaceDetection.builder()
                .boundingBox(new ObjectDetection.BoundingBox(150.0, 100.0, 200.0, 250.0))
                .build();

        ObjectDetection.Point center = face.getCenterPoint();
        assertNotNull(center);
        assertEquals(250.0, center.getX());
        assertEquals(225.0, center.getY());
    }

    @Test
    void testFaceInCenter() {
        FaceDetection centeredFace = FaceDetection.builder()
                .boundingBox(new ObjectDetection.BoundingBox(400.0, 300.0, 200.0, 200.0))
                .build();

        assertTrue(centeredFace.isInCenter(1920, 1080, 1000.0));
    }
}
