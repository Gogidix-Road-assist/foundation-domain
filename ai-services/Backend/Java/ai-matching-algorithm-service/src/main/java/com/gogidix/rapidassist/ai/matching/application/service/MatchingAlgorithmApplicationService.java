package com.gogidix.rapidassist.ai.matching.application.service;

import com.gogidix.rapidassist.ai.matching.application.command.*;
import com.gogidix.rapidassist.ai.matching.application.dto.*;
import com.gogidix.rapidassist.ai.matching.application.mapper.*;
import com.gogidix.rapidassist.ai.matching.application.query.*;
import com.gogidix.rapidassist.ai.matching.domain.model.*;
import com.gogidix.rapidassist.ai.matching.domain.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Application Service for Matching Algorithm operations.
 * Implements business logic and orchestrates domain operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MatchingAlgorithmApplicationService {

    private final MatchingResultRepositoryPort resultRepository;
    private final MatchingRuleRepositoryPort ruleRepository;
    private final SimilarityScoreRepositoryPort similarityScoreRepository;
    private final BatchMatchingJobRepositoryPort batchJobRepository;

    private final MatchingResultMapper resultMapper;
    private final MatchingRuleMapper ruleMapper;
    private final SimilarityScoreMapper similarityScoreMapper;
    private final BatchMatchingJobMapper batchJobMapper;

    /**
     * Execute a matching operation between source and target entities.
     */
    public MatchingResultDto executeMatching(ExecuteMatchingCommand command) {
        log.info("Executing matching: source={}({}) -> target={}({}) for tenant: {} using algorithm: {}",
                command.getSourceEntityType(), command.getSourceEntityId(),
                command.getTargetEntityType(), command.getTargetEntityId(),
                command.getTenantId(), command.getAlgorithmType());

        // Create matching result
        var result = MatchingResult.builder()
                .id(UUID.randomUUID())
                .tenantId(command.getTenantId())
                .matchId(UUID.randomUUID().toString())
                .sourceEntityType(command.getSourceEntityType())
                .sourceEntityId(command.getSourceEntityId())
                .targetEntityType(command.getTargetEntityType())
                .targetEntityId(command.getTargetEntityId())
                .algorithmType(command.getAlgorithmType())
                .matchingAttributes(command.getSourceAttributes())
                .metadata(command.getMetadata())
                .status(MatchingStatus.COMPLETED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Calculate similarity score based on algorithm type
        double similarityScore = calculateSimilarity(command.getAlgorithmType(),
                command.getSourceAttributes(), command.getTargetAttributes());
        result.setSimilarityScore(similarityScore);

        // Apply matching rules
        List<MatchingRule> rules = ruleRepository.findByEntityType(command.getTenantId(), command.getSourceEntityType(), command.getTargetEntityType());
        for (MatchingRule rule : rules) {
            if (rule.isActive()) {
                applyMatchingRule(result, rule);
            }
        }

        // Save result
        var savedResult = resultRepository.save(command.getTenantId(), result);

        // Save similarity score
        var similarityScoreEntity = SimilarityScore.builder()
                .id(UUID.randomUUID())
                .tenantId(command.getTenantId())
                .sourceEntityId(command.getSourceEntityId())
                .sourceEntityType(command.getSourceEntityType())
                .targetEntityId(command.getTargetEntityId())
                .targetEntityType(command.getTargetEntityType())
                .overallSimilarity(similarityScore)
                .algorithmType(command.getAlgorithmType())
                .createdAt(LocalDateTime.now())
                .build();
        similarityScoreRepository.save(command.getTenantId(), similarityScoreEntity);

        log.info("Matching executed successfully: matchId={}, score={}",
                savedResult.getMatchId(), savedResult.getSimilarityScore());
        return resultMapper.toDto(savedResult);
    }

    /**
     * Create a new matching rule.
     */
    public MatchingRuleDto createMatchingRule(CreateMatchingRuleCommand command) {
        log.info("Creating matching rule: {} for tenant: {}", command.getRuleName(), command.getTenantId());

        var rule = MatchingRule.builder()
                .id(UUID.randomUUID())
                .tenantId(command.getTenantId())
                .ruleName(command.getRuleName())
                .ruleCode(command.getRuleCode())
                .description(command.getDescription())
                .sourceEntityType(command.getSourceEntityType())
                .targetEntityType(command.getTargetEntityType())
                .active(command.getActive() != null ? command.getActive() : true)
                .priority(command.getPriority())
                .minimumSimilarityThreshold(command.getMinimumSimilarityThreshold())
                .confidenceThreshold(command.getConfidenceThreshold())
                .conditions(command.getConditions())
                .algorithmType(command.getAlgorithmType())
                .algorithmParameters(command.getAlgorithmParameters())
                .metadata(command.getMetadata())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        var savedRule = ruleRepository.save(command.getTenantId(), rule);

        log.info("Matching rule created: {}", savedRule.getId());
        return ruleMapper.toDto(savedRule);
    }

    /**
     * Get a matching result by ID.
     */
    public MatchingResultDto getMatchingResult(GetMatchingResultQuery query) {
        log.info("Getting matching result: {} for tenant: {}", query.getResultId(), query.getTenantId());

        var result = resultRepository.findById(query.getTenantId(), query.getResultId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Matching result not found: " + query.getResultId()));

        return resultMapper.toDto(result);
    }

    /**
     * Get a matching rule.
     */
    public MatchingRuleDto getMatchingRule(GetMatchingRuleQuery query) {
        log.info("Getting matching rule for tenant: {}", query.getTenantId());

        if (query.getRuleId() != null) {
            var rule = ruleRepository.findById(query.getTenantId(), query.getRuleId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Matching rule not found: " + query.getRuleId()));
            return ruleMapper.toDto(rule);
        } else if (query.getRuleCode() != null) {
            var rule = ruleRepository.findByRuleCode(query.getTenantId(), query.getRuleCode())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Matching rule not found: " + query.getRuleCode()));
            return ruleMapper.toDto(rule);
        }

        throw new IllegalArgumentException("Either ruleId or ruleCode must be provided");
    }

    /**
     * Get all matching results for a tenant.
     */
    public List<MatchingResultDto> getMatchingResults(String tenantId) {
        log.info("Getting all matching results for tenant: {}", tenantId);

        var results = resultRepository.findByTenantId(tenantId);
        return resultMapper.toDtoList(results);
    }

    /**
     * Get all matching rules for a tenant.
     */
    public List<MatchingRuleDto> getMatchingRules(String tenantId) {
        log.info("Getting all matching rules for tenant: {}", tenantId);

        var rules = ruleRepository.findByTenantId(tenantId);
        return ruleMapper.toDtoList(rules);
    }

    /**
     * Create a batch matching job.
     */
    public BatchMatchingJobDto createBatchMatchingJob(CreateBatchMatchingJobCommand command) {
        log.info("Creating batch matching job: {} for tenant: {}", command.getJobName(), command.getTenantId());

        var batchJob = BatchMatchingJob.builder()
                .id(UUID.randomUUID())
                .tenantId(command.getTenantId())
                .jobCode(UUID.randomUUID().toString())
                .jobName(command.getJobName())
                .sourceEntityType(command.getSourceEntityType())
                .targetEntityType(command.getTargetEntityType())
                .algorithmType(command.getAlgorithmType())
                .sourceEntityIds(command.getSourceEntityIds())
                .targetEntityIds(command.getTargetEntityIds())
                .totalRecords(command.getSourceEntityIds().size())
                .processedRecords(0)
                .successfulMatches(0)
                .failedRecords(0)
                .metadata(command.getMetadata())
                .status(BatchJobStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        var savedJob = batchJobRepository.save(command.getTenantId(), batchJob);

        log.info("Batch matching job created: {}", savedJob.getId());
        return batchJobMapper.toDto(savedJob);
    }

    /**
     * Get a batch matching job.
     */
    public BatchMatchingJobDto getBatchJob(GetBatchJobQuery query) {
        log.info("Getting batch job for tenant: {}", query.getTenantId());

        if (query.getJobId() != null) {
            var job = batchJobRepository.findById(query.getTenantId(), query.getJobId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Batch job not found: " + query.getJobId()));
            return batchJobMapper.toDto(job);
        } else if (query.getJobCode() != null) {
            var job = batchJobRepository.findByJobCode(query.getTenantId(), query.getJobCode())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Batch job not found: " + query.getJobCode()));
            return batchJobMapper.toDto(job);
        }

        throw new IllegalArgumentException("Either jobId or jobCode must be provided");
    }

    /**
     * Update batch job status.
     */
    public BatchMatchingJobDto updateBatchJobStatus(UpdateBatchJobStatusCommand command) {
        log.info("Updating batch job status: {} to {} for tenant: {}",
                command.getJobId(), command.getStatus(), command.getTenantId());

        var job = batchJobRepository.findById(command.getTenantId(), command.getJobId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Batch job not found: " + command.getJobId()));

        job.setStatus(command.getStatus());
        if (command.getProcessedCount() != null) {
            job.setProcessedRecords(command.getProcessedCount());
        }
        if (command.getSuccessCount() != null) {
            job.setSuccessfulMatches(command.getSuccessCount());
        }
        if (command.getFailureCount() != null) {
            job.setFailedRecords(command.getFailureCount());
        }
        if (command.getErrorMessage() != null) {
            job.setErrorMessage(command.getErrorMessage());
        }
        job.setUpdatedAt(LocalDateTime.now());

        if (command.getStatus() == BatchJobStatus.COMPLETED) {
            job.setCompletedAt(LocalDateTime.now());
        }

        var savedJob = batchJobRepository.save(command.getTenantId(), job);

        log.info("Batch job status updated: {}", savedJob.getStatus());
        return batchJobMapper.toDto(savedJob);
    }

    /**
     * Get similarity scores for entity pair.
     */
    public List<SimilarityScoreDto> getSimilarityScores(String tenantId, String entity1Id, String entity2Id) {
        log.info("Getting similarity scores for entity pair: {} - {} in tenant: {}",
                entity1Id, entity2Id, tenantId);

        // We need entity type information for proper query
        // For now, return empty list as we need source/target entity types
        log.warn("getSimilarityScores requires entity type information");
        return List.of();
    }

    /**
     * Delete a matching result.
     */
    public void deleteMatchingResult(String tenantId, UUID resultId) {
        log.info("Deleting matching result: {} for tenant: {}", resultId, tenantId);

        if (!resultRepository.exists(tenantId, resultId)) {
            throw new IllegalArgumentException("Matching result not found: " + resultId);
        }

        resultRepository.delete(tenantId, resultId);
        log.info("Matching result deleted: {}", resultId);
    }

    /**
     * Delete a matching rule.
     */
    public void deleteMatchingRule(String tenantId, UUID ruleId) {
        log.info("Deleting matching rule: {} for tenant: {}", ruleId, tenantId);

        if (!ruleRepository.exists(tenantId, ruleId)) {
            throw new IllegalArgumentException("Matching rule not found: " + ruleId);
        }

        ruleRepository.delete(tenantId, ruleId);
        log.info("Matching rule deleted: {}", ruleId);
    }

    /**
     * Calculate similarity score based on algorithm type.
     */
    private double calculateSimilarity(MatchingAlgorithmType algorithmType,
                                      Object source, Object target) {
        // Simplified similarity calculation
        // In production, this would use actual ML models or sophisticated algorithms
        return switch (algorithmType) {
            case EXACT_MATCHING -> source.equals(target) ? 1.0 : 0.0;
            case FUZZY_MATCHING -> calculateFuzzySimilarity(source, target);
            case SEMANTIC_MATCHING -> calculateSemanticSimilarity(source, target);
            case COSINE_SIMILARITY -> calculateCosineSimilarity(source, target);
            case JACCARD_SIMILARITY -> calculateJaccardSimilarity(source, target);
            case LEVENSHTEIN_DISTANCE -> calculateLevenshteinSimilarity(source, target);
            default -> 0.5;
        };
    }

    private double calculateFuzzySimilarity(Object source, Object target) {
        // Simplified fuzzy matching (Levenshtein distance would go here)
        String sourceStr = source.toString().toLowerCase();
        String targetStr = target.toString().toLowerCase();
        if (sourceStr.equals(targetStr)) return 1.0;
        if (sourceStr.contains(targetStr) || targetStr.contains(sourceStr)) return 0.8;
        return 0.5;
    }

    private double calculateSemanticSimilarity(Object source, Object target) {
        // Placeholder for semantic similarity
        // In production, this would use embeddings or NLP models
        return 0.7;
    }

    private double calculateCosineSimilarity(Object source, Object target) {
        // Placeholder for cosine similarity
        return 0.65;
    }

    private double calculateJaccardSimilarity(Object source, Object target) {
        // Placeholder for Jaccard similarity
        return 0.6;
    }

    private double calculateLevenshteinSimilarity(Object source, Object target) {
        // Placeholder for Levenshtein similarity
        return 0.7;
    }

    /**
     * Apply matching rule to result.
     */
    private void applyMatchingRule(MatchingResult result, MatchingRule rule) {
        // Simplified rule application
        // In production, this would evaluate conditions and execute actions
        if (result.getSimilarityScore() != null && result.getSimilarityScore() > 0.8) {
            result.setConfidenceScore(result.getSimilarityScore() * 1.1);
            if (result.getConfidenceScore() > 1.0) {
                result.setConfidenceScore(1.0);
            }
        }
    }
}
