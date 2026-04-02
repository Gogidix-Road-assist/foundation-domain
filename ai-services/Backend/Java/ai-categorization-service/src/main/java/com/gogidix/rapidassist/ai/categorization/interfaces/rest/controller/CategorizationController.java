package com.gogidix.rapidassist.ai.categorization.interfaces.rest.controller;

import com.gogidix.rapidassist.ai.categorization.application.service.CategorizationService;
import com.gogidix.rapidassist.ai.categorization.domain.model.CategorizationRequest;
import com.gogidix.rapidassist.ai.categorization.domain.model.CategorizationResult;
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
@RequestMapping("/api/v1/categorization")
@RequiredArgsConstructor
@Tag(name = "Categorization", description = "AI Content Categorization APIs")
public class CategorizationController {

    private final CategorizationService categorizationService;

    @PostMapping("/requests")
    @Operation(summary = "Create categorization request", description = "Creates a new categorization request")
    public ResponseEntity<CategorizationRequest> createRequest(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Valid @RequestBody CreateCategorizationRequest request) {
        
        CategorizationRequest categorizationRequest = categorizationService.createCategorizationRequest(
                tenantId,
                request.contentId(),
                request.contentType(),
                request.content(),
                request.taxonomyId()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(categorizationRequest);
    }

    @PostMapping("/categorize")
    @Operation(summary = "Categorize content", description = "Categorizes content using AI")
    public ResponseEntity<CategorizationResult> categorizeContent(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Valid @RequestBody CategorizeContentRequest request) {
        
        CategorizationResult result = categorizationService.categorizeContent(
                tenantId,
                request.contentId(),
                request.contentType(),
                request.content(),
                request.taxonomyId()
        );
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/results/{resultId}")
    @Operation(summary = "Get categorization result", description = "Retrieves a categorization result by ID")
    public ResponseEntity<CategorizationResult> getResult(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @PathVariable UUID resultId) {
        
        CategorizationResult result = categorizationService.getCategorizationResult(tenantId, resultId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/results/content/{contentId}")
    @Operation(summary = "Get results by content ID", description = "Retrieves all categorization results for content")
    public ResponseEntity<List<CategorizationResult>> getResultsByContentId(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @PathVariable String contentId) {
        
        List<CategorizationResult> results = categorizationService.getResultsByContentId(tenantId, contentId);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/requests/pending")
    @Operation(summary = "Get pending requests", description = "Retrieves all pending categorization requests")
    public ResponseEntity<List<CategorizationRequest>> getPendingRequests(
            @RequestHeader("X-Tenant-ID") String tenantId) {
        
        List<CategorizationRequest> requests = categorizationService.getPendingRequests(tenantId);
        return ResponseEntity.ok(requests);
    }

    public record CreateCategorizationRequest(
        String contentId,
        String contentType,
        String content,
        UUID taxonomyId
    ) {}

    public record CategorizeContentRequest(
        String contentId,
        String contentType,
        String content,
        UUID taxonomyId
    ) {}
}
