package com.gogidix.rapidassist.ai.tagging.interfaces.rest.controller;

import com.gogidix.rapidassist.ai.tagging.application.command.*;
import com.gogidix.rapidassist.ai.tagging.application.dto.TagDto;
import com.gogidix.rapidassist.ai.tagging.application.dto.TagCategoryDto;
import com.gogidix.rapidassist.ai.tagging.application.dto.TagSuggestionDto;
import com.gogidix.rapidassist.ai.tagging.application.dto.TaggingRuleDto;
import com.gogidix.rapidassist.ai.tagging.application.dto.ContentTagDto;
import com.gogidix.rapidassist.ai.tagging.application.query.GetContentTagsQuery;
import com.gogidix.rapidassist.ai.tagging.application.query.GetTagQuery;
import com.gogidix.rapidassist.ai.tagging.application.query.ListTagsQuery;
import com.gogidix.rapidassist.ai.tagging.application.service.TaggingApplicationService;
import com.gogidix.rapidassist.ai.tagging.domain.model.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for AI Automated Tagging Service
 * Provides endpoints for tag management and content tagging operations
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/tagging")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "AI Automated Tagging", description = "APIs for automated content tagging and tag management")
public class TaggingController {

    private final TaggingApplicationService taggingApplicationService;

    // ============================================================
    // Tag Management Endpoints
    // ============================================================

    @PostMapping("/tags")
    @Operation(summary = "Create a new tag", description = "Create a new tag with the provided details")
    public ResponseEntity<TagDto> createTag(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Valid @RequestBody CreateTagCommand command) {
        log.info("POST /api/v1/tagging/tags - Tenant: {}, Name: {}", tenantId, command.getName());
        command.setTenantId(tenantId);
        TagDto createdTag = taggingApplicationService.createTag(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTag);
    }

    @GetMapping("/tags/{id}")
    @Operation(summary = "Get tag by ID", description = "Retrieve a specific tag by its ID")
    public ResponseEntity<TagDto> getTag(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Tag ID") @PathVariable UUID id) {
        log.info("GET /api/v1/tagging/tags/{} - Tenant: {}", id, tenantId);
        GetTagQuery query = GetTagQuery.builder()
                .tenantId(tenantId)
                .tagId(id)
                .build();
        TagDto tag = taggingApplicationService.getTag(query);
        return ResponseEntity.ok(tag);
    }

    @GetMapping("/tags")
    @Operation(summary = "List all tags", description = "Retrieve all tags for the tenant with optional filters")
    public ResponseEntity<List<TagDto>> listTags(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Tag status filter") @RequestParam(required = false) String status,
            @Parameter(description = "Category ID filter") @RequestParam(required = false) UUID categoryId,
            @Parameter(description = "Search keyword") @RequestParam(required = false) String searchKeyword,
            @Parameter(description = "Page number") @RequestParam(required = false) Integer page,
            @Parameter(description = "Page size") @RequestParam(required = false) Integer size) {
        log.info("GET /api/v1/tagging/tags - Tenant: {}, Filters: status={}, categoryId={}, search={}",
                tenantId, status, categoryId, searchKeyword);

        ListTagsQuery query = ListTagsQuery.builder()
                .tenantId(tenantId)
                .status(status != null && !status.isEmpty() ? Tag.TagStatus.valueOf(status.toUpperCase()) : null)
                .categoryId(categoryId)
                .searchKeyword(searchKeyword)
                .page(page)
                .size(size)
                .build();

        List<TagDto> tags = taggingApplicationService.listTags(query);
        return ResponseEntity.ok(tags);
    }

    @PutMapping("/tags/{id}")
    @Operation(summary = "Update tag", description = "Update an existing tag with new details")
    public ResponseEntity<TagDto> updateTag(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Tag ID") @PathVariable UUID id,
            @Valid @RequestBody UpdateTagCommand command) {
        log.info("PUT /api/v1/tagging/tags/{} - Tenant: {}", id, tenantId);
        command.setTagId(id);
        command.setTenantId(tenantId);
        TagDto updatedTag = taggingApplicationService.updateTag(command);
        return ResponseEntity.ok(updatedTag);
    }

    @DeleteMapping("/tags/{id}")
    @Operation(summary = "Delete tag", description = "Delete a tag by its ID")
    public ResponseEntity<Void> deleteTag(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Tag ID") @PathVariable UUID id) {
        log.info("DELETE /api/v1/tagging/tags/{} - Tenant: {}", id, tenantId);
        taggingApplicationService.deleteTag(tenantId, id);
        return ResponseEntity.noContent().build();
    }

    // ============================================================
    // Tag Category Endpoints
    // ============================================================

    @GetMapping("/categories")
    @Operation(summary = "List all tag categories", description = "Retrieve all tag categories for the tenant")
    public ResponseEntity<List<TagCategoryDto>> listCategories(
            @RequestHeader("X-Tenant-ID") String tenantId) {
        log.info("GET /api/v1/tagging/categories - Tenant: {}", tenantId);
        List<TagCategoryDto> categories = taggingApplicationService.listCategories(tenantId);
        return ResponseEntity.ok(categories);
    }

    @PostMapping("/categories")
    @Operation(summary = "Create a new tag category", description = "Create a new tag category with the provided details")
    public ResponseEntity<TagCategoryDto> createCategory(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Valid @RequestBody CreateTagCategoryCommand command) {
        log.info("POST /api/v1/tagging/categories - Tenant: {}, Name: {}", tenantId, command.getName());
        command.setTenantId(tenantId);
        TagCategoryDto createdCategory = taggingApplicationService.createCategory(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
    }

    // ============================================================
    // Content Tagging Endpoints
    // ============================================================

    @PostMapping("/tag-content")
    @Operation(summary = "Tag content", description = "Apply tags to content automatically or manually")
    public ResponseEntity<List<ContentTagDto>> tagContent(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Valid @RequestBody TagContentCommand command) {
        log.info("POST /api/v1/tagging/tag-content - Tenant: {}, Content: {}, Tags: {}",
                tenantId, command.getContentId(), command.getTagIds().size());
        command.setTenantId(tenantId);
        List<ContentTagDto> contentTags = taggingApplicationService.tagContent(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(contentTags);
    }

    @GetMapping("/content/{contentId}/tags")
    @Operation(summary = "Get tags for content", description = "Retrieve all tags applied to a specific content")
    public ResponseEntity<List<ContentTagDto>> getContentTags(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Content ID") @PathVariable String contentId,
            @Parameter(description = "Content type filter") @RequestParam(required = false) String contentType) {
        log.info("GET /api/v1/tagging/content/{}/tags - Tenant: {}, Type: {}", contentId, tenantId, contentType);

        GetContentTagsQuery query = GetContentTagsQuery.builder()
                .tenantId(tenantId)
                .contentId(contentId)
                .contentType(contentType)
                .build();

        List<ContentTagDto> contentTags = taggingApplicationService.getContentTags(query);
        return ResponseEntity.ok(contentTags);
    }

    @DeleteMapping("/content/{contentId}/tags/{tagId}")
    @Operation(summary = "Remove tag from content", description = "Remove a specific tag from content")
    public ResponseEntity<Void> removeTagFromContent(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Content ID") @PathVariable String contentId,
            @Parameter(description = "Tag ID") @PathVariable UUID tagId) {
        log.info("DELETE /api/v1/tagging/content/{}/tags/{} - Tenant: {}", contentId, tagId, tenantId);
        taggingApplicationService.removeTagFromContent(tenantId, contentId, tagId);
        return ResponseEntity.noContent().build();
    }

    // ============================================================
    // AI Tag Suggestion Endpoints
    // ============================================================

    @PostMapping("/suggestions")
    @Operation(summary = "Generate AI tag suggestions", description = "Generate AI-powered tag suggestions for content")
    public ResponseEntity<List<TagSuggestionDto>> generateSuggestions(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Valid @RequestBody GenerateTagSuggestionsCommand command) {
        log.info("POST /api/v1/tagging/suggestions - Tenant: {}, Content: {}",
                tenantId, command.getContentId());
        command.setTenantId(tenantId);
        List<TagSuggestionDto> suggestions = taggingApplicationService.generateSuggestions(command);
        return ResponseEntity.ok(suggestions);
    }

    @PostMapping("/suggestions/{suggestionId}/accept")
    @Operation(summary = "Accept tag suggestion", description = "Accept a tag suggestion and apply it to content")
    public ResponseEntity<ContentTagDto> acceptSuggestion(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Suggestion ID") @PathVariable UUID suggestionId) {
        log.info("POST /api/v1/tagging/suggestions/{}/accept - Tenant: {}", suggestionId, tenantId);
        ContentTagDto contentTag = taggingApplicationService.acceptSuggestion(tenantId, suggestionId);
        return ResponseEntity.ok(contentTag);
    }

    @PostMapping("/suggestions/{suggestionId}/reject")
    @Operation(summary = "Reject tag suggestion", description = "Reject a tag suggestion")
    public ResponseEntity<Void> rejectSuggestion(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Suggestion ID") @PathVariable UUID suggestionId) {
        log.info("POST /api/v1/tagging/suggestions/{}/reject - Tenant: {}", suggestionId, tenantId);
        taggingApplicationService.rejectSuggestion(tenantId, suggestionId);
        return ResponseEntity.noContent().build();
    }

    // ============================================================
    // Health Check Endpoint
    // ============================================================

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Check if the service is running")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("AI Automated Tagging Service is running");
    }
}
