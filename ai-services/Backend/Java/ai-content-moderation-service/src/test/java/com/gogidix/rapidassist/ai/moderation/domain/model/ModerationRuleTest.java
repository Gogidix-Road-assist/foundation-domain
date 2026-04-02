package com.gogidix.rapidassist.ai.moderation.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ModerationRule domain model
 */
class ModerationRuleTest {

    @Test
    void testCreateModerationRule() {
        ModerationRule rule = ModerationRule.builder()
            .id("rule1")
            .tenantId("tenant1")
            .name("Profanity Filter")
            .description("Blocks profanity")
            .ruleType(ModerationRule.RuleType.KEYWORD)
            .severity(ModerationRule.RuleSeverity.HIGH)
            .keywords(List.of("badword", "profanity"))
            .active(true)
            .priority(10)
            .createdBy("admin")
            .createdAt(LocalDateTime.now())
            .build();

        assertNotNull(rule);
        assertEquals("rule1", rule.getId());
        assertEquals("Profanity Filter", rule.getName());
        assertEquals(ModerationRule.RuleType.KEYWORD, rule.getRuleType());
        assertEquals(ModerationRule.RuleSeverity.HIGH, rule.getSeverity());
        assertTrue(rule.isActive());
        assertEquals(2, rule.getKeywords().size());
    }

    @Test
    void testActivateRule() {
        ModerationRule rule = ModerationRule.builder()
            .active(false)
            .build();

        rule.activate();

        assertTrue(rule.isActive());
        assertNotNull(rule.getUpdatedAt());
    }

    @Test
    void testDeactivateRule() {
        ModerationRule rule = ModerationRule.builder()
            .active(true)
            .build();

        rule.deactivate();

        assertFalse(rule.isActive());
        assertNotNull(rule.getUpdatedAt());
    }

    @Test
    void testMatchesKeyword() {
        ModerationRule rule = ModerationRule.builder()
            .ruleType(ModerationRule.RuleType.KEYWORD)
            .keywords(List.of("badword", "profanity"))
            .active(true)
            .build();

        assertTrue(rule.matches("This contains badword"));
        assertTrue(rule.matches("PROFANITY in caps"));
        assertFalse(rule.matches("Clean content"));
    }

    @Test
    void testInactiveRuleDoesNotMatch() {
        ModerationRule rule = ModerationRule.builder()
            .ruleType(ModerationRule.RuleType.KEYWORD)
            .keywords(List.of("badword"))
            .active(false)
            .build();

        assertFalse(rule.matches("This contains badword"));
    }

    @Test
    void testNonKeywordRuleDoesNotMatch() {
        ModerationRule rule = ModerationRule.builder()
            .ruleType(ModerationRule.RuleType.PATTERN)
            .keywords(List.of("test"))
            .active(true)
            .build();

        assertFalse(rule.matches("This contains test"));
    }
}
