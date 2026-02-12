package com.gogidix.rapidassist.ai.moderation.application.service;

import com.gogidix.rapidassist.ai.moderation.application.command.ModerateContentCommand;
import com.gogidix.rapidassist.ai.moderation.application.command.ModerationRuleCommand;
import com.gogidix.rapidassist.ai.moderation.application.dto.ModerationResultDto;
import com.gogidix.rapidassist.ai.moderation.application.dto.ModerationRuleDto;
import com.gogidix.rapidassist.ai.moderation.application.port.out.ModerationQueueRepositoryPort;
import com.gogidix.rapidassist.ai.moderation.application.port.out.ModerationResultRepositoryPort;
import com.gogidix.rapidassist.ai.moderation.application.port.out.ModerationRuleRepositoryPort;
import com.gogidix.rapidassist.ai.moderation.domain.model.ModerationResult;
import com.gogidix.rapidassist.ai.moderation.domain.model.ModerationRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ContentModerationApplicationService
 */
@ExtendWith(MockitoExtension.class)
class ContentModerationApplicationServiceTest {

    @Mock
    private ModerationRuleRepositoryPort ruleRepository;

    @Mock
    private ModerationResultRepositoryPort resultRepository;

    @Mock
    private ModerationQueueRepositoryPort queueRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private ContentModerationApplicationService service;

    private ModerateContentCommand moderateCommand;
    private ModerationRuleCommand ruleCommand;

    @BeforeEach
    void setUp() {
        moderateCommand = ModerateContentCommand.builder()
            .tenantId("tenant1")
            .contentId("content1")
            .content("Test content")
            .contentType("comment")
            .userId("user1")
            .build();

        ruleCommand = ModerationRuleCommand.builder()
            .tenantId("tenant1")
            .name("Test Rule")
            .description("Test description")
            .ruleType(ModerationRule.RuleType.KEYWORD)
            .severity(ModerationRule.RuleSeverity.MEDIUM)
            .keywords(Arrays.asList("bad", "word"))
            .active(true)
            .priority(10)
            .createdBy("admin")
            .build();
    }

    @Test
    void testModerateContent_NoViolations() {
        when(ruleRepository.findActiveByTenantId("tenant1")).thenReturn(Arrays.asList());
        when(resultRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ModerationResultDto result = service.moderateContent(moderateCommand);

        assertNotNull(result);
        assertEquals(ModerationResult.ModerationStatus.AUTO_APPROVED, result.getStatus());
        assertEquals(1.0, result.getConfidenceScore());
        verify(resultRepository).save(any());
        verify(eventPublisher).publishEvent(any());
    }

    @Test
    void testModerateContent_WithViolations() {
        ModerationRule rule = ModerationRule.builder()
            .id("rule1")
            .name("Profanity Filter")
            .ruleType(ModerationRule.RuleType.KEYWORD)
            .severity(ModerationRule.RuleSeverity.HIGH)
            .keywords(Arrays.asList("bad"))
            .active(true)
            .build();

        when(ruleRepository.findActiveByTenantId("tenant1")).thenReturn(Arrays.asList(rule));
        when(resultRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ModerateContentCommand command = ModerateContentCommand.builder()
            .tenantId("tenant1")
            .contentId("content1")
            .content("This has bad words")
            .build();

        ModerationResultDto result = service.moderateContent(command);

        assertNotNull(result);
        assertEquals(ModerationResult.ModerationStatus.AUTO_REJECTED, result.getStatus());
        assertTrue(result.getConfidenceScore() >= 0.8);
        assertFalse(result.getViolations().isEmpty());
    }

    @Test
    void testCreateRule() {
        when(ruleRepository.existsByTenantIdAndName("tenant1", "Test Rule")).thenReturn(false);
        when(ruleRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ModerationRuleDto result = service.createRule(ruleCommand);

        assertNotNull(result);
        assertEquals("Test Rule", result.getName());
        assertEquals(ModerationRule.RuleType.KEYWORD, result.getRuleType());
        verify(ruleRepository).save(any());
        verify(eventPublisher).publishEvent(any());
    }

    @Test
    void testCreateRule_DuplicateName() {
        when(ruleRepository.existsByTenantIdAndName("tenant1", "Test Rule")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            service.createRule(ruleCommand);
        });
    }

    @Test
    void testGetRules() {
        ModerationRule rule1 = ModerationRule.builder()
            .id("rule1")
            .name("Rule 1")
            .build();

        ModerationRule rule2 = ModerationRule.builder()
            .id("rule2")
            .name("Rule 2")
            .build();

        when(ruleRepository.findByTenantId("tenant1")).thenReturn(Arrays.asList(rule1, rule2));

        List<ModerationRuleDto> rules = service.getRules("tenant1");

        assertNotNull(rules);
        assertEquals(2, rules.size());
    }

    @Test
    void testGetModerationResult() {
        ModerationResult result = ModerationResult.builder()
            .id("result1")
            .contentId("content1")
            .status(ModerationResult.ModerationStatus.APPROVED)
            .build();

        when(resultRepository.findById("result1")).thenReturn(Optional.of(result));

        ModerationResultDto dto = service.getModerationResult("result1");

        assertNotNull(dto);
        assertEquals("result1", dto.getId());
    }

    @Test
    void testGetModerationResult_NotFound() {
        when(resultRepository.findById("nonexistent")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            service.getModerationResult("nonexistent");
        });
    }

    @Test
    void testDeleteRule() {
        service.deleteRule("rule1");
        verify(ruleRepository).deleteById("rule1");
    }

    @Test
    void testUpdateRule() {
        ModerationRule existingRule = ModerationRule.builder()
            .id("rule1")
            .name("Old Name")
            .build();

        when(ruleRepository.findById("rule1")).thenReturn(Optional.of(existingRule));
        when(ruleRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ModerationRuleDto result = service.updateRule("rule1", ruleCommand);

        assertNotNull(result);
        verify(ruleRepository).save(any());
    }

    @Test
    void testUpdateRule_NotFound() {
        when(ruleRepository.findById("nonexistent")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            service.updateRule("nonexistent", ruleCommand);
        });
    }

    @Test
    void testModerateContent_FlaggedForReview() {
        ModerationRule rule = ModerationRule.builder()
            .id("rule1")
            .name("Medium Severity Rule")
            .ruleType(ModerationRule.RuleType.KEYWORD)
            .severity(ModerationRule.RuleSeverity.MEDIUM)
            .keywords(Arrays.asList("maybe"))
            .active(true)
            .build();

        when(ruleRepository.findActiveByTenantId("tenant1")).thenReturn(Arrays.asList(rule));
        when(resultRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ModerateContentCommand command = ModerateContentCommand.builder()
            .tenantId("tenant1")
            .contentId("content1")
            .content("This has maybe bad words")
            .build();

        ModerationResultDto result = service.moderateContent(command);

        assertNotNull(result);
        assertEquals(ModerationResult.ModerationStatus.FLAGGED, result.getStatus());
        assertTrue(result.getConfidenceScore() < 0.8);
        verify(queueRepository).save(any());
        verify(eventPublisher, times(2)).publishEvent(any());
    }

    @Test
    void testApproveQueueItem() {
        com.gogidix.rapidassist.ai.moderation.domain.model.ModerationQueue queue =
            com.gogidix.rapidassist.ai.moderation.domain.model.ModerationQueue.builder()
                .id("queue1")
                .contentId("content1")
                .build();

        ModerationResult moderationResult = ModerationResult.builder()
            .id("result1")
            .contentId("content1")
            .status(ModerationResult.ModerationStatus.FLAGGED)
            .build();

        when(queueRepository.findById("queue1")).thenReturn(Optional.of(queue));
        when(resultRepository.findByContentId("content1")).thenReturn(Optional.of(moderationResult));
        when(queueRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(resultRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        com.gogidix.rapidassist.ai.moderation.application.command.QueueActionCommand command =
            com.gogidix.rapidassist.ai.moderation.application.command.QueueActionCommand.builder()
                .reviewerId("reviewer1")
                .notes("Approved")
                .build();

        service.approveQueueItem("queue1", command);

        assertEquals(ModerationResult.ModerationStatus.APPROVED, moderationResult.getStatus());
        verify(queueRepository).save(any());
        verify(resultRepository).save(any());
    }

    @Test
    void testRejectQueueItem() {
        com.gogidix.rapidassist.ai.moderation.domain.model.ModerationQueue queue =
            com.gogidix.rapidassist.ai.moderation.domain.model.ModerationQueue.builder()
                .id("queue1")
                .contentId("content1")
                .build();

        ModerationResult moderationResult = ModerationResult.builder()
            .id("result1")
            .contentId("content1")
            .status(ModerationResult.ModerationStatus.FLAGGED)
            .build();

        when(queueRepository.findById("queue1")).thenReturn(Optional.of(queue));
        when(resultRepository.findByContentId("content1")).thenReturn(Optional.of(moderationResult));
        when(queueRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(resultRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        com.gogidix.rapidassist.ai.moderation.application.command.QueueActionCommand command =
            com.gogidix.rapidassist.ai.moderation.application.command.QueueActionCommand.builder()
                .reviewerId("reviewer1")
                .notes("Rejected")
                .build();

        service.rejectQueueItem("queue1", command);

        assertEquals(ModerationResult.ModerationStatus.REJECTED, moderationResult.getStatus());
        verify(queueRepository).save(any());
        verify(resultRepository).save(any());
    }

    @Test
    void testGetModerationResults() {
        ModerationResult result1 = ModerationResult.builder()
            .id("result1")
            .contentId("content1")
            .status(ModerationResult.ModerationStatus.APPROVED)
            .createdAt(java.time.LocalDateTime.now())
            .build();

        ModerationResult result2 = ModerationResult.builder()
            .id("result2")
            .contentId("content2")
            .status(ModerationResult.ModerationStatus.REJECTED)
            .createdAt(java.time.LocalDateTime.now().plusHours(1))
            .build();

        when(resultRepository.findByTenantId("tenant1"))
            .thenReturn(Arrays.asList(result1, result2));

        java.util.List<ModerationResultDto> results = service.getModerationResults("tenant1", 0, 10);

        assertNotNull(results);
        assertEquals(2, results.size());
    }

    @Test
    void testGetQueueItems() {
        com.gogidix.rapidassist.ai.moderation.domain.model.ModerationQueue queue1 =
            com.gogidix.rapidassist.ai.moderation.domain.model.ModerationQueue.builder()
                .id("queue1")
                .contentId("content1")
                .status(com.gogidix.rapidassist.ai.moderation.domain.model.ModerationQueue.QueueStatus.PENDING)
                .build();

        when(queueRepository.findPendingByTenantId("tenant1"))
            .thenReturn(Arrays.asList(queue1));

        java.util.List<com.gogidix.rapidassist.ai.moderation.application.dto.ModerationQueueDto> queues =
            service.getQueueItems("tenant1");

        assertNotNull(queues);
        assertEquals(1, queues.size());
    }

    @Test
    void testModerateContentWithMultipleRules() {
        ModerationRule rule1 = ModerationRule.builder()
            .id("rule1")
            .name("High Severity Rule")
            .ruleType(ModerationRule.RuleType.KEYWORD)
            .severity(ModerationRule.RuleSeverity.HIGH)
            .keywords(Arrays.asList("bad"))
            .active(true)
            .build();

        ModerationRule rule2 = ModerationRule.builder()
            .id("rule2")
            .name("Critical Severity Rule")
            .ruleType(ModerationRule.RuleType.KEYWORD)
            .severity(ModerationRule.RuleSeverity.CRITICAL)
            .keywords(Arrays.asList("worst"))
            .active(true)
            .build();

        when(ruleRepository.findActiveByTenantId("tenant1")).thenReturn(Arrays.asList(rule1, rule2));
        when(resultRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ModerateContentCommand command = ModerateContentCommand.builder()
            .tenantId("tenant1")
            .contentId("content1")
            .content("This has bad and worst words")
            .build();

        ModerationResultDto result = service.moderateContent(command);

        assertNotNull(result);
        assertEquals(ModerationResult.ModerationStatus.AUTO_REJECTED, result.getStatus());
        assertTrue(result.getConfidenceScore() >= 0.8);
        assertEquals(2, result.getViolations().size());
    }

    @Test
    void testCreateRuleWithAllFields() {
        ModerationRuleCommand fullRuleCommand = ModerationRuleCommand.builder()
            .tenantId("tenant1")
            .name("Complete Rule")
            .description("Complete description")
            .ruleType(ModerationRule.RuleType.PATTERN)
            .severity(ModerationRule.RuleSeverity.CRITICAL)
            .keywords(Arrays.asList("keyword1", "keyword2"))
            .patterns(Arrays.asList("pattern1", "pattern2"))
            .metadata(java.util.Map.of("key", "value"))
            .active(true)
            .priority(100)
            .createdBy("admin")
            .build();

        when(ruleRepository.existsByTenantIdAndName("tenant1", "Complete Rule")).thenReturn(false);
        when(ruleRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ModerationRuleDto result = service.createRule(fullRuleCommand);

        assertNotNull(result);
        assertEquals("Complete Rule", result.getName());
        assertEquals(ModerationRule.RuleType.PATTERN, result.getRuleType());
        assertEquals(ModerationRule.RuleSeverity.CRITICAL, result.getSeverity());
        assertEquals(100, result.getPriority());
        verify(ruleRepository).save(any());
        verify(eventPublisher).publishEvent(any());
    }
}
