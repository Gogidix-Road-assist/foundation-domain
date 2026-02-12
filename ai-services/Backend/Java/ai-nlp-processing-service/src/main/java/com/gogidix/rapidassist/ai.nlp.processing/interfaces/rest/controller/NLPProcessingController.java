package com.gogidix.rapidassist.ai.nlp.processing.interfaces.rest.controller;

import com.gogidix.rapidassist.ai.nlp.processing.application.dto.TextProcessingDto;
import com.gogidix.rapidassist.ai.nlp.processing.application.dto.TextSimilarityDto;
import com.gogidix.rapidassist.ai.nlp.processing.application.service.NLPProcessingService;
import com.gogidix.rapidassist.ai.nlp.processing.domain.model.TextProcessing;
import com.gogidix.rapidassist.ai.nlp.processing.domain.model.TextSimilarity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "NLP Processing", description = "NLP Processing APIs")
@RequestMapping("/api/v1/nlp")
public class NLPProcessingController {

    private final NLPProcessingService nlpProcessingService;

    @PostMapping("/process")
    @Operation(summary = "Process text", description = "Process text with specified NLP operation")
    public ResponseEntity<TextProcessingDto> processText(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestParam String text,
            @RequestParam TextProcessing.ProcessingType processingType,
            @RequestParam(defaultValue = "system") String createdBy) {
        
        TextProcessingDto result = nlpProcessingService.processText(tenantId, text, processingType, createdBy);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/processing/{id}")
    @Operation(summary = "Get text processing", description = "Get text processing by ID")
    public ResponseEntity<TextProcessingDto> getTextProcessing(
            @PathVariable UUID id,
            @RequestHeader("X-Tenant-ID") String tenantId) {
        
        TextProcessingDto result = nlpProcessingService.getTextProcessing(tenantId, id);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/processing/status/{status}")
    @Operation(summary = "Get by status", description = "Get text processing by status")
    public ResponseEntity<List<TextProcessingDto>> getByStatus(
            @PathVariable TextProcessing.ProcessingStatus status,
            @RequestHeader("X-Tenant-ID") String tenantId) {
        
        List<TextProcessingDto> results = nlpProcessingService.getTextProcessingByStatus(tenantId, status);
        return ResponseEntity.ok(results);
    }

    @DeleteMapping("/processing/{id}")
    @Operation(summary = "Delete processing", description = "Delete text processing by ID")
    public ResponseEntity<Void> deleteProcessing(
            @PathVariable UUID id,
            @RequestHeader("X-Tenant-ID") String tenantId) {
        
        nlpProcessingService.deleteTextProcessing(tenantId, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/similarity")
    @Operation(summary = "Analyze similarity", description = "Analyze similarity between two texts")
    public ResponseEntity<TextSimilarityDto> analyzeSimilarity(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestParam String text1,
            @RequestParam String text2,
            @RequestParam(defaultValue = "COSINE") TextSimilarity.SimilarityMethod method,
            @RequestParam(defaultValue = "system") String createdBy) {
        
        TextSimilarityDto result = nlpProcessingService.analyzeSimilarity(tenantId, text1, text2, method, createdBy);
        return ResponseEntity.ok(result);
    }
}
