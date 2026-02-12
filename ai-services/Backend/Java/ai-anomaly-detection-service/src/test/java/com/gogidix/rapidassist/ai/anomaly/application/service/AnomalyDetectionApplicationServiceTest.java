package com.gogidix.rapidassist.ai.anomaly.application.service;

import com.gogidix.rapidassist.ai.anomaly.application.command.CreateDetectionRuleCommand;
import com.gogidix.rapidassist.ai.anomaly.application.dto.AnomalyDetectionDto;
import com.gogidix.rapidassist.ai.anomaly.application.dto.DetectionRuleDto;
import com.gogidix.rapidassist.ai.anomaly.domain.model.*;
import com.gogidix.rapidassist.ai.anomaly.domain.repository.AnomalyAlertRepositoryPort;
import com.gogidix.rapidassist.ai.anomaly.domain.repository.AnomalyDetectionRepositoryPort;
import com.gogidix.rapidassist.ai.anomaly.domain.repository.DetectionRuleRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AnomalyDetectionApplicationService.
 */
@ExtendWith(MockitoExtension.class)
class AnomalyDetectionApplicationServiceTest {

    @Mock
    private AnomalyDetectionRepositoryPort detectionRepository;

    @Mock
    private DetectionRuleRepositoryPort ruleRepository;

    @Mock
    private AnomalyAlertRepositoryPort alertRepository;

    @InjectMocks
    private AnomalyDetectionApplicationService service;

    private String tenantId;
    private UUID ruleId;
    private DetectionRule testRule;

    @BeforeEach
    void setUp() {
        tenantId = "tenant-1";
        ruleId = UUID.randomUUID();

        Map<String, Object> conditions = new HashMap<>();
        conditions.put("threshold", 100);

        testRule = DetectionRule.builder()
                .id(ruleId)
                .tenantId(tenantId)
                .name("Test Rule")
                .ruleType(RuleType.THRESHOLD)
                .conditions(conditions)
                .dataSource("test-source")
                .priority(5)
                .isActive(true)
                .createAlert(false)
                .build();
    }

    @Test
    void testCreateRule() {
        CreateDetectionRuleCommand command = CreateDetectionRuleCommand.builder()
                .name("New Rule")
                .ruleType(RuleType.THRESHOLD)
                .conditions(new HashMap<>())
                .dataSource("test-source")
                .tenantId(tenantId)
                .createdBy("user-1")
                .build();

        when(ruleRepository.save(anyString(), any(DetectionRule.class))).thenReturn(testRule);

        DetectionRuleDto result = service.createRule(command);

        assertNotNull(result);
        assertEquals("Test Rule", result.getName());
        verify(ruleRepository, times(1)).save(eq(tenantId), any(DetectionRule.class));
    }

    @Test
    void testGetRuleById() {
        when(ruleRepository.findById(tenantId, ruleId)).thenReturn(Optional.of(testRule));

        DetectionRuleDto result = service.getRuleById(tenantId, ruleId);

        assertNotNull(result);
        assertEquals(ruleId, result.getId());
        verify(ruleRepository, times(1)).findById(tenantId, ruleId);
    }

    @Test
    void testGetRuleByIdNotFound() {
        when(ruleRepository.findById(tenantId, ruleId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.getRuleById(tenantId, ruleId));
    }

    @Test
    void testListRules() {
        when(ruleRepository.findByTenantId(tenantId)).thenReturn(List.of(testRule));

        List<DetectionRuleDto> result = service.listRules(tenantId);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(ruleRepository, times(1)).findByTenantId(tenantId);
    }

    @Test
    void testDeleteRule() {
        when(ruleRepository.exists(tenantId, ruleId)).thenReturn(true);
        doNothing().when(ruleRepository).delete(tenantId, ruleId);

        service.deleteRule(tenantId, ruleId);

        verify(ruleRepository, times(1)).delete(tenantId, ruleId);
    }

    @Test
    void testDeleteRuleNotFound() {
        when(ruleRepository.exists(tenantId, ruleId)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> service.deleteRule(tenantId, ruleId));
    }

    @Test
    void testListDetections() {
        AnomalyDetection detection = AnomalyDetection.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .build();

        when(detectionRepository.findByTenantId(tenantId)).thenReturn(List.of(detection));

        List<AnomalyDetectionDto> result = service.listDetections(tenantId, 0, 20);

        assertNotNull(result);
        verify(detectionRepository, times(1)).findByTenantId(tenantId);
    }

    @Test
    void testListPatterns() {
        List<com.gogidix.rapidassist.ai.anomaly.application.dto.AnomalyPatternDto> result = service.listPatterns(tenantId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
