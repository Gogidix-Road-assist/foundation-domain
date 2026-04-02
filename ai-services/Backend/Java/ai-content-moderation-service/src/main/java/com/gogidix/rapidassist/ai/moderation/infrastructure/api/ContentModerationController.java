package com.gogidix.rapidassist.ai.moderation.infrastructure.api;

import com.gogidix.rapidassist.ai.moderation.application.command.ModerateContentCommand;
import com.gogidix.rapidassist.ai.moderation.application.command.ModerationRuleCommand;
import com.gogidix.rapidassist.ai.moderation.application.command.QueueActionCommand;
import com.gogidix.rapidassist.ai.moderation.application.dto.ModerationQueueDto;
import com.gogidix.rapidassist.ai.moderation.application.dto.ModerationResultDto;
import com.gogidix.rapidassist.ai.moderation.application.dto.ModerationRuleDto;
import com.gogidix.rapidassist.ai.moderation.application.port.in.ContentModerationUseCase;
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

/**
 * REST controller for content moderation operations
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/moderation")
@RequiredArgsConstructor
@Tag(name = "Content Moderation", description = "APIs for content moderation and policy compliance")
public class ContentModerationController {

    private final ContentModerationUseCase contentModerationUseCase;

    @PostMapping("/moderate")
    @Operation(summary = "Moderate content", description = "Submit content for automated moderation analysis")
    public ResponseEntity<ModerationResultDto> moderateContent(
        @Parameter(description = "Tenant ID", required = true) @RequestHeader("X-Tenant-ID") String tenantId,
        @Valid @RequestBody ModerateContentCommand command
    ) {
        log.info("Received moderation request for content {} from tenant {}", command.getContentId(), tenantId);
        command.setTenantId(tenantId);
        ModerationResultDto result = contentModerationUseCase.moderateContent(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/results/{id}")
    @Operation(summary = "Get moderation result", description = "Retrieve moderation result by ID")
    public ResponseEntity<ModerationResultDto> getModerationResult(
        @Parameter(description = "Result ID", required = true) @PathVariable String id
    ) {
        log.info("Fetching moderation result {}", id);
        ModerationResultDto result = contentModerationUseCase.getModerationResult(id);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/results")
    @Operation(summary = "List moderation results", description = "Get all moderation results with pagination")
    public ResponseEntity<List<ModerationResultDto>> getModerationResults(
        @Parameter(description = "Tenant ID", required = true) @RequestHeader("X-Tenant-ID") String tenantId,
        @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size
    ) {
        log.info("Fetching moderation results for tenant {}, page {}, size {}", tenantId, page, size);
        List<ModerationResultDto> results = contentModerationUseCase.getModerationResults(tenantId, page, size);
        return ResponseEntity.ok(results);
    }

    @PostMapping("/rules")
    @Operation(summary = "Create moderation rule", description = "Create a new content moderation rule")
    public ResponseEntity<ModerationRuleDto> createRule(
        @Parameter(description = "Tenant ID", required = true) @RequestHeader("X-Tenant-ID") String tenantId,
        @Valid @RequestBody ModerationRuleCommand command
    ) {
        log.info("Creating moderation rule {} for tenant {}", command.getName(), tenantId);
        command.setTenantId(tenantId);
        ModerationRuleDto rule = contentModerationUseCase.createRule(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(rule);
    }

    @PutMapping("/rules/{id}")
    @Operation(summary = "Update moderation rule", description = "Update an existing moderation rule")
    public ResponseEntity<ModerationRuleDto> updateRule(
        @Parameter(description = "Rule ID", required = true) @PathVariable String id,
        @Parameter(description = "Tenant ID", required = true) @RequestHeader("X-Tenant-ID") String tenantId,
        @Valid @RequestBody ModerationRuleCommand command
    ) {
        log.info("Updating moderation rule {}", id);
        command.setTenantId(tenantId);
        ModerationRuleDto rule = contentModerationUseCase.updateRule(id, command);
        return ResponseEntity.ok(rule);
    }

    @DeleteMapping("/rules/{id}")
    @Operation(summary = "Delete moderation rule", description = "Delete a moderation rule")
    public ResponseEntity<Void> deleteRule(
        @Parameter(description = "Rule ID", required = true) @PathVariable String id
    ) {
        log.info("Deleting moderation rule {}", id);
        contentModerationUseCase.deleteRule(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/rules")
    @Operation(summary = "List moderation rules", description = "Get all moderation rules for a tenant")
    public ResponseEntity<List<ModerationRuleDto>> getRules(
        @Parameter(description = "Tenant ID", required = true) @RequestHeader("X-Tenant-ID") String tenantId
    ) {
        log.info("Fetching moderation rules for tenant {}", tenantId);
        List<ModerationRuleDto> rules = contentModerationUseCase.getRules(tenantId);
        return ResponseEntity.ok(rules);
    }

    @PostMapping("/queue/{id}/approve")
    @Operation(summary = "Approve queued content", description = "Approve content pending moderation review")
    public ResponseEntity<Void> approveQueueItem(
        @Parameter(description = "Queue item ID", required = true) @PathVariable String id,
        @Valid @RequestBody QueueActionCommand command
    ) {
        log.info("Approving queue item {}", id);
        contentModerationUseCase.approveQueueItem(id, command);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/queue/{id}/reject")
    @Operation(summary = "Reject queued content", description = "Reject content pending moderation review")
    public ResponseEntity<Void> rejectQueueItem(
        @Parameter(description = "Queue item ID", required = true) @PathVariable String id,
        @Valid @RequestBody QueueActionCommand command
    ) {
        log.info("Rejecting queue item {}", id);
        contentModerationUseCase.rejectQueueItem(id, command);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/queue")
    @Operation(summary = "List pending queue items", description = "Get all items pending moderation review")
    public ResponseEntity<List<ModerationQueueDto>> getQueueItems(
        @Parameter(description = "Tenant ID", required = true) @RequestHeader("X-Tenant-ID") String tenantId
    ) {
        log.info("Fetching queue items for tenant {}", tenantId);
        List<ModerationQueueDto> items = contentModerationUseCase.getQueueItems(tenantId);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Check if the service is running")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("AI Content Moderation Service is running");
    }
}
