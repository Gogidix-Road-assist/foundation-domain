package com.gogidix.rapidassist.ai.tagging.application.service;

import com.gogidix.rapidassist.ai.tagging.application.command.*;
import com.gogidix.rapidassist.ai.tagging.application.dto.*;
import com.gogidix.rapidassist.ai.tagging.application.mapper.*;
import com.gogidix.rapidassist.ai.tagging.application.query.GetContentTagsQuery;
import com.gogidix.rapidassist.ai.tagging.application.query.GetTagQuery;
import com.gogidix.rapidassist.ai.tagging.application.query.ListTagsQuery;
import com.gogidix.rapidassist.ai.tagging.domain.model.*;
import com.gogidix.rapidassist.ai.tagging.domain.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Application Service for AI Automated Tagging operations
 * Implements use cases for tag management and content tagging
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TaggingApplicationService {

    private final TagRepositoryPort tagRepository;
    private final TagCategoryRepositoryPort categoryRepository;
    private final ContentTagRepositoryPort contentTagRepository;
    private final TaggingRuleRepositoryPort ruleRepository;
    private final TagSuggestionRepositoryPort suggestionRepository;

    private final TagMapper tagMapper;
    private final TagCategoryMapper categoryMapper;
    private final ContentTagMapper contentTagMapper;
    private final TaggingRuleMapper ruleMapper;
    private final TagSuggestionMapper suggestionMapper;

    // ============================================================
    // Tag Operations
    // ============================================================

    /**
     * Create a new tag
     */
    public TagDto createTag(CreateTagCommand command) {
        log.info("Creating tag for tenant: {}, name: {}", command.getTenantId(), command.getName());

        // Check if tag name already exists
        if (tagRepository.existsByTenantIdAndName(command.getTenantId(), command.getName())) {
            throw new IllegalArgumentException("Tag with name '" + command.getName() + "' already exists");
        }

        // Fetch category if provided
        TagCategory category = null;
        if (command.getCategoryId() != null) {
            category = categoryRepository.findByTenantIdAndId(command.getTenantId(), command.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        }

        Tag tag = Tag.builder()
                .id(UUID.randomUUID())
                .tenantId(command.getTenantId())
                .name(command.getName())
                .description(command.getDescription())
                .color(command.getColor())
                .category(category)
                .status(Tag.TagStatus.ACTIVE)
                .usageCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy(command.getCreatedBy())
                .version(1L)
                .build();

        Tag savedTag = tagRepository.save(tag);
        log.info("Tag created successfully with ID: {}", savedTag.getId());
        return tagMapper.toDto(savedTag);
    }

    /**
     * Update an existing tag
     */
    public TagDto updateTag(UpdateTagCommand command) {
        log.info("Updating tag: {} for tenant: {}", command.getTagId(), command.getTenantId());

        Tag tag = tagRepository.findByTenantIdAndId(command.getTenantId(), command.getTagId())
                .orElseThrow(() -> new IllegalArgumentException("Tag not found"));

        // Check if new name conflicts with existing tag
        if (!tag.getName().equals(command.getName()) &&
                tagRepository.existsByTenantIdAndName(command.getTenantId(), command.getName())) {
            throw new IllegalArgumentException("Tag with name '" + command.getName() + "' already exists");
        }

        // Fetch category if provided
        TagCategory category = null;
        if (command.getCategoryId() != null) {
            category = categoryRepository.findByTenantIdAndId(command.getTenantId(), command.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        }

        tag.setName(command.getName());
        tag.setDescription(command.getDescription());
        tag.setColor(command.getColor());
        tag.setCategory(category);
        tag.setUpdatedBy(command.getUpdatedBy());
        tag.setUpdatedAt(LocalDateTime.now());
        tag.setVersion(tag.getVersion() + 1);

        Tag updatedTag = tagRepository.save(tag);
        log.info("Tag updated successfully");
        return tagMapper.toDto(updatedTag);
    }

    /**
     * Get tag by ID
     */
    @Transactional(readOnly = true)
    public TagDto getTag(GetTagQuery query) {
        log.info("Fetching tag: {} for tenant: {}", query.getTagId(), query.getTenantId());

        Tag tag = tagRepository.findByTenantIdAndId(query.getTenantId(), query.getTagId())
                .orElseThrow(() -> new IllegalArgumentException("Tag not found"));

        return tagMapper.toDto(tag);
    }

    /**
     * List tags with filters
     */
    @Transactional(readOnly = true)
    public List<TagDto> listTags(ListTagsQuery query) {
        log.info("Listing tags for tenant: {} with filters: {}", query.getTenantId(), query);

        List<Tag> tags;

        if (query.getCategoryId() != null) {
            tags = tagRepository.findByTenantIdAndCategoryId(query.getTenantId(), query.getCategoryId());
        } else if (query.getStatus() != null) {
            tags = tagRepository.findByTenantIdAndStatus(query.getTenantId(), query.getStatus());
        } else if (query.getSearchKeyword() != null && !query.getSearchKeyword().isEmpty()) {
            tags = tagRepository.findByTenantIdAndNameContainingIgnoreCase(query.getTenantId(), query.getSearchKeyword());
        } else {
            tags = tagRepository.findByTenantId(query.getTenantId());
        }

        return tagMapper.toDtoList(tags);
    }

    /**
     * Delete tag
     */
    public void deleteTag(String tenantId, UUID tagId) {
        log.info("Deleting tag: {} for tenant: {}", tagId, tenantId);

        Tag tag = tagRepository.findByTenantIdAndId(tenantId, tagId)
                .orElseThrow(() -> new IllegalArgumentException("Tag not found"));

        // Check if tag is in use
        long usageCount = contentTagRepository.countByTenantIdAndTagId(tenantId, tagId);
        if (usageCount > 0) {
            throw new IllegalStateException("Cannot delete tag that is in use. Detach from all content first.");
        }

        tagRepository.deleteByTenantIdAndId(tenantId, tagId);
        log.info("Tag deleted successfully");
    }

    // ============================================================
    // Tag Category Operations
    // ============================================================

    /**
     * Create a new tag category
     */
    public TagCategoryDto createCategory(CreateTagCategoryCommand command) {
        log.info("Creating category for tenant: {}, name: {}", command.getTenantId(), command.getName());

        if (categoryRepository.existsByTenantIdAndName(command.getTenantId(), command.getName())) {
            throw new IllegalArgumentException("Category with name '" + command.getName() + "' already exists");
        }

        TagCategory category = TagCategory.builder()
                .id(UUID.randomUUID())
                .tenantId(command.getTenantId())
                .name(command.getName())
                .description(command.getDescription())
                .icon(command.getIcon())
                .displayOrder(command.getDisplayOrder())
                .status(TagCategory.CategoryStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy(command.getCreatedBy())
                .version(1L)
                .build();

        TagCategory savedCategory = categoryRepository.save(category);
        log.info("Category created successfully with ID: {}", savedCategory.getId());
        return categoryMapper.toDto(savedCategory);
    }

    /**
     * List all categories
     */
    @Transactional(readOnly = true)
    public List<TagCategoryDto> listCategories(String tenantId) {
        log.info("Listing categories for tenant: {}", tenantId);

        List<TagCategory> categories = categoryRepository.findByTenantIdOrderByDisplayOrderAsc(tenantId);
        return categoryMapper.toDtoList(categories);
    }

    // ============================================================
    // Content Tagging Operations
    // ============================================================

    /**
     * Tag content with specified tags
     */
    public List<ContentTagDto> tagContent(TagContentCommand command) {
        log.info("Tagging content: {} for tenant: {}", command.getContentId(), command.getTenantId());

        List<ContentTag> contentTags = new ArrayList<>();

        for (UUID tagId : command.getTagIds()) {
            // Check if tag exists
            Tag tag = tagRepository.findByTenantIdAndId(command.getTenantId(), tagId)
                    .orElseThrow(() -> new IllegalArgumentException("Tag not found: " + tagId));

            // Check if content is already tagged with this tag
            if (contentTagRepository.existsByTenantIdAndContentIdAndTagId(
                    command.getTenantId(), command.getContentId(), tagId)) {
                log.warn("Content already tagged with tag: {}", tagId);
                continue;
            }

            ContentTag contentTag = ContentTag.builder()
                    .id(UUID.randomUUID())
                    .tenantId(command.getTenantId())
                    .contentId(command.getContentId())
                    .contentType(command.getContentType())
                    .tagId(tagId)
                    .taggingSource(command.getTaggingSource() != null ?
                            command.getTaggingSource() : ContentTag.TaggingSource.MANUAL)
                    .confidenceScore(command.getConfidenceThreshold())
                    .manuallyVerified(false)
                    .taggedBy(command.getTaggedBy())
                    .createdAt(LocalDateTime.now())
                    .createdBy(command.getTaggedBy())
                    .version(1L)
                    .build();

            ContentTag savedContentTag = contentTagRepository.save(contentTag);
            contentTags.add(savedContentTag);

            // Increment tag usage count
            tag.incrementUsage();
            tagRepository.save(tag);
        }

        log.info("Content tagged successfully with {} tags", contentTags.size());
        return contentTagMapper.toDtoList(contentTags);
    }

    /**
     * Get tags for content
     */
    @Transactional(readOnly = true)
    public List<ContentTagDto> getContentTags(GetContentTagsQuery query) {
        log.info("Fetching tags for content: {} for tenant: {}", query.getContentId(), query.getTenantId());

        List<ContentTag> contentTags;
        if (query.getContentType() != null) {
            contentTags = contentTagRepository.findByTenantIdAndContentIdAndContentType(
                    query.getTenantId(), query.getContentId(), query.getContentType());
        } else {
            contentTags = contentTagRepository.findByTenantIdAndContentId(
                    query.getTenantId(), query.getContentId());
        }

        return contentTagMapper.toDtoList(contentTags);
    }

    /**
     * Remove tag from content
     */
    public void removeTagFromContent(String tenantId, String contentId, UUID tagId) {
        log.info("Removing tag: {} from content: {} for tenant: {}", tagId, contentId, tenantId);

        if (!contentTagRepository.existsByTenantIdAndContentIdAndTagId(tenantId, contentId, tagId)) {
            throw new IllegalArgumentException("Content tag not found");
        }

        contentTagRepository.deleteByTenantIdAndContentIdAndTagId(tenantId, contentId, tagId);

        // Decrement tag usage count
        Tag tag = tagRepository.findByTenantIdAndId(tenantId, tagId)
                .orElseThrow(() -> new IllegalArgumentException("Tag not found"));
        tag.decrementUsage();
        tagRepository.save(tag);

        log.info("Tag removed from content successfully");
    }

    // ============================================================
    // AI Tag Suggestion Operations
    // ============================================================

    /**
     * Generate AI tag suggestions for content
     * Note: This is a simplified implementation. In production, this would
     * integrate with an actual AI/ML service for intelligent tag suggestions.
     */
    public List<TagSuggestionDto> generateSuggestions(GenerateTagSuggestionsCommand command) {
        log.info("Generating tag suggestions for content: {} for tenant: {}",
                command.getContentId(), command.getTenantId());

        // In production, this would call an AI service to analyze content and suggest tags
        // For now, we'll return a simple implementation based on keyword matching

        List<Tag> allActiveTags = tagRepository.findByTenantIdAndStatus(
                command.getTenantId(), Tag.TagStatus.ACTIVE);

        List<TagSuggestion> suggestions = new ArrayList<>();
        String contentTextLower = command.getContentText().toLowerCase();
        int maxSuggestions = command.getMaxSuggestions() != null ? command.getMaxSuggestions() : 5;
        double minConfidence = command.getMinConfidenceThreshold() != null ?
                command.getMinConfidenceThreshold() : 0.5;

        for (Tag tag : allActiveTags) {
            // Simple keyword matching for demo purposes
            double confidence = calculateConfidence(contentTextLower, tag);

            if (confidence >= minConfidence) {
                TagSuggestion suggestion = TagSuggestion.builder()
                        .id(UUID.randomUUID())
                        .tenantId(command.getTenantId())
                        .contentId(command.getContentId())
                        .contentType(command.getContentType())
                        .suggestedTagId(tag.getId())
                        .confidenceScore(confidence)
                        .status(TagSuggestion.SuggestionStatus.PENDING)
                        .suggestionMetadata("{\"algorithm\":\"keyword-matching\"}")
                        .aiModelUsed(command.getAiModel() != null ? command.getAiModel() : "keyword-matcher")
                        .createdAt(LocalDateTime.now())
                        .expiresAt(LocalDateTime.now().plusHours(24))
                        .createdBy("system")
                        .version(1L)
                        .build();

                suggestions.add(suggestion);
            }

            if (suggestions.size() >= maxSuggestions) {
                break;
            }
        }

        // Sort by confidence score descending
        suggestions.sort((s1, s2) -> Double.compare(s2.getConfidenceScore(), s1.getConfidenceScore()));

        List<TagSuggestion> savedSuggestions = suggestions.stream()
                .map(suggestionRepository::save)
                .collect(Collectors.toList());

        log.info("Generated {} tag suggestions", savedSuggestions.size());
        return suggestionMapper.toDtoList(savedSuggestions);
    }

    /**
     * Accept a tag suggestion
     */
    public ContentTagDto acceptSuggestion(String tenantId, UUID suggestionId) {
        log.info("Accepting suggestion: {} for tenant: {}", suggestionId, tenantId);

        TagSuggestion suggestion = suggestionRepository.findByTenantIdAndId(tenantId, suggestionId)
                .orElseThrow(() -> new IllegalArgumentException("Suggestion not found"));

        if (!suggestion.isPending()) {
            throw new IllegalStateException("Suggestion is not pending");
        }

        // Create content tag from suggestion
        TagContentCommand tagCommand = TagContentCommand.builder()
                .tenantId(tenantId)
                .contentId(suggestion.getContentId())
                .contentType(suggestion.getContentType())
                .tagIds(Collections.singletonList(suggestion.getSuggestedTagId()))
                .taggingSource(ContentTag.TaggingSource.AUTOMATIC_AI)
                .confidenceThreshold(suggestion.getConfidenceScore())
                .taggedBy("ai-suggestion")
                .build();

        List<ContentTagDto> contentTags = tagContent(tagCommand);

        // Mark suggestion as accepted
        suggestion.accept();
        suggestionRepository.save(suggestion);

        log.info("Suggestion accepted successfully");
        return contentTags.isEmpty() ? null : contentTags.get(0);
    }

    /**
     * Reject a tag suggestion
     */
    public void rejectSuggestion(String tenantId, UUID suggestionId) {
        log.info("Rejecting suggestion: {} for tenant: {}", suggestionId, tenantId);

        TagSuggestion suggestion = suggestionRepository.findByTenantIdAndId(tenantId, suggestionId)
                .orElseThrow(() -> new IllegalArgumentException("Suggestion not found"));

        suggestion.reject();
        suggestionRepository.save(suggestion);

        log.info("Suggestion rejected successfully");
    }

    // ============================================================
    // Helper Methods
    // ============================================================

    /**
     * Calculate confidence score for a tag based on content
     * Simple keyword matching implementation for demo purposes
     */
    private double calculateConfidence(String contentText, Tag tag) {
        String tagName = tag.getName().toLowerCase();
        String tagDescription = tag.getDescription() != null ? tag.getDescription().toLowerCase() : "";

        // Exact tag name match
        if (contentText.contains(tagName)) {
            return 0.9;
        }

        // Partial tag name match
        String[] tagWords = tagName.split("\\s+");
        int matchCount = 0;
        for (String word : tagWords) {
            if (contentText.contains(word)) {
                matchCount++;
            }
        }

        if (matchCount > 0) {
            return 0.5 + (matchCount * 0.1);
        }

        // Description keyword match
        if (!tagDescription.isEmpty() && contentText.contains(tagDescription.substring(0, Math.min(20, tagDescription.length())))) {
            return 0.6;
        }

        return 0.0;
    }
}
