package com.gogidix.rapidassist.ai.moderation.infrastructure.persistence;

import com.gogidix.rapidassist.ai.moderation.domain.model.ModerationRule;
import com.gogidix.rapidassist.ai.moderation.infrastructure.persistence.document.ModerationRuleDocument;
import com.gogidix.rapidassist.ai.moderation.infrastructure.persistence.repository.SpringDataModerationRuleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ModerationRuleRepositoryImpl
 */
@ExtendWith(MockitoExtension.class)
class ModerationRuleRepositoryImplTest {

    @Mock
    private SpringDataModerationRuleRepository springRepository;

    @InjectMocks
    private ModerationRuleRepositoryImpl repository;

    private ModerationRule rule;
    private ModerationRuleDocument document;

    @BeforeEach
    void setUp() {
        rule = ModerationRule.builder()
            .id("rule1")
            .tenantId("tenant1")
            .name("Test Rule")
            .description("Test description")
            .ruleType(ModerationRule.RuleType.KEYWORD)
            .severity(ModerationRule.RuleSeverity.HIGH)
            .keywords(Arrays.asList("bad", "word"))
            .active(true)
            .priority(10)
            .createdBy("admin")
            .createdAt(LocalDateTime.now())
            .build();

        document = ModerationRuleDocument.builder()
            .id("rule1")
            .tenantId("tenant1")
            .name("Test Rule")
            .description("Test description")
            .ruleType(ModerationRule.RuleType.KEYWORD)
            .severity(ModerationRule.RuleSeverity.HIGH)
            .keywords(Arrays.asList("bad", "word"))
            .active(true)
            .priority(10)
            .createdBy("admin")
            .createdAt(LocalDateTime.now())
            .build();
    }

    @Test
    void testSave() {
        when(springRepository.save(any(ModerationRuleDocument.class))).thenReturn(document);

        ModerationRule result = repository.save(rule);

        assertNotNull(result);
        assertEquals("rule1", result.getId());
        assertEquals("Test Rule", result.getName());
        verify(springRepository).save(any());
    }

    @Test
    void testFindById() {
        when(springRepository.findById("rule1")).thenReturn(Optional.of(document));

        Optional<ModerationRule> result = repository.findById("rule1");

        assertTrue(result.isPresent());
        assertEquals("rule1", result.get().getId());
    }

    @Test
    void testFindById_NotFound() {
        when(springRepository.findById("nonexistent")).thenReturn(Optional.empty());

        Optional<ModerationRule> result = repository.findById("nonexistent");

        assertFalse(result.isPresent());
    }

    @Test
    void testFindActiveByTenantId() {
        when(springRepository.findByTenantIdAndActiveTrue("tenant1"))
            .thenReturn(Arrays.asList(document));

        List<ModerationRule> results = repository.findActiveByTenantId("tenant1");

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Test Rule", results.get(0).getName());
    }

    @Test
    void testFindByTenantId() {
        when(springRepository.findByTenantId("tenant1")).thenReturn(Arrays.asList(document));

        List<ModerationRule> results = repository.findByTenantId("tenant1");

        assertNotNull(results);
        assertEquals(1, results.size());
    }

    @Test
    void testFindByTenantIdAndType() {
        when(springRepository.findByTenantIdAndRuleType("tenant1", ModerationRule.RuleType.KEYWORD))
            .thenReturn(Arrays.asList(document));

        List<ModerationRule> results = repository.findByTenantIdAndType("tenant1", ModerationRule.RuleType.KEYWORD);

        assertNotNull(results);
        assertEquals(1, results.size());
    }

    @Test
    void testDeleteById() {
        repository.deleteById("rule1");
        verify(springRepository).deleteById("rule1");
    }

    @Test
    void testExistsByTenantIdAndName() {
        when(springRepository.existsByTenantIdAndName("tenant1", "Test Rule")).thenReturn(true);

        boolean exists = repository.existsByTenantIdAndName("tenant1", "Test Rule");

        assertTrue(exists);
    }

    @Test
    void testNotExistsByTenantIdAndName() {
        when(springRepository.existsByTenantIdAndName("tenant1", "New Rule")).thenReturn(false);

        boolean exists = repository.existsByTenantIdAndName("tenant1", "New Rule");

        assertFalse(exists);
    }
}
