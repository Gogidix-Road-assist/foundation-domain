package com.gogidix.rapidassist.database.management.service.adapters.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.rapidassist.database.management.service.domain.model.BackupInfo;
import com.gogidix.rapidassist.database.management.service.domain.model.DatabaseConnection;
import com.gogidix.rapidassist.database.management.service.domain.model.HealthCheckResult;
import com.gogidix.rapidassist.database.management.service.domain.model.MigrationInfo;
import com.gogidix.rapidassist.database.management.service.domain.port.in.DatabaseManagementCommand;
import com.gogidix.rapidassist.database.management.service.domain.port.in.DatabaseManagementQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for DatabaseManagementController.
 * <p>
 * Tests the REST API endpoints for database management operations including
 * connection registration, health checks, migrations, and backups.
 * </p>
 */
@WebMvcTest(DatabaseManagementController.class)
class DatabaseManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DatabaseManagementQuery queryService;

    @MockBean
    private DatabaseManagementCommand commandService;

    private DatabaseConnection testConnection;
    private HealthCheckResult healthCheckResult;

    @BeforeEach
    void setUp() {
        testConnection = new DatabaseConnection(
            "conn-1",
            "Test Database",
            DatabaseConnection.DatabaseType.POSTGRESQL,
            "localhost",
            5432,
            "testdb",
            "user",
            false
        );

        healthCheckResult = new HealthCheckResult(
            "conn-1",
            true,
            150,
            "healthy"
        );
    }

    @Test
    @DisplayName("GET /api/database/health should return health status")
    void testHealthEndpoint() throws Exception {
        mockMvc.perform(get("/api/database/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("UP"))
            .andExpect(jsonPath("$.service").value("database-management-service"))
            .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("GET /api/database/connections should return all connections")
    void testGetAllConnections() throws Exception {
        when(queryService.getAllConnections()).thenReturn(List.of(testConnection));

        mockMvc.perform(get("/api/database/connections"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value("conn-1"))
            .andExpect(jsonPath("$[0].name").value("Test Database"))
            .andExpect(jsonPath("$[0].type").value("POSTGRESQL"));
    }

    @Test
    @DisplayName("GET /api/database/connections/{id} should return connection by id")
    void testGetConnectionById() throws Exception {
        when(queryService.getConnectionById("conn-1")).thenReturn(Optional.of(testConnection));

        mockMvc.perform(get("/api/database/connections/conn-1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("conn-1"))
            .andExpect(jsonPath("$.name").value("Test Database"));
    }

    @Test
    @DisplayName("GET /api/database/connections/{id} should return 404 when not found")
    void testGetConnectionByIdNotFound() throws Exception {
        when(queryService.getConnectionById("nonexistent")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/database/connections/nonexistent"))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/database/connections/register should create connection and return 201")
    void testRegisterConnection() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("name", "test-db");
        request.put("type", "POSTGRESQL");
        request.put("host", "localhost");
        request.put("port", 5432);
        request.put("database", "testdb");
        request.put("username", "user");
        request.put("password", "pass");
        request.put("poolSize", 10);
        request.put("properties", new HashMap<>());

        when(commandService.registerConnection(any())).thenReturn(testConnection);

        mockMvc.perform(post("/api/database/connections/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value("conn-1"))
            .andExpect(jsonPath("$.name").value("Test Database"));
    }

    @Test
    @DisplayName("POST /api/database/connections/register should return 400 for invalid request")
    void testRegisterConnectionValidationFailure() throws Exception {
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
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/database/connections/register should validate port range")
    void testRegisterConnectionInvalidPort() throws Exception {
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
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/database/connections/{id} should update connection")
    void testUpdateConnection() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("name", "updated-name");
        request.put("host", "newhost");
        request.put("port", 5432);
        request.put("poolSize", 20);
        request.put("properties", new HashMap<>());

        DatabaseConnection updatedConnection = new DatabaseConnection(
            "conn-1",
            "updated-name",
            DatabaseConnection.DatabaseType.POSTGRESQL,
            "newhost",
            5432,
            "testdb",
            "user",
            false
        );

        when(commandService.updateConnection(eq("conn-1"), any())).thenReturn(updatedConnection);

        mockMvc.perform(put("/api/database/connections/conn-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("updated-name"))
            .andExpect(jsonPath("$.host").value("newhost"));
    }

    @Test
    @DisplayName("DELETE /api/database/connections/{id} should return 204 No Content")
    void testRemoveConnection() throws Exception {
        mockMvc.perform(delete("/api/database/connections/conn-1"))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("POST /api/database/connections/{id}/health should perform health check")
    void testCheckHealth() throws Exception {
        when(queryService.performHealthCheck("conn-1")).thenReturn(healthCheckResult);

        mockMvc.perform(post("/api/database/connections/conn-1/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.connectionId").value("conn-1"))
            .andExpect(jsonPath("$.healthy").value(true))
            .andExpect(jsonPath("$.latencyMs").value(150));
    }

    @Test
    @DisplayName("POST /api/database/backups/create should create backup and return 201")
    void testCreateBackup() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("connectionId", "conn-1");
        request.put("backupName", "backup-1");
        request.put("type", "FULL");
        request.put("storageLocation", "/backups");

        BackupInfo backupInfo = new BackupInfo(
            "backup-1",
            "conn-1",
            BackupInfo.BackupType.FULL,
            "/backups",
            1024000L,
            BackupInfo.BackupStatus.COMPLETED
        );

        when(commandService.createBackup(any())).thenReturn(backupInfo);

        mockMvc.perform(post("/api/database/backups/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.backupId").value("backup-1"))
            .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    @DisplayName("POST /api/database/backups/create should validate required fields")
    void testCreateBackupValidationFailure() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("connectionId", ""); // Invalid: blank
        request.put("backupName", "");  // Invalid: blank
        request.put("type", "FULL");
        request.put("storageLocation", "/backups");

        mockMvc.perform(post("/api/database/backups/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/database/backups/{backupId}/cancel should return 202 Accepted")
    void testCancelBackup() throws Exception {
        mockMvc.perform(post("/api/database/backups/backup-1/cancel"))
            .andExpect(status().isAccepted());
    }
}
