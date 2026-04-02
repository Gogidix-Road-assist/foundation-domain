package com.gogidix.rapidassist.ai.moderation.application.port.in;

import com.gogidix.rapidassist.ai.moderation.application.command.ModerateContentCommand;
import com.gogidix.rapidassist.ai.moderation.application.command.ModerationRuleCommand;
import com.gogidix.rapidassist.ai.moderation.application.command.QueueActionCommand;
import com.gogidix.rapidassist.ai.moderation.application.dto.ModerationResultDto;
import com.gogidix.rapidassist.ai.moderation.application.dto.ModerationRuleDto;
import com.gogidix.rapidassist.ai.moderation.application.dto.ModerationQueueDto;

import java.util.List;

/**
 * Use case port for content moderation operations.
 * Defines the primary operations for content moderation.
 */
public interface ContentModerationUseCase {

    /**
     * Moderate content using configured rules
     */
    ModerationResultDto moderateContent(ModerateContentCommand command);

    /**
     * Get moderation result by ID
     */
    ModerationResultDto getModerationResult(String id);

    /**
     * Get all moderation results for a tenant
     */
    List<ModerationResultDto> getModerationResults(String tenantId, int page, int size);

    /**
     * Create a new moderation rule
     */
    ModerationRuleDto createRule(ModerationRuleCommand command);

    /**
     * Update an existing moderation rule
     */
    ModerationRuleDto updateRule(String id, ModerationRuleCommand command);

    /**
     * Delete a moderation rule
     */
    void deleteRule(String id);

    /**
     * Get all moderation rules for a tenant
     */
    List<ModerationRuleDto> getRules(String tenantId);

    /**
     * Approve content in queue
     */
    void approveQueueItem(String id, QueueActionCommand command);

    /**
     * Reject content in queue
     */
    void rejectQueueItem(String id, QueueActionCommand command);

    /**
     * Get all pending items in queue
     */
    List<ModerationQueueDto> getQueueItems(String tenantId);
}
