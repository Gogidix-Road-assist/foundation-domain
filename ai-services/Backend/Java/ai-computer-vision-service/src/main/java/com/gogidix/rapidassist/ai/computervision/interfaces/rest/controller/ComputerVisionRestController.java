package com.gogidix.rapidassist.ai.computervision.interfaces.rest.controller;

import com.gogidix.rapidassist.ai.computervision.application.command.*;
import com.gogidix.rapidassist.ai.computervision.application.dto.ImageAnalysisDto;
import com.gogidix.rapidassist.ai.computervision.application.query.GetImageAnalysisQuery;
import com.gogidix.rapidassist.ai.computervision.application.query.GetAvailableModelsQuery;
import com.gogidix.rapidassist.ai.computervision.application.query.ListImageAnalysesQuery;
import com.gogidix.rapidassist.ai.computervision.application.service.ComputerVisionApplicationService;
import com.gogidix.rapidassist.ai.computervision.domain.model.ImageFormat;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST Controller for Computer Vision operations
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/computer-vision")
@RequiredArgsConstructor
@Tag(name = "Computer Vision", description = "AI-powered computer vision APIs")
public class ComputerVisionRestController {

    private final ComputerVisionApplicationService computerVisionService;

    @PostMapping("/analyze")
    @Operation(summary = "Analyze image", description = "Perform comprehensive analysis on an image")
    public ResponseEntity<ImageAnalysisDto> analyzeImage(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Valid @RequestBody AnalyzeImageRequest request) {

        log.info("POST /api/v1/computer-vision/analyze - Tenant: {}", tenantId);

        AnalyzeImageCommand command = AnalyzeImageCommand.builder()
                .tenantId(tenantId)
                .userId(request.getUserId())
                .imageUrl(request.getImageUrl())
                .imageStoragePath(request.getImageStoragePath())
                .format(request.getFormat())
                .fileSize(request.getFileSize())
                .width(request.getWidth())
                .height(request.getHeight())
                .analysisType(request.getAnalysisType())
                .createdBy(request.getUserId())
                .build();

        ImageAnalysisDto result = computerVisionService.analyzeImage(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PostMapping("/detect-objects")
    @Operation(summary = "Detect objects", description = "Detect objects in an image")
    public ResponseEntity<ImageAnalysisDto> detectObjects(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Valid @RequestBody DetectObjectsRequest request) {

        log.info("POST /api/v1/computer-vision/detect-objects - Tenant: {}", tenantId);

        DetectObjectsCommand command = DetectObjectsCommand.builder()
                .tenantId(tenantId)
                .userId(request.getUserId())
                .imageUrl(request.getImageUrl())
                .imageStoragePath(request.getImageStoragePath())
                .width(request.getWidth())
                .height(request.getHeight())
                .minConfidence(request.getMinConfidence())
                .maxObjects(request.getMaxObjects())
                .createdBy(request.getUserId())
                .build();

        ImageAnalysisDto result = computerVisionService.detectObjects(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PostMapping("/detect-faces")
    @Operation(summary = "Detect faces", description = "Detect faces in an image")
    public ResponseEntity<ImageAnalysisDto> detectFaces(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Valid @RequestBody DetectFacesRequest request) {

        log.info("POST /api/v1/computer-vision/detect-faces - Tenant: {}", tenantId);

        DetectFacesCommand command = DetectFacesCommand.builder()
                .tenantId(tenantId)
                .userId(request.getUserId())
                .imageUrl(request.getImageUrl())
                .imageStoragePath(request.getImageStoragePath())
                .width(request.getWidth())
                .height(request.getHeight())
                .detectEmotions(request.getDetectEmotions() != null ? request.getDetectEmotions() : true)
                .detectAge(request.getDetectAge() != null ? request.getDetectAge() : true)
                .detectGender(request.getDetectGender() != null ? request.getDetectGender() : true)
                .detectLandmarks(request.getDetectLandmarks() != null ? request.getDetectLandmarks() : false)
                .minConfidence(request.getMinConfidence())
                .createdBy(request.getUserId())
                .build();

        ImageAnalysisDto result = computerVisionService.detectFaces(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PostMapping("/extract-text")
    @Operation(summary = "Extract text", description = "Extract text from an image using OCR")
    public ResponseEntity<ImageAnalysisDto> extractText(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Valid @RequestBody ExtractTextRequest request) {

        log.info("POST /api/v1/computer-vision/extract-text - Tenant: {}", tenantId);

        ExtractTextCommand command = ExtractTextCommand.builder()
                .tenantId(tenantId)
                .userId(request.getUserId())
                .imageUrl(request.getImageUrl())
                .imageStoragePath(request.getImageStoragePath())
                .language(request.getLanguage())
                .preserveLayout(request.getPreserveLayout() != null ? request.getPreserveLayout() : true)
                .extractWords(request.getExtractWords() != null ? request.getExtractWords() : true)
                .minConfidence(request.getMinConfidence())
                .createdBy(request.getUserId())
                .build();

        ImageAnalysisDto result = computerVisionService.extractText(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PostMapping("/classify")
    @Operation(summary = "Classify image", description = "Classify an image into categories")
    public ResponseEntity<ImageAnalysisDto> classifyImage(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Valid @RequestBody ClassifyImageRequest request) {

        log.info("POST /api/v1/computer-vision/classify - Tenant: {}", tenantId);

        ClassifyImageCommand command = ClassifyImageCommand.builder()
                .tenantId(tenantId)
                .userId(request.getUserId())
                .imageUrl(request.getImageUrl())
                .imageStoragePath(request.getImageStoragePath())
                .width(request.getWidth())
                .height(request.getHeight())
                .topPredictions(request.getTopPredictions() != null ? request.getTopPredictions() : 5)
                .minConfidence(request.getMinConfidence())
                .modelName(request.getModelName())
                .createdBy(request.getUserId())
                .build();

        ImageAnalysisDto result = computerVisionService.classifyImage(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/analysis/{id}")
    @Operation(summary = "Get analysis by ID", description = "Retrieve an image analysis by its ID")
    public ResponseEntity<ImageAnalysisDto> getAnalysis(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @PathVariable String id) {

        log.info("GET /api/v1/computer-vision/analysis/{} - Tenant: {}", id, tenantId);

        GetImageAnalysisQuery query = GetImageAnalysisQuery.builder()
                .tenantId(tenantId)
                .id(java.util.UUID.fromString(id))
                .build();

        ImageAnalysisDto result = computerVisionService.getAnalysis(query);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/analysis")
    @Operation(summary = "List all analyses", description = "List all image analyses with pagination")
    public ResponseEntity<Page<ImageAnalysisDto>> listAnalyses(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String analysisType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        log.info("GET /api/v1/computer-vision/analysis - Tenant: {}", tenantId);

        ListImageAnalysesQuery query = ListImageAnalysesQuery.builder()
                .tenantId(tenantId)
                .userId(userId)
                .status(status)
                .analysisType(analysisType)
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();

        Page<ImageAnalysisDto> result = computerVisionService.listAnalyses(query);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/models")
    @Operation(summary = "List available models", description = "List all available computer vision models")
    public ResponseEntity<Map<String, Object>> getAvailableModels(
            @RequestHeader(value = "X-Tenant-ID", required = false) String tenantId,
            @RequestParam(required = false) String modelType) {

        log.info("GET /api/v1/computer-vision/models - Tenant: {}", tenantId);

        GetAvailableModelsQuery query = GetAvailableModelsQuery.builder()
                .modelType(modelType)
                .build();

        Map<String, Object> result = computerVisionService.getAvailableModels(query);
        return ResponseEntity.ok(result);
    }

    // Request DTOs as inner classes for simplicity

    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class AnalyzeImageRequest {
        private String userId;
        private String imageUrl;
        private String imageStoragePath;
        private ImageFormat format;
        private Long fileSize;
        private Integer width;
        private Integer height;
        private String analysisType;
    }

    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class DetectObjectsRequest {
        private String userId;
        private String imageUrl;
        private String imageStoragePath;
        private Integer width;
        private Integer height;
        private Double minConfidence;
        private Integer maxObjects;
    }

    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class DetectFacesRequest {
        private String userId;
        private String imageUrl;
        private String imageStoragePath;
        private Integer width;
        private Integer height;
        private Boolean detectEmotions;
        private Boolean detectAge;
        private Boolean detectGender;
        private Boolean detectLandmarks;
        private Double minConfidence;
    }

    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ExtractTextRequest {
        private String userId;
        private String imageUrl;
        private String imageStoragePath;
        private String language;
        private Boolean preserveLayout;
        private Boolean extractWords;
        private Double minConfidence;
    }

    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ClassifyImageRequest {
        private String userId;
        private String imageUrl;
        private String imageStoragePath;
        private Integer width;
        private Integer height;
        private Integer topPredictions;
        private Double minConfidence;
        private String modelName;
    }
}
