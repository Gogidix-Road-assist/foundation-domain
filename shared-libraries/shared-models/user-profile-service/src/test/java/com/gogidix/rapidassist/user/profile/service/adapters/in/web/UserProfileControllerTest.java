package com.gogidix.rapidassist.user.profile.service.adapters.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.rapidassist.user.profile.service.application.service.ComprehensiveUserProfileService;
import com.gogidix.rapidassist.user.profile.service.domain.model.UserProfile;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContext;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(UserProfileController.class)
class UserProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ComprehensiveUserProfileService userProfileService;

    private UserProfile sampleProfile;

    @BeforeEach
    void setUp() {
        RequestContextHolder.set(new RequestContext("corr-1", "US", "tenant-1", "user-1", "req-1"));
        sampleProfile = new UserProfile(
                null,
                "user-123",
                "tenant-123",
                "john@example.com",
                "John",
                "Doe",
                "+1234567890",
                "https://example.com/avatar.jpg",
                new UserProfile.UserPreferences(
                        true,
                        "light",
                        "en"
                ),
                new UserProfile.UserSettings(
                        true,
                        true,
                        true,
                        Map.of()
                ),
                Set.of("ADMIN", "USER"),
                Set.of("READ", "WRITE"),
                UserProfile.UserStatus.ACTIVE,
                Map.of("department", "engineering"),
                "admin@example.com",
                Instant.now(),
                "admin@example.com",
                Instant.now(),
                Instant.now()
        );
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.clear();
    }

    @Test
    void createProfile_ShouldReturnCreated() throws Exception {
        when(userProfileService.createProfile(any()))
                .thenReturn(CompletableFuture.completedFuture(sampleProfile));

        mockMvc.perform(post("/api/v1/user-profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "tenantId", "tenant-123",
                                "userId", "user-123",
                                "email", "john@example.com",
                                "firstName", "John",
                                "lastName", "Doe",
                                "createdBy", "admin@example.com"
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value("user-123"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void getProfile_ShouldReturnProfile() throws Exception {
        when(userProfileService.getProfileByUserId(any(), any()))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(sampleProfile)));

        mockMvc.perform(get("/api/v1/user-profile/user-123")
                        .param("tenantId", "tenant-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("user-123"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void getProfile_WhenNotFound_ShouldReturn404() throws Exception {
        when(userProfileService.getProfileByUserId(any(), any()))
                .thenReturn(CompletableFuture.completedFuture(Optional.empty()));

        mockMvc.perform(get("/api/v1/user-profile/nonexistent")
                        .param("tenantId", "tenant-123"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateProfile_ShouldReturnUpdated() throws Exception {
        when(userProfileService.updateProfile(any()))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(sampleProfile)));

        mockMvc.perform(put("/api/v1/user-profile/user-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "tenantId", "tenant-123",
                                "email", "updated@example.com",
                                "firstName", "John",
                                "lastName", "Doe",
                                "updatedBy", "admin@example.com"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("user-123"));
    }

    @Test
    void deleteProfile_ShouldReturnNoContent() throws Exception {
        when(userProfileService.deleteProfile(any(), any(), any()))
                .thenReturn(CompletableFuture.completedFuture(true));

        mockMvc.perform(delete("/api/v1/user-profile/user-123")
                        .param("tenantId", "tenant-123")
                        .param("deletedBy", "admin@example.com"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getProfilesByTenant_ShouldReturnList() throws Exception {
        when(userProfileService.getProfilesByTenant(any()))
                .thenReturn(CompletableFuture.completedFuture(List.of(sampleProfile)));

        mockMvc.perform(get("/api/v1/user-profile")
                        .param("tenantId", "tenant-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value("user-123"));
    }

    @Test
    void getActiveProfiles_ShouldReturnActiveOnly() throws Exception {
        when(userProfileService.getActiveProfiles(any()))
                .thenReturn(CompletableFuture.completedFuture(List.of(sampleProfile)));

        mockMvc.perform(get("/api/v1/user-profile/active")
                        .param("tenantId", "tenant-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));
    }
}
