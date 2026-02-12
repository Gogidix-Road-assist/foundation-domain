package com.gogidix.rapidassist.ai.recommendation.application.service;

import com.gogidix.rapidassist.ai.recommendation.application.command.CreateRecommendationRequestCommand;
import com.gogidix.rapidassist.ai.recommendation.application.command.UpdateUserPreferenceCommand;
import com.gogidix.rapidassist.ai.recommendation.application.dto.RecommendationResultDto;
import com.gogidix.rapidassist.ai.recommendation.application.dto.UserPreferenceDto;
import com.gogidix.rapidassist.ai.recommendation.application.mapper.RecommendationResultMapper;
import com.gogidix.rapidassist.ai.recommendation.application.mapper.UserPreferenceMapper;
import com.gogidix.rapidassist.ai.recommendation.application.query.GetRecommendationQuery;
import com.gogidix.rapidassist.ai.recommendation.application.query.GetUserPreferencesQuery;
import com.gogidix.rapidassist.ai.recommendation.domain.model.*;
import com.gogidix.rapidassist.ai.recommendation.domain.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Application Service for Recommendation operations.
 * Implements business logic for collaborative filtering, content-based filtering, and hybrid recommendations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationApplicationService {

    private final RecommendationRequestRepositoryPort requestRepository;
    private final RecommendationResultRepositoryPort resultRepository;
    private final UserPreferenceRepositoryPort preferenceRepository;
    private final ItemSimilarityRepositoryPort similarityRepository;
    private final RecommendationResultMapper resultMapper;
    private final UserPreferenceMapper preferenceMapper;

    /**
     * Create and process a recommendation request.
     */
    public RecommendationResultDto createRecommendation(CreateRecommendationRequestCommand command) {
        log.info("Creating recommendation request for user: {} in tenant: {}", command.getUserId(), command.getTenantId());

        long startTime = System.currentTimeMillis();

        // Create request
        RecommendationRequest request = RecommendationRequest.builder()
                .id(UUID.randomUUID())
                .tenantId(command.getTenantId())
                .userId(command.getUserId())
                .recommendationType(command.getRecommendationType())
                .itemType(command.getItemType())
                .contextType(command.getContextType())
                .contextId(command.getContextId())
                .limit(command.getLimit() != null ? command.getLimit() : 10)
                .filters(command.getFilters())
                .parameters(command.getParameters())
                .status(RecommendationStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .expiresAt(command.getTtlMinutes() != null ?
                        LocalDateTime.now().plusMinutes(command.getTtlMinutes()) :
                        LocalDateTime.now().plusHours(24))
                .build();

        request = requestRepository.save(command.getTenantId(), request);

        // Process recommendation
        request.markAsProcessing();
        request = requestRepository.save(command.getTenantId(), request);

        List<RecommendationResult.RecommendedItem> recommendations = switch (command.getRecommendationType()) {
            case COLLABORATIVE_FILTERING -> generateCollaborativeFilteringRecommendations(command);
            case CONTENT_BASED -> generateContentBasedRecommendations(command);
            case HYBRID -> generateHybridRecommendations(command);
            case USER_BASED -> generateUserBasedRecommendations(command);
            case ITEM_BASED -> generateItemBasedRecommendations(command);
            default -> generateCollaborativeFilteringRecommendations(command);
        };

        long processingTime = System.currentTimeMillis() - startTime;

        // Create result
        RecommendationResult result = RecommendationResult.builder()
                .id(UUID.randomUUID())
                .tenantId(command.getTenantId())
                .requestId(request.getId())
                .userId(command.getUserId())
                .recommendationType(command.getRecommendationType())
                .itemType(command.getItemType())
                .status(RecommendationStatus.COMPLETED)
                .items(recommendations)
                .totalResults(recommendations.size())
                .processingTimeMs(processingTime)
                .algorithm(command.getRecommendationType().name())
                .createdAt(LocalDateTime.now())
                .expiresAt(request.getExpiresAt())
                .cached(false)
                .build();

        result.calculateConfidenceScore();
        result = resultRepository.save(command.getTenantId(), result);

        // Mark request as completed
        request.markAsCompleted();
        requestRepository.save(command.getTenantId(), request);

        log.info("Recommendation created: {} with {} items in {}ms", result.getId(), recommendations.size(), processingTime);
        return resultMapper.toDto(result);
    }

    /**
     * Get recommendation result by ID.
     */
    public RecommendationResultDto getRecommendation(GetRecommendationQuery query) {
        log.info("Getting recommendation: {} for tenant: {}", query.getResultId(), query.getTenantId());

        RecommendationResult result = resultRepository.findById(query.getTenantId(), query.getResultId())
                .orElseThrow(() -> new IllegalArgumentException("Recommendation not found: " + query.getResultId()));

        if (result.isExpired()) {
            throw new IllegalArgumentException("Recommendation has expired");
        }

        return resultMapper.toDto(result);
    }

    /**
     * Update user preference.
     */
    public UserPreferenceDto updateUserPreference(UpdateUserPreferenceCommand command) {
        log.info("Updating preference for user: {}, item: {} in tenant: {}",
                command.getUserId(), command.getItemId(), command.getTenantId());

        Optional<UserPreference> existingPref = preferenceRepository.findByUserAndItem(
                command.getTenantId(),
                command.getUserId(),
                command.getItemType(),
                command.getItemId()
        );

        UserPreference preference;
        if (existingPref.isPresent()) {
            preference = existingPref.get();
            preference.updateScore(command.getScoreDelta() != null ? command.getScoreDelta() : 0.1);
        } else {
            preference = UserPreference.builder()
                    .id(UUID.randomUUID())
                    .tenantId(command.getTenantId())
                    .userId(command.getUserId())
                    .itemType(command.getItemType())
                    .itemId(command.getItemId())
                    .preferenceKey(command.getPreferenceKey())
                    .preferenceValue(command.getPreferenceValue())
                    .preferenceScore(command.getScoreDelta() != null ? command.getScoreDelta() : 0.1)
                    .interactionCount(1)
                    .lastInteractionAt(LocalDateTime.now())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .metadata(command.getMetadata())
                    .build();
        }

        preference = preferenceRepository.save(command.getTenantId(), preference);
        return preferenceMapper.toDto(preference);
    }

    /**
     * Get user preferences.
     */
    public List<UserPreferenceDto> getUserPreferences(GetUserPreferencesQuery query) {
        log.info("Getting preferences for user: {} in tenant: {}", query.getUserId(), query.getTenantId());

        List<UserPreference> preferences;
        if (query.getItemType() != null) {
            preferences = preferenceRepository.findByUserIdAndItemType(
                    query.getTenantId(),
                    query.getUserId(),
                    query.getItemType()
            );
        } else {
            preferences = preferenceRepository.findByUserId(query.getTenantId(), query.getUserId());
        }

        return preferences.stream()
                .map(preferenceMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Generate collaborative filtering recommendations.
     */
    private List<RecommendationResult.RecommendedItem> generateCollaborativeFilteringRecommendations(
            CreateRecommendationRequestCommand command) {

        log.debug("Generating collaborative filtering recommendations for user: {}", command.getUserId());

        // Get user preferences
        List<UserPreference> userPreferences = preferenceRepository.findByUserId(
                command.getTenantId(),
                command.getUserId()
        );

        // Find similar users based on item preferences
        Map<String, Double> itemScores = new HashMap<>();

        for (UserPreference pref : userPreferences) {
            if (pref.getItemType().equals(command.getItemType())) {
                // Find similar items
                List<ItemSimilarity> similarities = similarityRepository.findByItem1(
                        command.getTenantId(),
                        command.getItemType(),
                        pref.getItemId()
                );

                for (ItemSimilarity similarity : similarities) {
                    double score = pref.getPreferenceScore() * similarity.getSimilarityScore();
                    itemScores.merge(similarity.getItem2Id(), score, Double::sum);
                }
            }
        }

        // Convert to recommended items
        return itemScores.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(command.getLimit() != null ? command.getLimit() : 10)
                .map(entry -> RecommendationResult.RecommendedItem.builder()
                        .itemId(entry.getKey())
                        .itemType(command.getItemType())
                        .score(entry.getValue())
                        .confidence(Math.min(1.0, entry.getValue()))
                        .reason("Collaborative filtering based on similar users")
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Generate content-based recommendations.
     */
    private List<RecommendationResult.RecommendedItem> generateContentBasedRecommendations(
            CreateRecommendationRequestCommand command) {

        log.debug("Generating content-based recommendations for user: {}", command.getUserId());

        // Get user's liked items
        List<UserPreference> userPreferences = preferenceRepository.findByUserIdAndItemType(
                command.getTenantId(),
                command.getUserId(),
                command.getItemType()
        );

        if (userPreferences.isEmpty()) {
            return List.of();
        }

        // Find similar items based on content
        Map<String, Double> itemScores = new HashMap<>();

        for (UserPreference pref : userPreferences) {
            if (pref.getPreferenceScore() > 0.5) {
                List<ItemSimilarity> similarities = similarityRepository.findByItem1(
                        command.getTenantId(),
                        command.getItemType(),
                        pref.getItemId()
                );

                for (ItemSimilarity similarity : similarities) {
                    double score = pref.getPreferenceScore() * similarity.getSimilarityScore();
                    itemScores.merge(similarity.getItem2Id(), score, Double::sum);
                }
            }
        }

        return itemScores.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(command.getLimit() != null ? command.getLimit() : 10)
                .map(entry -> RecommendationResult.RecommendedItem.builder()
                        .itemId(entry.getKey())
                        .itemType(command.getItemType())
                        .score(entry.getValue())
                        .confidence(Math.min(1.0, entry.getValue()))
                        .reason("Content-based filtering")
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Generate hybrid recommendations.
     */
    private List<RecommendationResult.RecommendedItem> generateHybridRecommendations(
            CreateRecommendationRequestCommand command) {

        log.debug("Generating hybrid recommendations for user: {}", command.getUserId());

        // Combine collaborative and content-based
        List<RecommendationResult.RecommendedItem> collaborative =
                generateCollaborativeFilteringRecommendations(command);
        List<RecommendationResult.RecommendedItem> contentBased =
                generateContentBasedRecommendations(command);

        // Merge and re-score
        Map<String, RecommendationResult.RecommendedItem> mergedItems = new HashMap<>();

        // Add collaborative items (weight 0.6)
        for (RecommendationResult.RecommendedItem item : collaborative) {
            RecommendationResult.RecommendedItem newItem = RecommendationResult.RecommendedItem.builder()
                    .itemId(item.getItemId())
                    .itemType(item.getItemType())
                    .score(item.getScore() * 0.6)
                    .confidence(item.getConfidence() * 0.6)
                    .reason("Hybrid: " + item.getReason())
                    .build();
            mergedItems.put(item.getItemId(), newItem);
        }

        // Add content-based items (weight 0.4)
        for (RecommendationResult.RecommendedItem item : contentBased) {
            if (mergedItems.containsKey(item.getItemId())) {
                RecommendationResult.RecommendedItem existing = mergedItems.get(item.getItemId());
                existing.setScore(existing.getScore() + item.getScore() * 0.4);
                existing.setConfidence(Math.min(1.0, existing.getConfidence() + item.getConfidence() * 0.4));
            } else {
                RecommendationResult.RecommendedItem newItem = RecommendationResult.RecommendedItem.builder()
                        .itemId(item.getItemId())
                        .itemType(item.getItemType())
                        .score(item.getScore() * 0.4)
                        .confidence(item.getConfidence() * 0.4)
                        .reason("Hybrid: " + item.getReason())
                        .build();
                mergedItems.put(item.getItemId(), newItem);
            }
        }

        return mergedItems.values().stream()
                .sorted(Comparator.comparing(RecommendationResult.RecommendedItem::getScore).reversed())
                .limit(command.getLimit() != null ? command.getLimit() : 10)
                .collect(Collectors.toList());
    }

    /**
     * Generate user-based recommendations.
     */
    private List<RecommendationResult.RecommendedItem> generateUserBasedRecommendations(
            CreateRecommendationRequestCommand command) {

        log.debug("Generating user-based recommendations for user: {}", command.getUserId());
        // Simplified implementation - same as collaborative for now
        return generateCollaborativeFilteringRecommendations(command);
    }

    /**
     * Generate item-based recommendations.
     */
    private List<RecommendationResult.RecommendedItem> generateItemBasedRecommendations(
            CreateRecommendationRequestCommand command) {

        log.debug("Generating item-based recommendations for user: {}", command.getUserId());
        // Simplified implementation - same as content-based for now
        return generateContentBasedRecommendations(command);
    }

    /**
     * Delete expired recommendation results.
     */
    public int deleteExpiredResults(String tenantId) {
        log.info("Deleting expired recommendation results for tenant: {}", tenantId);

        List<RecommendationResult> expiredResults = resultRepository.findExpiredResults(tenantId);
        int deletedCount = expiredResults.size();

        for (RecommendationResult result : expiredResults) {
            resultRepository.delete(tenantId, result.getId());
        }

        log.info("Deleted {} expired recommendation results", deletedCount);
        return deletedCount;
    }
}
