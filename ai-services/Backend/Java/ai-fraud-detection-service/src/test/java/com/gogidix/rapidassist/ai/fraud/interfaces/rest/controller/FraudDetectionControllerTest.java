package com.gogidix.rapidassist.ai.fraud.interfaces.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.rapidassist.ai.fraud.application.dto.FraudDetectionDto;
import com.gogidix.rapidassist.ai.fraud.application.dto.FraudAlertDto;
import com.gogidix.rapidassist.ai.fraud.application.dto.FraudCaseDto;
import com.gogidix.rapidassist.ai.fraud.application.dto.FraudPatternDto;
import com.gogidix.rapidassist.ai.fraud.application.dto.FraudRuleDto;
import com.gogidix.rapidassist.ai.fraud.application.service.FraudDetectionService;
import com.gogidix.rapidassist.ai.fraud.domain.model.FraudRiskLevel;
import com.gogidix.rapidassist.ai.fraud.domain.tenant.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.*;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Unit tests for FraudDetectionController
 * Uses pure Mockito MockMvc approach without @WebMvcTest
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("FraudDetectionController Tests")
class FraudDetectionControllerTest {

    @Mock
    private FraudDetectionService fraudDetectionService;

    @InjectMocks
    private FraudDetectionController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private static final String TENANT_ID = "tenant-123";
    private UUID testId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
        testId = UUID.randomUUID();
        TenantContext.clear();
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    // ==================== POST /detect tests ====================

    @Test
    @DisplayName("POST /detect - Returns created detection")
    void testDetectFraud_ReturnsCreatedDetection() throws Exception {
        FraudDetectionDto detectionDto = FraudDetectionDto.builder()
                .id(testId)
                .tenantId(TENANT_ID)
                .entityType("CLAIM")
                .entityId("claim-123")
                .riskLevel(FraudRiskLevel.HIGH)
                .riskScore(0.85)
                .build();

        when(fraudDetectionService.detectFraud(eq(TENANT_ID), eq("CLAIM"), eq("claim-123"),
                eq(FraudRiskLevel.HIGH), eq(0.85), eq("ML_MODEL"), ArgumentMatchers.anyMap()))
                .thenReturn(detectionDto);

        String requestBody = """
                {
                    "entityType": "CLAIM",
                    "entityId": "claim-123",
                    "riskLevel": "HIGH",
                    "riskScore": 0.85,
                    "detectionMethod": "ML_MODEL",
                    "details": {"confidence": 0.85}
                }
                """;

        mockMvc.perform(post("/api/v1/fraud-detection/detect")
                        .header("X-Tenant-ID", TENANT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testId.toString()))
                .andExpect(jsonPath("$.entityType").value("CLAIM"))
                .andExpect(jsonPath("$.riskLevel").value("HIGH"))
                .andExpect(jsonPath("$.riskScore").value(0.85));

        verify(fraudDetectionService).detectFraud(eq(TENANT_ID), eq("CLAIM"), eq("claim-123"),
                eq(FraudRiskLevel.HIGH), eq(0.85), eq("ML_MODEL"), ArgumentMatchers.anyMap());
    }

    // ==================== GET /{id} tests ====================

    @Test
    @DisplayName("GET /{id} - Returns detection")
    void testGetDetectionById_ReturnsDetection() throws Exception {
        FraudDetectionDto detectionDto = FraudDetectionDto.builder()
                .id(testId)
                .tenantId(TENANT_ID)
                .entityType("TRANSACTION")
                .entityId("txn-456")
                .riskLevel(FraudRiskLevel.MEDIUM)
                .build();

        when(fraudDetectionService.getDetectionById(TENANT_ID, testId))
                .thenReturn(detectionDto);

        mockMvc.perform(get("/api/v1/fraud-detection/{id}", testId)
                        .header("X-Tenant-ID", TENANT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testId.toString()))
                .andExpect(jsonPath("$.entityType").value("TRANSACTION"));

        verify(fraudDetectionService).getDetectionById(TENANT_ID, testId);
    }

    @Test
    @DisplayName("GET /{id} - Returns 404 when not found")
    void testGetDetectionById_Returns404WhenNotFound() throws Exception {
        when(fraudDetectionService.getDetectionById(TENANT_ID, testId))
                .thenThrow(new RuntimeException("FraudDetection not found"));

        mockMvc.perform(get("/api/v1/fraud-detection/{id}", testId)
                        .header("X-Tenant-ID", TENANT_ID))
                .andExpect(status().isNotFound());

        verify(fraudDetectionService).getDetectionById(TENANT_ID, testId);
    }

    // ==================== GET / (list detections) tests ====================

    @Test
    @DisplayName("GET / - Returns all detections")
    void testGetAllDetections_ReturnsAllDetections() throws Exception {
        List<FraudDetectionDto> detections = Arrays.asList(
                FraudDetectionDto.builder().id(UUID.randomUUID()).entityType("CLAIM").build(),
                FraudDetectionDto.builder().id(UUID.randomUUID()).entityType("TRANSACTION").build()
        );

        when(fraudDetectionService.getAllDetections(TENANT_ID)).thenReturn(detections);

        mockMvc.perform(get("/api/v1/fraud-detection")
                        .header("X-Tenant-ID", TENANT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].entityType").value("CLAIM"))
                .andExpect(jsonPath("$[1].entityType").value("TRANSACTION"));

        verify(fraudDetectionService).getAllDetections(TENANT_ID);
    }

    @Test
    @DisplayName("GET / - Returns empty list")
    void testGetAllDetections_ReturnsEmptyList() throws Exception {
        when(fraudDetectionService.getAllDetections(TENANT_ID)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/fraud-detection")
                        .header("X-Tenant-ID", TENANT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /?status=PENDING - Filters by status")
    void testGetAllDetections_FiltersByStatus() throws Exception {
        List<FraudDetectionDto> detections = Arrays.asList(
                FraudDetectionDto.builder().id(UUID.randomUUID()).status("PENDING").build(),
                FraudDetectionDto.builder().id(UUID.randomUUID()).status("PENDING").build()
        );

        when(fraudDetectionService.getDetectionsByStatus(TENANT_ID, "PENDING"))
                .thenReturn(detections);

        mockMvc.perform(get("/api/v1/fraud-detection")
                        .header("X-Tenant-ID", TENANT_ID)
                        .param("status", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].status").value("PENDING"))
                .andExpect(jsonPath("$[1].status").value("PENDING"));

        verify(fraudDetectionService).getDetectionsByStatus(TENANT_ID, "PENDING");
        verify(fraudDetectionService, never()).getAllDetections(TENANT_ID);
    }

    // ==================== GET /alerts tests ====================

    @Test
    @DisplayName("GET /alerts - Returns all alerts")
    void testGetAllAlerts_ReturnsAllAlerts() throws Exception {
        List<FraudAlertDto> alerts = Arrays.asList(
                FraudAlertDto.builder().id(UUID.randomUUID()).severity("HIGH").build(),
                FraudAlertDto.builder().id(UUID.randomUUID()).severity("CRITICAL").build()
        );

        when(fraudDetectionService.getAllAlerts(TENANT_ID)).thenReturn(alerts);

        mockMvc.perform(get("/api/v1/fraud-detection/alerts")
                        .header("X-Tenant-ID", TENANT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].severity").value("HIGH"))
                .andExpect(jsonPath("$[1].severity").value("CRITICAL"));

        verify(fraudDetectionService).getAllAlerts(TENANT_ID);
    }

    @Test
    @DisplayName("GET /alerts?status=ACKNOWLEDGED - Filters by status")
    void testGetAllAlerts_FiltersByStatus() throws Exception {
        List<FraudAlertDto> alerts = Arrays.asList(
                FraudAlertDto.builder().id(UUID.randomUUID()).status("ACKNOWLEDGED").build()
        );

        when(fraudDetectionService.getAlertsByStatus(TENANT_ID, "ACKNOWLEDGED"))
                .thenReturn(alerts);

        mockMvc.perform(get("/api/v1/fraud-detection/alerts")
                        .header("X-Tenant-ID", TENANT_ID)
                        .param("status", "ACKNOWLEDGED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status").value("ACKNOWLEDGED"));

        verify(fraudDetectionService).getAlertsByStatus(TENANT_ID, "ACKNOWLEDGED");
    }

    // ==================== POST /alerts/{id}/acknowledge tests ====================

    @Test
    @DisplayName("POST /alerts/{id}/acknowledge - Acknowledges alert")
    void testAcknowledgeAlert_AcknowledgesAlert() throws Exception {
        FraudAlertDto alertDto = FraudAlertDto.builder()
                .id(testId)
                .status("ACKNOWLEDGED")
                .acknowledgedBy("admin")
                .acknowledgedAt(LocalDateTime.now())
                .build();

        when(fraudDetectionService.acknowledgeAlert(TENANT_ID, testId, "admin", "Reviewing"))
                .thenReturn(alertDto);

        String requestBody = """
                {
                    "acknowledgedBy": "admin",
                    "notes": "Reviewing"
                }
                """;

        mockMvc.perform(post("/api/v1/fraud-detection/alerts/{id}/acknowledge", testId)
                        .header("X-Tenant-ID", TENANT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testId.toString()))
                .andExpect(jsonPath("$.status").value("ACKNOWLEDGED"))
                .andExpect(jsonPath("$.acknowledgedBy").value("admin"));

        verify(fraudDetectionService).acknowledgeAlert(TENANT_ID, testId, "admin", "Reviewing");
    }

    // ==================== POST /cases tests ====================

    @Test
    @DisplayName("POST /cases - Creates fraud case")
    void testCreateCase_CreatesFraudCase() throws Exception {
        FraudCaseDto caseDto = FraudCaseDto.builder()
                .id(testId)
                .caseType("FRAUD_INVESTIGATION")
                .title("Suspicious Claim")
                .description("Requires investigation")
                .priority("HIGH")
                .assignedTo("analyst1")
                .build();

        when(fraudDetectionService.createCase(TENANT_ID, "FRAUD_INVESTIGATION",
                "Suspicious Claim", "Requires investigation", "HIGH", "analyst1"))
                .thenReturn(caseDto);

        String requestBody = """
                {
                    "caseType": "FRAUD_INVESTIGATION",
                    "title": "Suspicious Claim",
                    "description": "Requires investigation",
                    "priority": "HIGH",
                    "assignedTo": "analyst1"
                }
                """;

        mockMvc.perform(post("/api/v1/fraud-detection/cases")
                        .header("X-Tenant-ID", TENANT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testId.toString()))
                .andExpect(jsonPath("$.caseType").value("FRAUD_INVESTIGATION"))
                .andExpect(jsonPath("$.title").value("Suspicious Claim"))
                .andExpect(jsonPath("$.priority").value("HIGH"));

        verify(fraudDetectionService).createCase(TENANT_ID, "FRAUD_INVESTIGATION",
                "Suspicious Claim", "Requires investigation", "HIGH", "analyst1");
    }

    @Test
    @DisplayName("POST /cases - All case types")
    void testCreateCase_AllCaseTypes() throws Exception {
        String[] caseTypes = {
            "FRAUD_INVESTIGATION",
            "MONEY_LAUNDERING",
            "IDENTITY_THEFT",
            "CLAIMS_FRAUD",
            "TRANSACTION_FRAUD"
        };

        for (String caseType : caseTypes) {
            FraudCaseDto caseDto = FraudCaseDto.builder()
                    .id(UUID.randomUUID())
                    .caseType(caseType)
                    .build();

            when(fraudDetectionService.createCase(eq(TENANT_ID), eq(caseType),
                    anyString(), anyString(), anyString(), anyString()))
                    .thenReturn(caseDto);

            String requestBody = String.format("""
                    {
                        "caseType": "%s",
                        "title": "Test Case",
                        "description": "Test",
                        "priority": "MEDIUM",
                        "assignedTo": "analyst"
                    }
                    """, caseType);

            mockMvc.perform(post("/api/v1/fraud-detection/cases")
                            .header("X-Tenant-ID", TENANT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.caseType").value(caseType));
        }
    }

    // ==================== PUT /cases/{id} tests ====================

    @Test
    @DisplayName("PUT /cases/{id} - Updates fraud case")
    void testUpdateCase_UpdatesFraudCase() throws Exception {
        FraudCaseDto caseDto = FraudCaseDto.builder()
                .id(testId)
                .assignedTo("analyst2")
                .finding("Fraud confirmed")
                .decision("Block claim")
                .build();

        when(fraudDetectionService.updateCase(TENANT_ID, testId, "analyst2", "Fraud confirmed", "Block claim"))
                .thenReturn(caseDto);

        String requestBody = """
                {
                    "assignedTo": "analyst2",
                    "finding": "Fraud confirmed",
                    "decision": "Block claim"
                }
                """;

        mockMvc.perform(put("/api/v1/fraud-detection/cases/{id}", testId)
                        .header("X-Tenant-ID", TENANT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testId.toString()))
                .andExpect(jsonPath("$.assignedTo").value("analyst2"))
                .andExpect(jsonPath("$.finding").value("Fraud confirmed"))
                .andExpect(jsonPath("$.decision").value("Block claim"));

        verify(fraudDetectionService).updateCase(TENANT_ID, testId, "analyst2", "Fraud confirmed", "Block claim");
    }

    // ==================== GET /cases tests ====================

    @Test
    @DisplayName("GET /cases - Returns all cases")
    void testGetAllCases_ReturnsAllCases() throws Exception {
        List<FraudCaseDto> cases = Arrays.asList(
                FraudCaseDto.builder().id(UUID.randomUUID()).caseType("FRAUD_INVESTIGATION").build(),
                FraudCaseDto.builder().id(UUID.randomUUID()).caseType("MONEY_LAUNDERING").build()
        );

        when(fraudDetectionService.getAllCases(TENANT_ID)).thenReturn(cases);

        mockMvc.perform(get("/api/v1/fraud-detection/cases")
                        .header("X-Tenant-ID", TENANT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].caseType").value("FRAUD_INVESTIGATION"))
                .andExpect(jsonPath("$[1].caseType").value("MONEY_LAUNDERING"));

        verify(fraudDetectionService).getAllCases(TENANT_ID);
    }

    @Test
    @DisplayName("GET /cases - Returns empty list")
    void testGetAllCases_ReturnsEmptyList() throws Exception {
        when(fraudDetectionService.getAllCases(TENANT_ID)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/fraud-detection/cases")
                        .header("X-Tenant-ID", TENANT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ==================== POST /rules tests ====================

    @Test
    @DisplayName("POST /rules - Creates detection rule")
    void testCreateRule_CreatesRule() throws Exception {
        FraudRuleDto ruleDto = FraudRuleDto.builder()
                .id(testId)
                .ruleName("High Amount Threshold")
                .ruleCode("HIGH_AMOUNT_001")
                .ruleType("THRESHOLD")
                .action("BLOCK")
                .priority(1)
                .build();

        when(fraudDetectionService.createRule(TENANT_ID, "High Amount Threshold", "HIGH_AMOUNT_001",
                "Threshold rule", "THRESHOLD", Map.of("min", 1000), "BLOCK", 1))
                .thenReturn(ruleDto);

        String requestBody = """
                {
                    "ruleName": "High Amount Threshold",
                    "ruleCode": "HIGH_AMOUNT_001",
                    "description": "Threshold rule",
                    "ruleType": "THRESHOLD",
                    "conditions": {"min": 1000},
                    "action": "BLOCK",
                    "priority": 1
                }
                """;

        mockMvc.perform(post("/api/v1/fraud-detection/rules")
                        .header("X-Tenant-ID", TENANT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testId.toString()))
                .andExpect(jsonPath("$.ruleName").value("High Amount Threshold"))
                .andExpect(jsonPath("$.action").value("BLOCK"));

        verify(fraudDetectionService).createRule(TENANT_ID, "High Amount Threshold", "HIGH_AMOUNT_001",
                "Threshold rule", "THRESHOLD", Map.of("min", 1000), "BLOCK", 1);
    }

    @Test
    @DisplayName("POST /rules - All rule types")
    void testCreateRule_AllRuleTypes() throws Exception {
        String[] ruleTypes = {"THRESHOLD", "VELOCITY", "PATTERN", "ANOMALY", "COMPOSITE"};

        for (String ruleType : ruleTypes) {
            FraudRuleDto ruleDto = FraudRuleDto.builder()
                    .id(UUID.randomUUID())
                    .ruleType(ruleType)
                    .build();

            when(fraudDetectionService.createRule(eq(TENANT_ID), anyString(), anyString(),
                    anyString(), eq(ruleType), ArgumentMatchers.anyMap(), anyString(), anyInt()))
                    .thenReturn(ruleDto);

            String requestBody = String.format("""
                    {
                        "ruleName": "Test Rule",
                        "ruleCode": "RULE001",
                        "description": "Test",
                        "ruleType": "%s",
                        "conditions": {},
                        "action": "REVIEW",
                        "priority": 1
                    }
                    """, ruleType);

            mockMvc.perform(post("/api/v1/fraud-detection/rules")
                            .header("X-Tenant-ID", TENANT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.ruleType").value(ruleType));
        }
    }

    @Test
    @DisplayName("POST /rules - All action types")
    void testCreateRule_AllActionTypes() throws Exception {
        String[] actions = {"ALLOW", "BLOCK", "REVIEW", "FLAG", "CHALLENGE"};

        for (String action : actions) {
            FraudRuleDto ruleDto = FraudRuleDto.builder()
                    .id(UUID.randomUUID())
                    .action(action)
                    .build();

            when(fraudDetectionService.createRule(eq(TENANT_ID), anyString(), anyString(),
                    anyString(), anyString(), ArgumentMatchers.anyMap(), eq(action), anyInt()))
                    .thenReturn(ruleDto);

            String requestBody = String.format("""
                    {
                        "ruleName": "Test Rule",
                        "ruleCode": "RULE001",
                        "description": "Test",
                        "ruleType": "THRESHOLD",
                        "conditions": {},
                        "action": "%s",
                        "priority": 1
                    }
                    """, action);

            mockMvc.perform(post("/api/v1/fraud-detection/rules")
                            .header("X-Tenant-ID", TENANT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.action").value(action));
        }
    }

    // ==================== GET /rules tests ====================

    @Test
    @DisplayName("GET /rules - Returns all rules")
    void testGetAllRules_ReturnsAllRules() throws Exception {
        List<FraudRuleDto> rules = Arrays.asList(
                FraudRuleDto.builder().id(UUID.randomUUID()).ruleName("Rule1").build(),
                FraudRuleDto.builder().id(UUID.randomUUID()).ruleName("Rule2").build()
        );

        when(fraudDetectionService.getAllRules(TENANT_ID)).thenReturn(rules);

        mockMvc.perform(get("/api/v1/fraud-detection/rules")
                        .header("X-Tenant-ID", TENANT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].ruleName").value("Rule1"))
                .andExpect(jsonPath("$[1].ruleName").value("Rule2"));

        verify(fraudDetectionService).getAllRules(TENANT_ID);
    }

    @Test
    @DisplayName("GET /rules - Returns empty list")
    void testGetAllRules_ReturnsEmptyList() throws Exception {
        when(fraudDetectionService.getAllRules(TENANT_ID)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/fraud-detection/rules")
                        .header("X-Tenant-ID", TENANT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ==================== POST /patterns tests ====================

    @Test
    @DisplayName("POST /patterns - Creates fraud pattern")
    void testCreatePattern_CreatesPattern() throws Exception {
        FraudPatternDto patternDto = FraudPatternDto.builder()
                .id(testId)
                .patternName("Velocity Check")
                .patternType("VELOCITY")
                .description("Checks transaction velocity")
                .build();

        when(fraudDetectionService.createPattern(TENANT_ID, "Velocity Check", "VELOCITY",
                "Checks transaction velocity", Map.of("max", 10)))
                .thenReturn(patternDto);

        String requestBody = """
                {
                    "patternName": "Velocity Check",
                    "patternType": "VELOCITY",
                    "description": "Checks transaction velocity",
                    "definition": {"max": 10}
                }
                """;

        mockMvc.perform(post("/api/v1/fraud-detection/patterns")
                        .header("X-Tenant-ID", TENANT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testId.toString()))
                .andExpect(jsonPath("$.patternName").value("Velocity Check"))
                .andExpect(jsonPath("$.patternType").value("VELOCITY"));

        verify(fraudDetectionService).createPattern(TENANT_ID, "Velocity Check", "VELOCITY",
                "Checks transaction velocity", Map.of("max", 10));
    }

    @Test
    @DisplayName("POST /patterns - All pattern types")
    void testCreatePattern_AllPatternTypes() throws Exception {
        String[] patternTypes = {"GEOGRAPHIC", "TEMPORAL", "BEHAVIORAL", "NETWORK", "VELOCITY"};

        for (String patternType : patternTypes) {
            FraudPatternDto patternDto = FraudPatternDto.builder()
                    .id(UUID.randomUUID())
                    .patternType(patternType)
                    .build();

            when(fraudDetectionService.createPattern(eq(TENANT_ID), anyString(), eq(patternType),
                    anyString(), ArgumentMatchers.anyMap()))
                    .thenReturn(patternDto);

            String requestBody = String.format("""
                    {
                        "patternName": "Test Pattern",
                        "patternType": "%s",
                        "description": "Test",
                        "definition": {}
                    }
                    """, patternType);

            mockMvc.perform(post("/api/v1/fraud-detection/patterns")
                            .header("X-Tenant-ID", TENANT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.patternType").value(patternType));
        }
    }

    // ==================== GET /patterns tests ====================

    @Test
    @DisplayName("GET /patterns - Returns all patterns")
    void testGetAllPatterns_ReturnsAllPatterns() throws Exception {
        List<FraudPatternDto> patterns = Arrays.asList(
                FraudPatternDto.builder().id(UUID.randomUUID()).patternName("Pattern1").build(),
                FraudPatternDto.builder().id(UUID.randomUUID()).patternName("Pattern2").build()
        );

        when(fraudDetectionService.getAllPatterns(TENANT_ID)).thenReturn(patterns);

        mockMvc.perform(get("/api/v1/fraud-detection/patterns")
                        .header("X-Tenant-ID", TENANT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].patternName").value("Pattern1"))
                .andExpect(jsonPath("$[1].patternName").value("Pattern2"));

        verify(fraudDetectionService).getAllPatterns(TENANT_ID);
    }

    @Test
    @DisplayName("GET /patterns - Returns empty list")
    void testGetAllPatterns_ReturnsEmptyList() throws Exception {
        when(fraudDetectionService.getAllPatterns(TENANT_ID)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/fraud-detection/patterns")
                        .header("X-Tenant-ID", TENANT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ==================== Tenant Context tests ====================

    @Test
    @DisplayName("X-Tenant-ID header - Sets tenant context")
    void testTenantHeader_SetsTenantContext() throws Exception {
        when(fraudDetectionService.getAllDetections(TENANT_ID))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/fraud-detection")
                        .header("X-Tenant-ID", TENANT_ID))
                .andExpect(status().isOk());

        // Verify TenantContext was set
        assertTrue(TenantContext.getTenantId().isPresent());
        assertEquals(TENANT_ID, TenantContext.getTenantId().orElse(null));
    }
}
