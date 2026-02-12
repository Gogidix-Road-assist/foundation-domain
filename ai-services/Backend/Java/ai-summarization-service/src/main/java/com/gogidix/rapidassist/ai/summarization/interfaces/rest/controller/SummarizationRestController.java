package com.gogidix.rapidassist.ai.summarization.interfaces.rest.controller;

import com.gogidix.rapidassist.ai.summarization.application.command.CreateSummarizationRequestCommand;
import com.gogidix.rapidassist.ai.summarization.application.command.ProcessSummarizationCommand;
import com.gogidix.rapidassist.ai.summarization.application.dto.SummarizationRequestDto;
import com.gogidix.rapidassist.ai.summarization.application.dto.SummaryDto;
import com.gogidix.rapidassist.ai.summarization.application.query.GetSummarizationRequestQuery;
import com.gogidix.rapidassist.ai.summarization.application.service.SummarizationRequestApplicationService;
import com.gogidix.rapidassist.ai.summarization.domain.tenant.TenantContext;
import com.gogidix.rapidassist.ai.summarization.interfaces.rest.request.CreateSummarizationRequestRequest;
import com.gogidix.rapidassist.ai.summarization.interfaces.rest.response.SummarizationRequestResponse;
import com.gogidix.rapidassist.ai.summarization.interfaces.rest.response.SummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@Tag(name = "Summarization", description = "AI Summarization APIs")
public class SummarizationRestController {

    private final SummarizationRequestApplicationService applicationService;

    @PostMapping("/api/v1/summarization/requests")
    @Operation(summary = "Create summarization request", description = "Creates a new summarization request")
    public ResponseEntity<SummarizationRequestResponse> createRequest(
            @Valid @RequestBody CreateSummarizationRequestRequest request,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader(value = "X-User-ID", defaultValue = "system") String userId) {

        TenantContext.setTenantId(tenantId);

        var command = CreateSummarizationRequestCommand.builder()
                .tenantId(tenantId)
                .requestId(request.getRequestId())
                .summarizationType(request.getSummarizationType())
                .summaryLength(request.getSummaryLength())
                .sourceTexts(request.getSourceTexts())
                .documentUrls(request.getDocumentUrls())
                .options(request.getOptions())
                .createdBy(userId)
                .build();

        SummarizationRequestDto requestDto = applicationService.createRequest(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toRequestResponse(requestDto));
    }

    @GetMapping("/api/v1/summarization/requests/{requestId}")
    @Operation(summary = "Get summarization request", description = "Retrieves a summarization request by ID")
    public ResponseEntity<SummarizationRequestResponse> getRequest(
            @PathVariable UUID requestId,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);

        var query = GetSummarizationRequestQuery.builder()
                .tenantId(tenantId)
                .requestId(requestId)
                .build();

        SummarizationRequestDto requestDto = applicationService.getRequest(query);

        return ResponseEntity.ok(toRequestResponse(requestDto));
    }

    @PostMapping("/api/v1/summarization/requests/{requestId}/process")
    @Operation(summary = "Process summarization", description = "Processes a summarization request")
    public ResponseEntity<SummaryResponse> processSummarization(
            @PathVariable UUID requestId,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);

        var command = ProcessSummarizationCommand.builder()
                .tenantId(tenantId)
                .requestId(requestId)
                .build();

        SummaryDto summaryDto = applicationService.processSummarization(command);

        return ResponseEntity.ok(toSummaryResponse(summaryDto));
    }

    @DeleteMapping("/api/v1/summarization/requests/{requestId}")
    @Operation(summary = "Delete summarization request", description = "Deletes a summarization request")
    public ResponseEntity<Void> deleteRequest(
            @PathVariable UUID requestId,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);
        applicationService.deleteRequest(tenantId, requestId);

        return ResponseEntity.noContent().build();
    }

    private SummarizationRequestResponse toRequestResponse(SummarizationRequestDto dto) {
        return SummarizationRequestResponse.builder()
                .id(dto.getId())
                .tenantId(dto.getTenantId())
                .requestId(dto.getRequestId())
                .summarizationType(dto.getSummarizationType())
                .summaryLength(dto.getSummaryLength())
                .sourceTexts(dto.getSourceTexts())
                .documentUrls(dto.getDocumentUrls())
                .options(dto.getOptions())
                .status(dto.getStatus())
                .errorMessage(dto.getErrorMessage())
                .createdBy(dto.getCreatedBy())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .completedAt(dto.getCompletedAt())
                .build();
    }

    private SummaryResponse toSummaryResponse(SummaryDto dto) {
        return SummaryResponse.builder()
                .id(dto.getId())
                .tenantId(dto.getTenantId())
                .summarizationRequestId(dto.getSummarizationRequestId())
                .content(dto.getContent())
                .summarizationType(dto.getSummarizationType())
                .summaryLength(dto.getSummaryLength())
                .originalWordCount(dto.getOriginalWordCount())
                .summaryWordCount(dto.getSummaryWordCount())
                .compressionRatio(dto.getCompressionRatio())
                .qualityScore(dto.getQualityScore())
                .keySentences(dto.getKeySentences())
                .keyPhrases(dto.getKeyPhrases())
                .metadata(dto.getMetadata())
                .version(dto.getVersion())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .createdBy(dto.getCreatedBy())
                .updatedBy(dto.getUpdatedBy())
                .build();
    }
}
