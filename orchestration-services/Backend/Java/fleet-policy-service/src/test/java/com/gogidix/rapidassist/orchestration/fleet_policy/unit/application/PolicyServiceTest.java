package com.gogidix.rapidassist.orchestration.fleet_policy.unit.application;

import com.gogidix.rapidassist.orchestration.fleet_policy.application.dto.request.PolicyRequestDto;
import com.gogidix.rapidassist.orchestration.fleet_policy.application.dto.response.PolicyResponseDto;
import com.gogidix.rapidassist.orchestration.fleet_policy.application.mapper.PolicyMapper;
import com.gogidix.rapidassist.orchestration.fleet_policy.application.service.PolicyService;
import com.gogidix.rapidassist.orchestration.fleet_policy.domain.model.Policy;
import com.gogidix.rapidassist.orchestration.fleet_policy.domain.repository.PolicyRepository;
import com.gogidix.rapidassist.orchestration.fleet_policy.domain.repository.PolicyRuleRepository;
import com.gogidix.rapidassist.orchestration.fleet_policy.domain.repository.PolicyComplianceRepository;
import com.gogidix.rapidassist.orchestration.fleet_policy.domain.repository.PolicyViolationRepository;
import com.gogidix.rapidassist.orchestration.fleet_policy.domain.service.PolicyValidationEngine;
import com.gogidix.rapidassist.orchestration.fleet_policy.shared.exception.NotFoundException;
import com.gogidix.rapidassist.orchestration.fleet_policy.shared.exception.ValidationException;
import com.gogidix.rapidassist.orchestration.fleet_policy.shared.requestcontext.RequestContext;
import com.gogidix.rapidassist.orchestration.fleet_policy.shared.requestcontext.RequestContextHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PolicyService
 */
@ExtendWith(MockitoExtension.class)
class PolicyServiceTest {

    @Mock
    private PolicyRepository policyRepository;

    @Mock
    private PolicyRuleRepository ruleRepository;

    @Mock
    private PolicyComplianceRepository complianceRepository;

    @Mock
    private PolicyViolationRepository violationRepository;

    @Mock
    private PolicyMapper policyMapper;

    @Mock
    private PolicyValidationEngine validationEngine;

    @InjectMocks
    private PolicyService policyService;

    private RequestContext requestContext;

    @BeforeEach
    void setUp() {
        requestContext = RequestContext.builder()
                .tenantId("test-tenant")
                .userId("test-user")
                .build();
        RequestContextHolder.setContext(requestContext);
    }

    @Test
    void testCreatePolicy_Success() {
        // Arrange
        PolicyRequestDto dto = PolicyRequestDto.builder()
                .policyCode("SAFETY-001")
                .name("Safety Policy")
                .description("Fleet safety rules")
                .policyType(Policy.PolicyType.SAFETY)
                .severity(Policy.PolicySeverity.HIGH)
                .build();

        Policy policy = createTestPolicy();
        PolicyResponseDto responseDto = createTestPolicyResponseDto();

        when(policyRepository.existsByTenantIdAndPolicyCode(anyString(), anyString())).thenReturn(false);
        when(policyMapper.toEntity(any())).thenReturn(policy);
        when(policyRepository.save(any())).thenReturn(policy);
        when(policyMapper.toResponseDto(any())).thenReturn(responseDto);

        // Act
        PolicyResponseDto result = policyService.createPolicy(dto);

        // Assert
        assertNotNull(result);
        assertEquals("SAFETY-001", result.getPolicyCode());
        verify(policyRepository, times(1)).save(any(Policy.class));
    }

    @Test
    void testCreatePolicy_DuplicateCode() {
        // Arrange
        PolicyRequestDto dto = PolicyRequestDto.builder()
                .policyCode("SAFETY-001")
                .name("Safety Policy")
                .policyType(Policy.PolicyType.SAFETY)
                .build();

        when(policyRepository.existsByTenantIdAndPolicyCode(anyString(), anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(ValidationException.class, () -> policyService.createPolicy(dto));
        verify(policyRepository, never()).save(any(Policy.class));
    }

    @Test
    void testGetPolicyById_Success() {
        // Arrange
        String policyId = "policy-1";
        Policy policy = createTestPolicy();
        PolicyResponseDto responseDto = createTestPolicyResponseDto();

        when(policyRepository.findById(policyId)).thenReturn(Optional.of(policy));
        when(policyMapper.toResponseDto(policy)).thenReturn(responseDto);

        // Act
        PolicyResponseDto result = policyService.getPolicyById(policyId);

        // Assert
        assertNotNull(result);
        assertEquals(policyId, result.getId());
        verify(policyRepository, times(1)).findById(policyId);
    }

    @Test
    void testGetPolicyById_NotFound() {
        // Arrange
        String policyId = "non-existent";
        when(policyRepository.findById(policyId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> policyService.getPolicyById(policyId));
    }

    @Test
    void testGetAllPolicies_Success() {
        // Arrange
        List<Policy> policies = Arrays.asList(createTestPolicy());
        List<PolicyResponseDto> responseDtos = Arrays.asList(createTestPolicyResponseDto());

        when(policyRepository.findByTenantId(anyString())).thenReturn(policies);
        when(policyMapper.toPolicyResponseDtoList(policies)).thenReturn(responseDtos);

        // Act
        List<PolicyResponseDto> result = policyService.getAllPolicies();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(policyRepository, times(1)).findByTenantId(anyString());
    }

    @Test
    void testActivatePolicy_Success() {
        // Arrange
        String policyId = "policy-1";
        Policy policy = createTestPolicy();
        PolicyResponseDto responseDto = createTestPolicyResponseDto();

        when(policyRepository.findById(policyId)).thenReturn(Optional.of(policy));
        when(policyRepository.save(any())).thenReturn(policy);
        when(policyMapper.toResponseDto(policy)).thenReturn(responseDto);

        // Act
        PolicyResponseDto result = policyService.activatePolicy(policyId);

        // Assert
        assertNotNull(result);
        assertEquals(Policy.PolicyStatus.ACTIVE, policy.getStatus());
        verify(policyRepository, times(1)).save(policy);
    }

    private Policy createTestPolicy() {
        return Policy.builder()
                .id("policy-1")
                .tenantId("test-tenant")
                .policyCode("SAFETY-001")
                .name("Safety Policy")
                .description("Fleet safety rules")
                .policyType(Policy.PolicyType.SAFETY)
                .severity(Policy.PolicySeverity.HIGH)
                .status(Policy.PolicyStatus.DRAFT)
                .isActive(true)
                .version(1)
                .build();
    }

    private PolicyResponseDto createTestPolicyResponseDto() {
        return PolicyResponseDto.builder()
                .id("policy-1")
                .tenantId("test-tenant")
                .policyCode("SAFETY-001")
                .name("Safety Policy")
                .description("Fleet safety rules")
                .policyType(Policy.PolicyType.SAFETY)
                .severity(Policy.PolicySeverity.HIGH)
                .status(Policy.PolicyStatus.DRAFT)
                .isActive(true)
                .effective(true)
                .ruleCount(0)
                .version(1)
                .build();
    }
}
