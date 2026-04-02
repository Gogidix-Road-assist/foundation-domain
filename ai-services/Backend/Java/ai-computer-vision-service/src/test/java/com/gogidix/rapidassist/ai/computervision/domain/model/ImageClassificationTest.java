package com.gogidix.rapidassist.ai.computervision.domain.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ImageClassification domain model
 */
class ImageClassificationTest {

    @Test
    void testImageClassificationCreation() {
        List<ImageClassification.ClassPrediction> predictions = new ArrayList<>();
        predictions.add(ImageClassification.ClassPrediction.builder()
                .className("outdoor")
                .confidence(0.92)
                .rank(1)
                .build());
        predictions.add(ImageClassification.ClassPrediction.builder()
                .className("street")
                .confidence(0.85)
                .rank(2)
                .build());

        ImageClassification classification = ImageClassification.builder()
                .id(UUID.randomUUID())
                .tenantId("tenant-123")
                .primaryClass("outdoor")
                .primaryConfidence(0.92)
                .predictions(predictions)
                .modelName("resnet50")
                .modelVersion("1.0")
                .build();

        assertNotNull(classification);
        assertEquals("outdoor", classification.getPrimaryClass());
        assertEquals(0.92, classification.getPrimaryConfidence());
        assertEquals(2, classification.getPredictions().size());
        assertTrue(classification.isPrimaryConfident(0.9));
    }

    @Test
    void testGetTopPredictions() {
        List<ImageClassification.ClassPrediction> predictions = new ArrayList<>();
        predictions.add(ImageClassification.ClassPrediction.builder()
                .className("outdoor")
                .confidence(0.92)
                .rank(1)
                .build());
        predictions.add(ImageClassification.ClassPrediction.builder()
                .className("street")
                .confidence(0.85)
                .rank(2)
                .build());
        predictions.add(ImageClassification.ClassPrediction.builder()
                .className("urban")
                .confidence(0.78)
                .rank(3)
                .build());

        ImageClassification classification = ImageClassification.builder()
                .predictions(predictions)
                .build();

        List<ImageClassification.ClassPrediction> top2 = classification.getTopPredictions(2);
        assertEquals(2, top2.size());
        assertEquals("outdoor", top2.get(0).getClassName());
        assertEquals("street", top2.get(1).getClassName());
    }

    @Test
    void testGetPredictionsWithMinConfidence() {
        List<ImageClassification.ClassPrediction> predictions = new ArrayList<>();
        predictions.add(ImageClassification.ClassPrediction.builder()
                .className("outdoor")
                .confidence(0.92)
                .rank(1)
                .build());
        predictions.add(ImageClassification.ClassPrediction.builder()
                .className("street")
                .confidence(0.65)
                .rank(2)
                .build());
        predictions.add(ImageClassification.ClassPrediction.builder()
                .className("urban")
                .confidence(0.78)
                .rank(3)
                .build());

        ImageClassification classification = ImageClassification.builder()
                .predictions(predictions)
                .build();

        List<ImageClassification.ClassPrediction> highConfidence = classification.getPredictionsWithMinConfidence(0.75);
        assertEquals(2, highConfidence.size());
    }

    @Test
    void testGetPredictionByClass() {
        List<ImageClassification.ClassPrediction> predictions = new ArrayList<>();
        predictions.add(ImageClassification.ClassPrediction.builder()
                .className("outdoor")
                .confidence(0.92)
                .rank(1)
                .build());

        ImageClassification classification = ImageClassification.builder()
                .predictions(predictions)
                .build();

        ImageClassification.ClassPrediction outdoor = classification.getPredictionByClass("outdoor");
        assertNotNull(outdoor);
        assertEquals("outdoor", outdoor.getClassName());

        ImageClassification.ClassPrediction indoor = classification.getPredictionByClass("indoor");
        assertNull(indoor);
    }

    @Test
    void testContainsContentType() {
        List<ImageClassification.ClassPrediction> predictions = new ArrayList<>();
        predictions.add(ImageClassification.ClassPrediction.builder()
                .className("car")
                .confidence(0.92)
                .rank(1)
                .build());

        ImageClassification classification = ImageClassification.builder()
                .primaryClass("vehicle")
                .predictions(predictions)
                .build();

        assertTrue(classification.containsContentType("car"));
        assertTrue(classification.containsContentType("vehicle"));
        assertFalse(classification.containsContentType("person"));
    }

    @Test
    void testClassPredictionConfidence() {
        ImageClassification.ClassPrediction prediction = ImageClassification.ClassPrediction.builder()
                .className("outdoor")
                .confidence(0.92)
                .rank(1)
                .build();

        assertEquals(ConfidenceLevel.VERY_HIGH, prediction.getConfidenceLevel());
        assertTrue(prediction.isConfident(0.9));
    }
}
