package com.gogidix.rapidassist.ai.computervision.application.service;

import com.gogidix.rapidassist.ai.computervision.application.command.*;
import com.gogidix.rapidassist.ai.computervision.application.dto.*;
import com.gogidix.rapidassist.ai.computervision.application.mapper.ImageAnalysisMapper;
import com.gogidix.rapidassist.ai.computervision.application.query.GetAvailableModelsQuery;
import com.gogidix.rapidassist.ai.computervision.application.query.GetImageAnalysisQuery;
import com.gogidix.rapidassist.ai.computervision.application.query.ListImageAnalysesQuery;
import com.gogidix.rapidassist.ai.computervision.domain.aggregate.ImageAnalysisAggregate;
import com.gogidix.rapidassist.ai.computervision.domain.model.*;
import com.gogidix.rapidassist.ai.computervision.domain.repository.ImageAnalysisRepositoryPort;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Application Service for Computer Vision operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ComputerVisionApplicationService {

    private final ImageAnalysisRepositoryPort imageAnalysisRepository;
    private final ImageAnalysisMapper mapper;

    @Transactional
    public ImageAnalysisDto analyzeImage(AnalyzeImageCommand command) {
        log.info("Analyzing image: {}", command.getImageUrl());

        ImageAnalysisAggregate aggregate = ImageAnalysisAggregate.initialize(
                command.getTenantId(),
                command.getUserId(),
                command.getImageUrl(),
                command.getImageStoragePath(),
                command.getFormat(),
                command.getFileSize(),
                command.getWidth(),
                command.getHeight(),
                command.getAnalysisType()
        );

        aggregate.startProcessing();

        // Simulate processing with mock results
        simulateProcessing(aggregate, command.getAnalysisType());

        aggregate.completeAnalysis(0.85);

        ImageAnalysisAggregate saved = imageAnalysisRepository.save(aggregate);

        log.info("Image analysis completed: {}", saved.getId());
        return mapper.toDto(saved);
    }

    @Transactional
    public ImageAnalysisDto detectObjects(DetectObjectsCommand command) {
        log.info("Detecting objects in image: {}", command.getImageUrl());

        ImageAnalysisAggregate aggregate = ImageAnalysisAggregate.initialize(
                command.getTenantId(),
                command.getUserId(),
                command.getImageUrl(),
                command.getImageStoragePath(),
                ImageFormat.JPEG,
                0L,
                command.getWidth(),
                command.getHeight(),
                "OBJECT_DETECTION"
        );

        aggregate.startProcessing();

        // Add mock detected objects
        ObjectDetection car = ObjectDetection.builder()
                .type(DetectionType.VEHICLE)
                .label("Car")
                .confidenceScore(0.92)
                .boundingBox(new ObjectDetection.BoundingBox(100.0, 200.0, 300.0, 200.0))
                .color("Red")
                .description("A red car detected on the road")
                .objectCount(1)
                .build();

        aggregate.addDetectedObject(car);

        aggregate.completeAnalysis(0.92);

        ImageAnalysisAggregate saved = imageAnalysisRepository.save(aggregate);
        log.info("Object detection completed: {}", saved.getId());

        return enrichWithObjects(mapper.toDto(saved), aggregate.getDetectedObjects());
    }

    @Transactional
    public ImageAnalysisDto detectFaces(DetectFacesCommand command) {
        log.info("Detecting faces in image: {}", command.getImageUrl());

        ImageAnalysisAggregate aggregate = ImageAnalysisAggregate.initialize(
                command.getTenantId(),
                command.getUserId(),
                command.getImageUrl(),
                command.getImageStoragePath(),
                ImageFormat.JPEG,
                0L,
                command.getWidth(),
                command.getHeight(),
                "FACE_DETECTION"
        );

        aggregate.startProcessing();

        // Add mock detected face
        FaceDetection face = FaceDetection.builder()
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

        aggregate.addDetectedFace(face);

        aggregate.completeAnalysis(0.95);

        ImageAnalysisAggregate saved = imageAnalysisRepository.save(aggregate);
        log.info("Face detection completed: {}", saved.getId());

        return enrichWithFaces(mapper.toDto(saved), aggregate.getDetectedFaces());
    }

    @Transactional
    public ImageAnalysisDto extractText(ExtractTextCommand command) {
        log.info("Extracting text from image: {}", command.getImageUrl());

        ImageAnalysisAggregate aggregate = ImageAnalysisAggregate.initialize(
                command.getTenantId(),
                command.getUserId(),
                command.getImageUrl(),
                command.getImageStoragePath(),
                ImageFormat.JPEG,
                0L,
                800,
                600,
                "TEXT_RECOGNITION"
        );

        aggregate.startProcessing();

        // Add mock recognized text
        TextRecognition text = TextRecognition.builder()
                .fullText("Sample extracted text from image")
                .textLines(new ArrayList<>())
                .textWords(new ArrayList<>())
                .language("en")
                .confidenceScore(0.89)
                .totalCharacters(28)
                .totalWords(5)
                .totalLines(1)
                .build();

        aggregate.addRecognizedText(text);

        aggregate.completeAnalysis(0.89);

        ImageAnalysisAggregate saved = imageAnalysisRepository.save(aggregate);
        log.info("Text extraction completed: {}", saved.getId());

        return enrichWithText(mapper.toDto(saved), aggregate.getRecognizedTexts());
    }

    @Transactional
    public ImageAnalysisDto classifyImage(ClassifyImageCommand command) {
        log.info("Classifying image: {}", command.getImageUrl());

        ImageAnalysisAggregate aggregate = ImageAnalysisAggregate.initialize(
                command.getTenantId(),
                command.getUserId(),
                command.getImageUrl(),
                command.getImageStoragePath(),
                ImageFormat.JPEG,
                0L,
                command.getWidth(),
                command.getHeight(),
                "IMAGE_CLASSIFICATION"
        );

        aggregate.startProcessing();

        // Add mock classification
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
                .primaryClass("outdoor")
                .primaryConfidence(0.92)
                .predictions(predictions)
                .modelName("resnet50")
                .modelVersion("1.0")
                .build();

        aggregate.addClassification(classification);

        aggregate.completeAnalysis(0.92);

        ImageAnalysisAggregate saved = imageAnalysisRepository.save(aggregate);
        log.info("Image classification completed: {}", saved.getId());

        return enrichWithClassification(mapper.toDto(saved), aggregate.getClassifications());
    }

    public ImageAnalysisDto getAnalysis(GetImageAnalysisQuery query) {
        log.info("Getting analysis: {}", query.getId());

        Optional<ImageAnalysisAggregate> aggregate = imageAnalysisRepository.findById(query.getId());

        if (aggregate.isEmpty()) {
            throw new RuntimeException("Analysis not found: " + query.getId());
        }

        return mapper.toDto(aggregate.get());
    }

    public Page<ImageAnalysisDto> listAnalyses(ListImageAnalysesQuery query) {
        log.info("Listing analyses for tenant: {}", query.getTenantId());

        List<ImageAnalysisAggregate> aggregates;

        if (query.getTenantId() != null && query.getStatus() != null) {
            aggregates = imageAnalysisRepository.findByTenantIdAndStatus(query.getTenantId(), query.getStatus());
        } else if (query.getTenantId() != null) {
            aggregates = imageAnalysisRepository.findByTenantId(query.getTenantId());
        } else if (query.getUserId() != null) {
            aggregates = imageAnalysisRepository.findByUserId(query.getUserId());
        } else {
            aggregates = imageAnalysisRepository.findByStatus(query.getStatus() != null ? query.getStatus() : "PENDING");
        }

        List<ImageAnalysisDto> dtos = aggregates.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());

        int page = query.getPage() != null ? query.getPage() : 0;
        int size = query.getSize() != null ? query.getSize() : 20;

        int start = (int) Math.min(page * size, dtos.size());
        int end = (int) Math.min((page + 1) * size, dtos.size());

        return new PageImpl<>(
                dtos.subList(start, end),
                PageRequest.of(page, size),
                dtos.size()
        );
    }

    public Map<String, Object> getAvailableModels(GetAvailableModelsQuery query) {
        Map<String, Object> models = new HashMap<>();

        models.put("object_detection", Arrays.asList(
                Map.of("name", "yolov8", "version", "1.0", "description", "Real-time object detection"),
                Map.of("name", "faster-rcnn", "version", "1.0", "description", "High accuracy object detection")
        ));

        models.put("face_detection", Arrays.asList(
                Map.of("name", "mediapipe", "version", "1.0", "description", "Real-time face detection"),
                Map.of("name", "mtcnn", "version", "1.0", "description", "Multi-task cascaded CNN")
        ));

        models.put("text_recognition", Arrays.asList(
                Map.of("name", "tesseract", "version", "5.0", "description", "OCR text extraction")
        ));

        models.put("image_classification", Arrays.asList(
                Map.of("name", "resnet50", "version", "1.0", "description", "Deep residual network"),
                Map.of("name", "efficientnet", "version", "1.0", "description", "Efficient neural network")
        ));

        return models;
    }

    private void simulateProcessing(ImageAnalysisAggregate aggregate, String analysisType) {
        // Mock processing simulation
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private ImageAnalysisDto enrichWithObjects(ImageAnalysisDto dto, List<ObjectDetection> objects) {
        dto.setMetadata(Map.of("detectedObjectsCount", objects.size()));
        return dto;
    }

    private ImageAnalysisDto enrichWithFaces(ImageAnalysisDto dto, List<FaceDetection> faces) {
        dto.setMetadata(Map.of("detectedFacesCount", faces.size()));
        return dto;
    }

    private ImageAnalysisDto enrichWithText(ImageAnalysisDto dto, List<TextRecognition> texts) {
        dto.setMetadata(Map.of("textRegionsCount", texts.size()));
        return dto;
    }

    private ImageAnalysisDto enrichWithClassification(ImageAnalysisDto dto, List<ImageClassification> classifications) {
        dto.setMetadata(Map.of("classificationCount", classifications.size()));
        return dto;
    }
}
