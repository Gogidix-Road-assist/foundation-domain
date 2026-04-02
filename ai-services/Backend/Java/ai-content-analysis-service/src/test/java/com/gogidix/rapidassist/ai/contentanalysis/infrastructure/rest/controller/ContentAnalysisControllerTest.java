package com.gogidix.rapidassist.ai.contentanalysis.infrastructure.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.rapidassist.ai.contentanalysis.application.service.ContentAnalysisApplicationService;
import com.gogidix.rapidassist.ai.contentanalysis.bootstrap.Application;
import com.gogidix.rapidassist.ai.contentanalysis.domain.tenant.TenantContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for ContentAnalysisController
 */
@WebMvcTest(ContentAnalysisController.class)
@ContextConfiguration(classes = {Application.class, ContentAnalysisController.class})
class ContentAnalysisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ContentAnalysisApplicationService applicationService;

    private String tenantId = "test-tenant";

    @BeforeEach
    void setUp() {
        TenantContext.setTenantId(tenantId);
    }

    @Test
    void testAnalyzeContent() throws Exception {
        String requestJson = """
            {
                "contentId": "content-1",
                "contentType": "article",
                "contentTitle": "Test Article",
                "contentBody": "This is a test article content.",
                "contentLanguage": "en",
                "requestedBy": "user-1"
            }
            """;

        mockMvc.perform(post("/api/v1/content-analysis/analyze")
                        .header("X-Tenant-ID", tenantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated());
    }

    @Test
    void testGetAnalysis() throws Exception {
        UUID analysisId = UUID.randomUUID();

        mockMvc.perform(get("/api/v1/content-analysis/" + analysisId)
                        .header("X-Tenant-ID", tenantId)
                        .param("includeTopics", "true"))
                .andExpect(status().isOk());
    }

    @Test
    void testListAnalyses() throws Exception {
        mockMvc.perform(get("/api/v1/content-analysis")
                        .header("X-Tenant-ID", tenantId)
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk());
    }

    @Test
    void testBulkAnalyzeContent() throws Exception {
        String requestJson = """
            {
                "contentItems": [
                    {
                        "contentId": "content-1",
                        "contentType": "article",
                        "contentTitle": "Article 1",
                        "contentBody": "Content 1"
                    },
                    {
                        "contentId": "content-2",
                        "contentType": "article",
                        "contentTitle": "Article 2",
                        "contentBody": "Content 2"
                    }
                ],
                "requestedBy": "user-1"
            }
            """;

        mockMvc.perform(post("/api/v1/content-analysis/bulk")
                        .header("X-Tenant-ID", tenantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated());
    }

    @Test
    void testGetAnalysisByContentId() throws Exception {
        String contentId = "content-1";

        mockMvc.perform(get("/api/v1/content-analysis/content/" + contentId)
                        .header("X-Tenant-ID", tenantId)
                        .param("includeTopics", "true"))
                .andExpect(status().isOk());
    }

    @Test
    void testExtractTopics() throws Exception {
        String requestJson = """
            {
                "contentId": "content-1",
                "contentBody": "This is a test article content about artificial intelligence and machine learning.",
                "contentLanguage": "en",
                "maxTopics": 5,
                "minRelevanceScore": 0.7
            }
            """;

        mockMvc.perform(post("/api/v1/content-analysis/topics/extract")
                        .header("X-Tenant-ID", tenantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteAnalysis() throws Exception {
        UUID analysisId = UUID.randomUUID();

        mockMvc.perform(delete("/api/v1/content-analysis/" + analysisId)
                        .header("X-Tenant-ID", tenantId))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetMetrics() throws Exception {
        UUID analysisId = UUID.randomUUID();

        mockMvc.perform(get("/api/v1/content-analysis/metrics/" + analysisId)
                        .header("X-Tenant-ID", tenantId))
                .andExpect(status().isOk());
    }
}
