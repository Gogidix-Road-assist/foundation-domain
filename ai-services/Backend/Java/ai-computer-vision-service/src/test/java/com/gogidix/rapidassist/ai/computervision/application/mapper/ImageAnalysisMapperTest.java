package com.gogidix.rapidassist.ai.computervision.application.mapper;

import com.gogidix.rapidassist.ai.computervision.application.dto.*;
import com.gogidix.rapidassist.ai.computervision.domain.aggregate.ImageAnalysisAggregate;
import com.gogidix.rapidassist.ai.computervision.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import java.util.Arrays;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static com.gogidix.rapidassist.ai.computervision.domain.model.AnalysisStatus.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for ImageAnalysisMapper
 * Tests all mapping operations between domain models and DTOs
 */
@DisplayName("ImageAnalysis Mapper Tests")
class ImageAnalysisMapperTest {

    private ImageAnalysisMapper mapper;

    private ImageAnalysisAggregate testAggregate;
    private ImageAnalysis testModel;
    private ObjectDetection testObjectDetection;
    private FaceDetection testFaceDetection;
    private TextRecognition testTextRecognition;
    private ImageClassification testImageClassification;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(ImageAnalysisMapper.class);

        UUID testId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

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
                .createdAt(now)
                .updatedAt(now)
                .completedAt(now)
                .createdBy("user-456")
                .updatedBy("user-456")
                .processingTimeMs(5000L)
                .detectedObjects(new ArrayList<>())
                .detectedFaces(new ArrayList<>())
                .recognizedTexts(new ArrayList<>())
                .classifications(new ArrayList<>())
                .build();

        testModel = ImageAnalysis.builder()
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
                .confidenceScore(0.85)
                .createdAt(now)
                .updatedAt(now)
                .completedAt(now)
                .createdBy("user-456")
                .updatedBy("user-456")
                .processingTimeMs(5000L)
                .build();

        testObjectDetection = ObjectDetection.builder()
                .type(DetectionType.VEHICLE)
                .label("Car")
                .confidenceScore(0.92)
                .boundingBox(new ObjectDetection.BoundingBox(100.0, 200.0, 300.0, 200.0))
                .color("Red")
                .description("A red car detected on the road")
                .objectCount(1)
                .build();

        testFaceDetection = FaceDetection.builder()
                .emotion(FaceEmotion.HAPPY)
                .emotionConfidence(0.88)
                .confidenceScore(0.95)
                .age(28)
                .gender("Male")
                .boundingBox(new ObjectDetection.BoundingBox(150.0, 100.0, 200.0, 250.0))
                .landmarks(new ArrayList<>())
                .hasGlasses(false)
                .hasBeard(false)
                .hasMustache(false)
                .smileConfidence(0.75)
                .build();

        testTextRecognition = TextRecognition.builder()
                .fullText("Sample extracted text from image")
                .textLines(new ArrayList<>())
                .textWords(new ArrayList<>())
                .language("en")
                .confidenceScore(0.89)
                .totalCharacters(28)
                .totalWords(5)
                .totalLines(1)
                .build();

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

        testImageClassification = ImageClassification.builder()
                .primaryClass("outdoor")
                .primaryConfidence(0.92)
                .predictions(predictions)
                .modelName("resnet50")
                .modelVersion("1.0")
                .build();
    }

    @Test
    @DisplayName("Should map aggregate to DTO")
    void shouldMapAggregateToDto() {
        // When
        ImageAnalysisDto dto = mapper.toDto(testAggregate);

        // Then
        assertNotNull(dto);
        assertEquals(testAggregate.getId(), dto.getId());
        assertEquals(testAggregate.getTenantId(), dto.getTenantId());
        assertEquals(testAggregate.getUserId(), dto.getUserId());
        assertEquals(testAggregate.getImageUrl(), dto.getImageUrl());
        assertEquals(testAggregate.getFormat(), dto.getFormat());
        assertEquals(testAggregate.getStatus(), dto.getStatus());
        assertEquals(testAggregate.getOverallConfidence(), dto.getConfidenceScore());
    }

    @Test
    @DisplayName("Should map DTO to aggregate")
    void shouldMapDtoToAggregate() {
        // Given
        ImageAnalysisDto dto = ImageAnalysisDto.builder()
                .id(testAggregate.getId())
                .tenantId(testAggregate.getTenantId())
                .userId(testAggregate.getUserId())
                .imageUrl(testAggregate.getImageUrl())
                .format(testAggregate.getFormat())
                .status(testAggregate.getStatus())
                .confidenceScore(testAggregate.getOverallConfidence())
                .build();

        // When
        ImageAnalysisAggregate aggregate = mapper.toAggregate(dto);

        // Then
        assertNotNull(aggregate);
        assertEquals(dto.getId(), aggregate.getId());
        assertEquals(dto.getTenantId(), aggregate.getTenantId());
        assertEquals(dto.getUserId(), aggregate.getUserId());
    }

    @Test
    @DisplayName("Should map model to DTO")
    void shouldMapModelToDto() {
        // When
        ImageAnalysisDto dto = mapper.toDto(testModel);

        // Then
        assertNotNull(dto);
        assertEquals(testModel.getId(), dto.getId());
        assertEquals(testModel.getTenantId(), dto.getTenantId());
        assertEquals(testModel.getUserId(), dto.getUserId());
        assertEquals(testModel.getImageUrl(), dto.getImageUrl());
        assertEquals(testModel.getFormat(), dto.getFormat());
        assertEquals(testModel.getStatus(), dto.getStatus());
        assertEquals(testModel.getConfidenceScore(), dto.getConfidenceScore());
    }

    @Test
    @DisplayName("Should map ObjectDetection to DTO")
    void shouldMapObjectDetectionToDto() {
        // When
        ObjectDetectionDto dto = mapper.toDto(testObjectDetection);

        // Then
        assertNotNull(dto);
        assertEquals(testObjectDetection.getType(), dto.getType());
        assertEquals(testObjectDetection.getLabel(), dto.getLabel());
        assertEquals(testObjectDetection.getConfidenceScore(), dto.getConfidenceScore());
        assertEquals("VERY_HIGH", dto.getConfidenceLevel());
        assertEquals(testObjectDetection.getColor(), dto.getColor());
        assertEquals(testObjectDetection.getDescription(), dto.getDescription());
        assertEquals(testObjectDetection.getObjectCount(), dto.getObjectCount());
    }

    @Test
    @DisplayName("Should map FaceDetection to DTO with age group")
    void shouldMapFaceDetectionToDtoWithAgeGroup() {
        // When
        FaceDetectionDto dto = mapper.toDto(testFaceDetection);

        // Then
        assertNotNull(dto);
        assertEquals(testFaceDetection.getEmotion(), dto.getEmotion());
        assertEquals(testFaceDetection.getEmotionConfidence(), dto.getEmotionConfidence());
        assertEquals(testFaceDetection.getConfidenceScore(), dto.getConfidenceScore());
        assertEquals("VERY_HIGH", dto.getConfidenceLevel());
        assertEquals("Adult", dto.getAgeGroup());
        assertEquals(testFaceDetection.getGender(), dto.getGender());
        assertEquals(Boolean.FALSE, dto.getSmiling());
        assertEquals(testFaceDetection.getHasGlasses(), dto.getHasGlasses());
        assertEquals(testFaceDetection.getHasBeard(), dto.getHasBeard());
    }

    @Test
    @DisplayName("Should map FaceDetection with child age to correct age group")
    void shouldMapFaceDetectionWithChildAgeToCorrectAgeGroup() {
        // Given
        FaceDetection childFace = FaceDetection.builder()
                .age(10)
                .confidenceScore(0.85)
                .build();

        // When
        FaceDetectionDto dto = mapper.toDto(childFace);

        // Then
        assertEquals("Child", dto.getAgeGroup());
    }

    @Test
    @DisplayName("Should map FaceDetection with teenager age to correct age group")
    void shouldMapFaceDetectionWithTeenagerAgeToCorrectAgeGroup() {
        // Given
        FaceDetection teenFace = FaceDetection.builder()
                .age(15)
                .confidenceScore(0.85)
                .build();

        // When
        FaceDetectionDto dto = mapper.toDto(teenFace);

        // Then
        assertEquals("Teenager", dto.getAgeGroup());
    }

    @Test
    @DisplayName("Should map FaceDetection with senior age to correct age group")
    void shouldMapFaceDetectionWithSeniorAgeToCorrectAgeGroup() {
        // Given
        FaceDetection seniorFace = FaceDetection.builder()
                .age(65)
                .confidenceScore(0.85)
                .build();

        // When
        FaceDetectionDto dto = mapper.toDto(seniorFace);

        // Then
        assertEquals("Senior", dto.getAgeGroup());
    }

    @Test
    @DisplayName("Should map FaceDetection with null age to Unknown age group")
    void shouldMapFaceDetectionWithNullAgeToUnknownAgeGroup() {
        // Given
        FaceDetection nullAgeFace = FaceDetection.builder()
                .age(null)
                .confidenceScore(0.85)
                .build();

        // When
        FaceDetectionDto dto = mapper.toDto(nullAgeFace);

        // Then
        assertEquals("Unknown", dto.getAgeGroup());
    }

    @Test
    @DisplayName("Should detect smiling face correctly")
    void shouldDetectSmilingFaceCorrectly() {
        // Given
        FaceDetection smilingFace = FaceDetection.builder()
                .smileConfidence(0.75)
                .confidenceScore(0.85)
                .build();

        // When
        FaceDetectionDto dto = mapper.toDto(smilingFace);

        // Then
        assertEquals(Boolean.TRUE, dto.getSmiling());
    }

    @Test
    @DisplayName("Should detect non-smiling face correctly")
    void shouldDetectNonSmilingFaceCorrectly() {
        // Given
        FaceDetection nonSmilingFace = FaceDetection.builder()
                .smileConfidence(0.25)
                .confidenceScore(0.85)
                .build();

        // When
        FaceDetectionDto dto = mapper.toDto(nonSmilingFace);

        // Then
        assertEquals(Boolean.FALSE, dto.getSmiling());
    }

    @Test
    @DisplayName("Should map TextRecognition to DTO")
    void shouldMapTextRecognitionToDto() {
        // When
        TextRecognitionDto dto = mapper.toDto(testTextRecognition);

        // Then
        assertNotNull(dto);
        assertEquals(testTextRecognition.getFullText(), dto.getFullText());
        assertEquals(testTextRecognition.getLanguage(), dto.getLanguage());
        assertEquals(testTextRecognition.getConfidenceScore(), dto.getConfidenceScore());
        assertEquals("HIGH", dto.getConfidenceLevel());
        assertEquals(testTextRecognition.getTotalCharacters(), dto.getTotalCharacters());
        assertEquals(testTextRecognition.getTotalWords(), dto.getTotalWords());
        assertEquals(testTextRecognition.getTotalLines(), dto.getTotalLines());
    }

    @Test
    @DisplayName("Should map ImageClassification to DTO")
    void shouldMapImageClassificationToDto() {
        // When
        ImageClassificationDto dto = mapper.toDto(testImageClassification);

        // Then
        assertNotNull(dto);
        assertEquals(testImageClassification.getPrimaryClass(), dto.getPrimaryClass());
        assertEquals(testImageClassification.getPrimaryConfidence(), dto.getPrimaryConfidence());
        assertEquals("VERY_HIGH", dto.getPrimaryConfidenceLevel());
        assertEquals(testImageClassification.getModelName(), dto.getModelName());
        assertEquals(testImageClassification.getModelVersion(), dto.getModelVersion());
        assertNotNull(dto.getPredictions());
        assertEquals(2, dto.getPredictions().size());
    }

    @Test
    @DisplayName("Should map predictions with confidence levels")
    void shouldMapPredictionsWithConfidenceLevels() {
        // When
        ImageClassificationDto dto = mapper.toDto(testImageClassification);

        // Then
        assertEquals(2, dto.getPredictions().size());
        assertEquals("outdoor", dto.getPredictions().get(0).getClassName());
        assertEquals(0.92, dto.getPredictions().get(0).getConfidence());
        assertEquals("VERY_HIGH", dto.getPredictions().get(0).getConfidenceLevel());
        assertEquals(1, dto.getPredictions().get(0).getRank());

        assertEquals("street", dto.getPredictions().get(1).getClassName());
        assertEquals(0.85, dto.getPredictions().get(1).getConfidence());
        assertEquals("HIGH", dto.getPredictions().get(1).getConfidenceLevel());
        assertEquals(2, dto.getPredictions().get(1).getRank());
    }

    @Test
    @DisplayName("Should map null confidence to VERY_LOW level")
    void shouldMapNullConfidenceToVeryLowLevel() {
        // Given
        ObjectDetection nullConfidence = ObjectDetection.builder()
                .type(DetectionType.VEHICLE)
                .label("Car")
                .confidenceScore(null)
                .build();

        // When
        ObjectDetectionDto dto = mapper.toDto(nullConfidence);

        // Then
        assertEquals("VERY_LOW", dto.getConfidenceLevel());
    }

    @Test
    @DisplayName("Should map list of ObjectDetection to DTO list")
    void shouldMapListOfObjectDetectionToDtoList() {
        // Given
        List<ObjectDetection> detections = Arrays.asList(
                testObjectDetection,
                ObjectDetection.builder()
                        .type(DetectionType.PERSON)
                        .label("Person")
                        .confidenceScore(0.78)
                        .build()
        );

        // When
        List<ObjectDetectionDto> dtoList = mapper.toObjectDetectionDtoList(detections);

        // Then
        assertNotNull(dtoList);
        assertEquals(2, dtoList.size());
        assertEquals("Car", dtoList.get(0).getLabel());
        assertEquals("Person", dtoList.get(1).getLabel());
    }

    @Test
    @DisplayName("Should map empty list of ObjectDetection")
    void shouldMapEmptyListOfObjectDetection() {
        // Given
        List<ObjectDetection> detections = new ArrayList<>();

        // When
        List<ObjectDetectionDto> dtoList = mapper.toObjectDetectionDtoList(detections);

        // Then
        assertNotNull(dtoList);
        assertTrue(dtoList.isEmpty());
    }

    @Test
    @DisplayName("Should map list of FaceDetection to DTO list")
    void shouldMapListOfFaceDetectionToDtoList() {
        // Given
        List<FaceDetection> faces = Arrays.asList(
                testFaceDetection,
                FaceDetection.builder()
                        .emotion(FaceEmotion.NEUTRAL)
                        .confidenceScore(0.82)
                        .age(35)
                        .build()
        );

        // When
        List<FaceDetectionDto> dtoList = mapper.toFaceDetectionDtoList(faces);

        // Then
        assertNotNull(dtoList);
        assertEquals(2, dtoList.size());
        assertEquals(FaceEmotion.HAPPY, dtoList.get(0).getEmotion());
        assertEquals(FaceEmotion.NEUTRAL, dtoList.get(1).getEmotion());
    }

    @Test
    @DisplayName("Should map list of TextRecognition to DTO list")
    void shouldMapListOfTextRecognitionToDtoList() {
        // Given
        List<TextRecognition> texts = Arrays.asList(
                testTextRecognition,
                TextRecognition.builder()
                        .fullText("Another text")
                        .confidenceScore(0.75)
                        .build()
        );

        // When
        List<TextRecognitionDto> dtoList = mapper.toTextRecognitionDtoList(texts);

        // Then
        assertNotNull(dtoList);
        assertEquals(2, dtoList.size());
        assertEquals("Sample extracted text from image", dtoList.get(0).getFullText());
        assertEquals("Another text", dtoList.get(1).getFullText());
    }

    @Test
    @DisplayName("Should map list of ImageClassification to DTO list")
    void shouldMapListOfImageClassificationToDtoList() {
        // Given
        List<ImageClassification> classifications = Arrays.asList(
                testImageClassification,
                ImageClassification.builder()
                        .primaryClass("indoor")
                        .primaryConfidence(0.78)
                        .build()
        );

        // When
        List<ImageClassificationDto> dtoList = mapper.toImageClassificationDtoList(classifications);

        // Then
        assertNotNull(dtoList);
        assertEquals(2, dtoList.size());
        assertEquals("outdoor", dtoList.get(0).getPrimaryClass());
        assertEquals("indoor", dtoList.get(1).getPrimaryClass());
    }

    @Test
    @DisplayName("Should handle null predictions in ImageClassification")
    void shouldHandleNullPredictionsInImageClassification() {
        // Given
        ImageClassification noPredictions = ImageClassification.builder()
                .primaryClass("unknown")
                .primaryConfidence(0.5)
                .predictions(null)
                .build();

        // When
        ImageClassificationDto dto = mapper.toDto(noPredictions);

        // Then
        assertNotNull(dto);
        assertNotNull(dto.getPredictions());
        assertTrue(dto.getPredictions().isEmpty());
    }
}
