package com.gogidix.rapidassist.ai.matching.interfaces.rest.controller;

import com.gogidix.rapidassist.ai.matching.application.command.*;
import com.gogidix.rapidassist.ai.matching.application.dto.*;
import com.gogidix.rapidassist.ai.matching.application.query.*;
import com.gogidix.rapidassist.ai.matching.application.service.MatchingAlgorithmApplicationService;
import com.gogidix.rapidassist.ai.matching.interfaces.rest.request.*;
import com.gogidix.rapidassist.ai.matching.interfaces.rest.response.*;
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
import java.util.stream.Collectors;

/**
 * REST Controller for Matching Algorithm operations.
 * API endpoint: /api/v1/matching
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Matching Algorithm", description = "AI Matching Algorithm APIs")
@RequestMapping("/api/v1/matching")
public class MatchingAlgorithmRestController {

    private final MatchingAlgorithmApplicationService applicationService;

    /**
     * Execute a matching operation.
     * POST /api/v1/matching/execute
     */
    @PostMapping("/execute")
    @Operation(summary = "Execute matching", description = "Executes a matching operation between source and target entities")
    public ResponseEntity<MatchingResultResponse> executeMatching(
            @Valid @RequestBody ExecuteMatchingRequest request,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        var command = ExecuteMatchingCommand.builder()
                .tenantId(tenantId)
                .sourceEntityType(request.getSourceEntityType())
                .sourceEntityId(request.getSourceEntityId())
                .targetEntityType(request.getTargetEntityType())
                .targetEntityId(request.getTargetEntityId())
                .algorithmType(request.getAlgorithmType())
                .sourceAttributes(request.getSourceAttributes())
                .targetAttributes(request.getTargetAttributes())
                .minSimilarityThreshold(request.getMinSimilarityThreshold())
                .maxResults(request.getMaxResults())
                .metadata(request.getMetadata())
                .build();

        MatchingResultDto resultDto = applicationService.executeMatching(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toResponse(resultDto));
    }

    /**
     * Get a matching result by ID.
     * GET /api/v1/matching/results/{resultId}
     */
    @GetMapping("/results/{resultId}")
    @Operation(summary = "Get matching result", description = "Retrieves a matching result by ID")
    public ResponseEntity<MatchingResultResponse> getMatchingResult(
            @PathVariable UUID resultId,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        var query = GetMatchingResultQuery.builder()
                .tenantId(tenantId)
                .resultId(resultId)
                .build();

        MatchingResultDto resultDto = applicationService.getMatchingResult(query);

        return ResponseEntity.ok(toResponse(resultDto));
    }

    /**
     * Get all matching results for a tenant.
     * GET /api/v1/matching/results
     */
    @GetMapping("/results")
    @Operation(summary = "Get all matching results", description = "Retrieves all matching results for a tenant")
    public ResponseEntity<List<MatchingResultResponse>> getMatchingResults(
            @RequestHeader("X-Tenant-ID") String tenantId) {

        List<MatchingResultDto> results = applicationService.getMatchingResults(tenantId);

        return ResponseEntity.ok(results.stream()
                .map(this::toResponse)
                .collect(Collectors.toList()));
    }

    /**
     * Delete a matching result.
     * DELETE /api/v1/matching/results/{resultId}
     */
    @DeleteMapping("/results/{resultId}")
    @Operation(summary = "Delete matching result", description = "Deletes a matching result")
    public ResponseEntity<Void> deleteMatchingResult(
            @PathVariable UUID resultId,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        applicationService.deleteMatchingResult(tenantId, resultId);

        return ResponseEntity.noContent().build();
    }

    /**
     * Create a matching rule.
     * POST /api/v1/matching/rules
     */
    @PostMapping("/rules")
    @Operation(summary = "Create matching rule", description = "Creates a new matching rule")
    public ResponseEntity<MatchingRuleResponse> createMatchingRule(
            @Valid @RequestBody CreateMatchingRuleRequest request,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        var command = CreateMatchingRuleCommand.builder()
                .tenantId(tenantId)
                .ruleName(request.getRuleName())
                .ruleCode(request.getRuleCode())
                .description(request.getDescription())
                .sourceEntityType(request.getSourceEntityType())
                .targetEntityType(request.getTargetEntityType())
                .active(request.getActive())
                .priority(request.getPriority())
                .minimumSimilarityThreshold(request.getMinimumSimilarityThreshold())
                .confidenceThreshold(request.getConfidenceThreshold())
                .conditions(request.getConditions())
                .algorithmType(request.getAlgorithmType())
                .algorithmParameters(request.getAlgorithmParameters())
                .metadata(request.getMetadata())
                .build();

        MatchingRuleDto ruleDto = applicationService.createMatchingRule(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toRuleResponse(ruleDto));
    }

    /**
     * Get a matching rule.
     * GET /api/v1/matching/rules/{ruleId}
     */
    @GetMapping("/rules/{ruleId}")
    @Operation(summary = "Get matching rule", description = "Retrieves a matching rule by ID")
    public ResponseEntity<MatchingRuleResponse> getMatchingRule(
            @PathVariable UUID ruleId,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        var query = GetMatchingRuleQuery.builder()
                .tenantId(tenantId)
                .ruleId(ruleId)
                .build();

        MatchingRuleDto ruleDto = applicationService.getMatchingRule(query);

        return ResponseEntity.ok(toRuleResponse(ruleDto));
    }

    /**
     * Get all matching rules for a tenant.
     * GET /api/v1/matching/rules
     */
    @GetMapping("/rules")
    @Operation(summary = "Get all matching rules", description = "Retrieves all matching rules for a tenant")
    public ResponseEntity<List<MatchingRuleResponse>> getMatchingRules(
            @RequestHeader("X-Tenant-ID") String tenantId) {

        List<MatchingRuleDto> rules = applicationService.getMatchingRules(tenantId);

        return ResponseEntity.ok(rules.stream()
                .map(this::toRuleResponse)
                .collect(Collectors.toList()));
    }

    /**
     * Delete a matching rule.
     * DELETE /api/v1/matching/rules/{ruleId}
     */
    @DeleteMapping("/rules/{ruleId}")
    @Operation(summary = "Delete matching rule", description = "Deletes a matching rule")
    public ResponseEntity<Void> deleteMatchingRule(
            @PathVariable UUID ruleId,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        applicationService.deleteMatchingRule(tenantId, ruleId);

        return ResponseEntity.noContent().build();
    }

    /**
     * Create a batch matching job.
     * POST /api/v1/matching/batch-jobs
     */
    @PostMapping("/batch-jobs")
    @Operation(summary = "Create batch job", description = "Creates a new batch matching job")
    public ResponseEntity<BatchMatchingJobResponse> createBatchJob(
            @Valid @RequestBody CreateBatchJobRequest request,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        var command = CreateBatchMatchingJobCommand.builder()
                .tenantId(tenantId)
                .jobName(request.getJobName())
                .sourceEntityType(request.getSourceEntityType())
                .targetEntityType(request.getTargetEntityType())
                .algorithmType(request.getAlgorithmType())
                .sourceEntityIds(request.getSourceEntityIds())
                .targetEntityIds(request.getTargetEntityIds())
                .minSimilarityThreshold(request.getMinSimilarityThreshold())
                .batchSize(request.getBatchSize())
                .configuration(request.getConfiguration())
                .metadata(request.getMetadata())
                .build();

        BatchMatchingJobDto jobDto = applicationService.createBatchMatchingJob(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toBatchJobResponse(jobDto));
    }

    /**
     * Get a batch matching job.
     * GET /api/v1/matching/batch-jobs/{jobId}
     */
    @GetMapping("/batch-jobs/{jobId}")
    @Operation(summary = "Get batch job", description = "Retrieves a batch matching job by ID")
    public ResponseEntity<BatchMatchingJobResponse> getBatchJob(
            @PathVariable UUID jobId,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        var query = GetBatchJobQuery.builder()
                .tenantId(tenantId)
                .jobId(jobId)
                .build();

        BatchMatchingJobDto jobDto = applicationService.getBatchJob(query);

        return ResponseEntity.ok(toBatchJobResponse(jobDto));
    }

    /**
     * Update batch job status.
     * PATCH /api/v1/matching/batch-jobs/{jobId}/status
     */
    @PatchMapping("/batch-jobs/{jobId}/status")
    @Operation(summary = "Update batch job status", description = "Updates the status of a batch matching job")
    public ResponseEntity<BatchMatchingJobResponse> updateBatchJobStatus(
            @PathVariable UUID jobId,
            @Valid @RequestBody UpdateBatchJobStatusRequest request,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        var command = UpdateBatchJobStatusCommand.builder()
                .tenantId(tenantId)
                .jobId(jobId)
                .status(request.getStatus())
                .processedCount(request.getProcessedCount())
                .successCount(request.getSuccessCount())
                .failureCount(request.getFailureCount())
                .errorMessage(request.getErrorMessage())
                .build();

        BatchMatchingJobDto jobDto = applicationService.updateBatchJobStatus(command);

        return ResponseEntity.ok(toBatchJobResponse(jobDto));
    }

    /**
     * Get similarity scores for entity pair.
     * GET /api/v1/matching/similarity/{entity1Id}/{entity2Id}
     */
    @GetMapping("/similarity/{entity1Id}/{entity2Id}")
    @Operation(summary = "Get similarity scores", description = "Retrieves similarity scores for an entity pair")
    public ResponseEntity<List<SimilarityScoreResponse>> getSimilarityScores(
            @PathVariable String entity1Id,
            @PathVariable String entity2Id,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        List<SimilarityScoreDto> scores = applicationService.getSimilarityScores(tenantId, entity1Id, entity2Id);

        return ResponseEntity.ok(scores.stream()
                .map(this::toSimilarityScoreResponse)
                .collect(Collectors.toList()));
    }

    /**
     * Convert DTO to Response.
     */
    private MatchingResultResponse toResponse(MatchingResultDto dto) {
        return MatchingResultResponse.builder()
                .id(dto.getId())
                .tenantId(dto.getTenantId())
                .matchId(dto.getMatchId())
                .sourceEntityType(dto.getSourceEntityType())
                .sourceEntityId(dto.getSourceEntityId())
                .targetEntityType(dto.getTargetEntityType())
                .targetEntityId(dto.getTargetEntityId())
                .algorithmType(dto.getAlgorithmType())
                .similarityScore(dto.getSimilarityScore())
                .confidenceScore(dto.getConfidenceScore())
                .status(dto.getStatus())
                .matchingAttributes(dto.getMatchingAttributes())
                .metadata(dto.getMetadata())
                .version(dto.getVersion())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .createdBy(dto.getCreatedBy())
                .updatedBy(dto.getUpdatedBy())
                .build();
    }

    private MatchingRuleResponse toRuleResponse(MatchingRuleDto dto) {
        return MatchingRuleResponse.builder()
                .id(dto.getId())
                .tenantId(dto.getTenantId())
                .ruleName(dto.getRuleName())
                .ruleCode(dto.getRuleCode())
                .description(dto.getDescription())
                .sourceEntityType(dto.getSourceEntityType())
                .targetEntityType(dto.getTargetEntityType())
                .active(dto.getActive())
                .priority(dto.getPriority())
                .minimumSimilarityThreshold(dto.getMinimumSimilarityThreshold())
                .confidenceThreshold(dto.getConfidenceThreshold())
                .conditions(dto.getConditions())
                .algorithmType(dto.getAlgorithmType())
                .algorithmParameters(dto.getAlgorithmParameters())
                .metadata(dto.getMetadata())
                .version(dto.getVersion())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .createdBy(dto.getCreatedBy())
                .updatedBy(dto.getUpdatedBy())
                .build();
    }

    private BatchMatchingJobResponse toBatchJobResponse(BatchMatchingJobDto dto) {
        return BatchMatchingJobResponse.builder()
                .id(dto.getId())
                .tenantId(dto.getTenantId())
                .jobId(dto.getJobId())
                .jobName(dto.getJobName())
                .sourceEntityType(dto.getSourceEntityType())
                .targetEntityType(dto.getTargetEntityType())
                .algorithmType(dto.getAlgorithmType())
                .status(dto.getStatus())
                .sourceEntityIds(dto.getSourceEntityIds())
                .targetEntityIds(dto.getTargetEntityIds())
                .totalCount(dto.getTotalCount())
                .processedCount(dto.getProcessedCount())
                .successCount(dto.getSuccessCount())
                .failureCount(dto.getFailureCount())
                .minSimilarityThreshold(dto.getMinSimilarityThreshold())
                .batchSize(dto.getBatchSize())
                .configuration(dto.getConfiguration())
                .metadata(dto.getMetadata())
                .createdAt(dto.getCreatedAt())
                .startedAt(dto.getStartedAt())
                .completedAt(dto.getCompletedAt())
                .createdBy(dto.getCreatedBy())
                .updatedBy(dto.getUpdatedBy())
                .errorMessage(dto.getErrorMessage())
                .build();
    }

    private SimilarityScoreResponse toSimilarityScoreResponse(SimilarityScoreDto dto) {
        return SimilarityScoreResponse.builder()
                .id(dto.getId())
                .tenantId(dto.getTenantId())
                .entity1Id(dto.getEntity1Id())
                .entity1Type(dto.getEntity1Type())
                .entity2Id(dto.getEntity2Id())
                .entity2Type(dto.getEntity2Type())
                .score(dto.getScore())
                .algorithm(dto.getAlgorithm())
                .scoreDetails(dto.getScoreDetails())
                .metadata(dto.getMetadata())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
    }
}
