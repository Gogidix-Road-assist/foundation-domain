package com.gogidix.rapidassist.database.management.service.adapters.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for GlobalExceptionHandler.
 * <p>
 * Tests that exceptions are properly handled and returned with
 * appropriate HTTP status codes and error messages.
 * </p>
 */
@WebMvcTest(DatabaseManagementController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should return 400 Bad Request for validation errors")
    void testValidationErrors() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("name", ""); // Invalid: blank name
        request.put("type", "POSTGRESQL");
        request.put("host", ""); // Invalid: blank host
        request.put("port", 5432);
        request.put("database", "testdb");
        request.put("username", "user");
        request.put("password", "pass");
        request.put("poolSize", 10);

        mockMvc.perform(post("/api/database/connections/register")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Validation Failed"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request for invalid port range")
    void testInvalidPortRange() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("name", "test-db");
        request.put("type", "POSTGRESQL");
        request.put("host", "localhost");
        request.put("port", 70000); // Invalid: exceeds 65535
        request.put("database", "testdb");
        request.put("username", "user");
        request.put("password", "pass");
        request.put("poolSize", 10);

        mockMvc.perform(post("/api/database/connections/register")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Validation Failed"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request for invalid pool size")
    void testInvalidPoolSize() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("name", "test-db");
        request.put("type", "POSTGRESQL");
        request.put("host", "localhost");
        request.put("port", 5432);
        request.put("database", "testdb");
        request.put("username", "user");
        request.put("password", "pass");
        request.put("poolSize", 0); // Invalid: must be at least 1

        mockMvc.perform(post("/api/database/connections/register")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("Should return 400 for invalid connection name pattern")
    void testInvalidConnectionNamePattern() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("name", "test db@#$"); // Invalid: contains spaces and special chars
        request.put("type", "POSTGRESQL");
        request.put("host", "localhost");
        request.put("port", 5432);
        request.put("database", "testdb");
        request.put("username", "user");
        request.put("password", "pass");
        request.put("poolSize", 10);

        mockMvc.perform(post("/api/database/connections/register")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("Should include timestamp in error response")
    void testErrorResponseIncludesTimestamp() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("name", ""); // Invalid: blank name
        request.put("type", "POSTGRESQL");
        request.put("host", "localhost");
        request.put("port", 5432);
        request.put("database", "testdb");
        request.put("username", "user");
        request.put("password", "pass");
        request.put("poolSize", 10);

        mockMvc.perform(post("/api/database/connections/register")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.path").exists());
    }
}
