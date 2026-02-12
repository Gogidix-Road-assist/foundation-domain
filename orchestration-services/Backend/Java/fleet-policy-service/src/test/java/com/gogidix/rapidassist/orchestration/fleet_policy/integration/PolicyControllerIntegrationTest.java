package com.gogidix.rapidassist.orchestration.fleet_policy.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.rapidassist.orchestration.fleet_policy.application.dto.request.PolicyRequestDto;
import com.gogidix.rapidassist.orchestration.fleet_policy.domain.model.Policy;
import com.gogidix.rapidassist.orchestration.fleet_policy.domain.repository.PolicyRepository;
import com.gogidix.rapidassist.orchestration.fleet_policy.shared.requestcontext.RequestContext;
import com.gogidix.rapidassist.orchestration.fleet_policy.shared.requestcontext.RequestContextHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for PolicyController
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class PolicyControllerIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.data.mongodb.database", () -> "test_fleet_policy_db");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PolicyRepository policyRepository;

    private RequestContext requestContext;

    @BeforeEach
    void setUp() {
        policyRepository.deleteAll();
        requestContext = RequestContext.builder()
                .tenantId("test-tenant")
                .userId("test-user")
                .build();
        RequestContextHolder.setContext(requestContext);
    }

    @Test
    void testCreatePolicy_Success() throws Exception {
        // Arrange
        PolicyRequestDto dto = PolicyRequestDto.builder()
                .policyCode("SAFETY-001")
                .name("Safety Policy")
                .description("Fleet safety rules")
                .policyType(Policy.PolicyType.SAFETY)
                .severity(Policy.PolicySeverity.HIGH)
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/v1/policies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.policyCode").value("SAFETY-001"))
                .andExpect(jsonPath("$.name").value("Safety Policy"));
    }

    @Test
    void testGetPolicyById_Success() throws Exception {
        // Arrange
        Policy policy = Policy.builder()
                .tenantId("test-tenant")
                .policyCode("SAFETY-001")
                .name("Safety Policy")
                .description("Fleet safety rules")
                .policyType(Policy.PolicyType.SAFETY)
                .severity(Policy.PolicySeverity.HIGH)
                .status(Policy.PolicyStatus.ACTIVE)
                .isActive(true)
                .createdBy("test-user")
                .createdAt(LocalDateTime.now())
                .build();

        policy = policyRepository.save(policy);

        // Act & Assert
        mockMvc.perform(get("/api/v1/policies/" + policy.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(policy.getId()))
                .andExpect(jsonPath("$.policyCode").value("SAFETY-001"));
    }

    @Test
    void testGetAllPolicies_Success() throws Exception {
        // Arrange
        Policy policy1 = Policy.builder()
                .tenantId("test-tenant")
                .policyCode("SAFETY-001")
                .name("Safety Policy")
                .policyType(Policy.PolicyType.SAFETY)
                .status(Policy.PolicyStatus.ACTIVE)
                .isActive(true)
                .createdBy("test-user")
                .createdAt(LocalDateTime.now())
                .build();

        Policy policy2 = Policy.builder()
                .tenantId("test-tenant")
                .policyCode("MAINT-001")
                .name("Maintenance Policy")
                .policyType(Policy.PolicyType.MAINTENANCE)
                .status(Policy.PolicyStatus.ACTIVE)
                .isActive(true)
                .createdBy("test-user")
                .createdAt(LocalDateTime.now())
                .build();

        policyRepository.save(policy1);
        policyRepository.save(policy2);

        // Act & Assert
        mockMvc.perform(get("/api/v1/policies")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testActivatePolicy_Success() throws Exception {
        // Arrange
        Policy policy = Policy.builder()
                .tenantId("test-tenant")
                .policyCode("SAFETY-001")
                .name("Safety Policy")
                .policyType(Policy.PolicyType.SAFETY)
                .status(Policy.PolicyStatus.DRAFT)
                .isActive(false)
                .createdBy("test-user")
                .createdAt(LocalDateTime.now())
                .build();

        policy = policyRepository.save(policy);

        // Act & Assert
        mockMvc.perform(post("/api/v1/policies/" + policy.getId() + "/activate")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void testDeletePolicy_Success() throws Exception {
        // Arrange
        Policy policy = Policy.builder()
                .tenantId("test-tenant")
                .policyCode("SAFETY-001")
                .name("Safety Policy")
                .policyType(Policy.PolicyType.SAFETY)
                .status(Policy.PolicyStatus.ACTIVE)
                .isActive(true)
                .createdBy("test-user")
                .createdAt(LocalDateTime.now())
                .build();

        policy = policyRepository.save(policy);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/policies/" + policy.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
