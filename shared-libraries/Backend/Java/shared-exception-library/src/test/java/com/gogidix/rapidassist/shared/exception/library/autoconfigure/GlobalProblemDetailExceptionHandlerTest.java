package com.gogidix.rapidassist.shared.exception.library.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.rapidassist.shared.exception.library.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for GlobalProblemDetailExceptionHandler.
 */
@SpringBootTest
@AutoConfigureMockMvc
class GlobalProblemDetailExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testHandleNotFoundException() throws Exception {
        mockMvc.perform(get("/test/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource not found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.type").exists())
                .andExpect(jsonPath("$.detail").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testHandleBadRequestException() throws Exception {
        mockMvc.perform(get("/test/bad-request"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testHandleConflictException() throws Exception {
        mockMvc.perform(post("/test/conflict")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Resource conflict"))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.detail").exists());
    }

    @Test
    void testHandleForbiddenException() throws Exception {
        mockMvc.perform(get("/test/forbidden"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.title").value("Access forbidden"))
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.detail").exists());
    }

    @Test
    void testHandleUnauthorizedException() throws Exception {
        mockMvc.perform(get("/test/unauthorized"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.title").value("Unauthorized"))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.detail").exists());
    }

    @Test
    void testHandleServiceUnavailableException() throws Exception {
        mockMvc.perform(get("/test/service-unavailable"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.title").value("Service unavailable"))
                .andExpect(jsonPath("$.status").value(503))
                .andExpect(jsonPath("$.detail").exists());
    }

    @Test
    void testProblemDetailIncludesAllRequiredFields() throws Exception {
        MvcResult result = mockMvc.perform(get("/test/not-found"))
                .andExpect(status().isNotFound())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        assertNotNull(response);

        // Parse and check fields
        var jsonNode = objectMapper.readTree(response);
        assertTrue(jsonNode.has("title"));
        assertTrue(jsonNode.has("status"));
        assertTrue(jsonNode.has("type"));
        assertTrue(jsonNode.has("detail"));
        assertTrue(jsonNode.has("timestamp"));
        assertTrue(jsonNode.has("path"));
    }
}
