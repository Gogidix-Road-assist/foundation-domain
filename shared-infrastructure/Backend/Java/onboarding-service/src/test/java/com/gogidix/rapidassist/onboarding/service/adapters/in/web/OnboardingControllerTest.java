package com.gogidix.rapidassist.onboarding.service.adapters.in.web;

import com.gogidix.rapidassist.onboarding.service.domain.model.OnboardingRecord;
import com.gogidix.rapidassist.onboarding.service.domain.model.OnboardingStatus;
import com.gogidix.rapidassist.onboarding.service.domain.port.in.GetOnboardingStatusQuery;
import com.gogidix.rapidassist.onboarding.service.domain.port.in.StartOnboardingCommand;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContext;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OnboardingController.class)
class OnboardingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StartOnboardingCommand startOnboardingCommand;

    @MockBean
    private GetOnboardingStatusQuery getOnboardingStatusQuery;

    private RequestContext requestContext;

    @BeforeEach
    void setUp() {
        requestContext = new RequestContext("corr-456", "US", "tenant-123", "user-789", null);
        RequestContextHolder.set(requestContext);
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.clear();
    }

    @Test
    @WithMockUser(username = "user123")
    void start_Onboarding_Success() throws Exception {
        OnboardingRecord record = new OnboardingRecord("tenant-123", "user123", OnboardingStatus.STARTED, Instant.now());
        when(startOnboardingCommand.start("tenant-123", "user123")).thenReturn(record);

        mockMvc.perform(post("/api/v1/onboarding/start")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subject").value("user123"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

        verify(startOnboardingCommand).start("tenant-123", "user123");
    }

    @Test
    @WithMockUser(username = "user123")
    void status_OnboardingFound_Success() throws Exception {
        OnboardingRecord record = new OnboardingRecord("tenant-123", "user123", OnboardingStatus.COMPLETED, Instant.now());
        when(getOnboardingStatusQuery.get("tenant-123", "user123")).thenReturn(record);

        mockMvc.perform(get("/api/v1/onboarding/status")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subject").value("user123"))
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        verify(getOnboardingStatusQuery).get("tenant-123", "user123");
    }

    @Test
    @WithMockUser(username = "user123")
    void status_OnboardingNotFound_Returns404() throws Exception {
        when(getOnboardingStatusQuery.get("tenant-123", "user123")).thenReturn(null);

        mockMvc.perform(get("/api/v1/onboarding/status")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(getOnboardingStatusQuery).get("tenant-123", "user123");
    }

    @Test
    void start_NoAuthentication_Returns401() throws Exception {
        RequestContextHolder.clear();

        mockMvc.perform(post("/api/v1/onboarding/start")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        verify(startOnboardingCommand, never()).start(anyString(), anyString());
    }

    @Test
    @WithMockUser(username = "user123")
    void start_NoTenantId_Returns401() throws Exception {
        RequestContextHolder.clear();

        mockMvc.perform(post("/api/v1/onboarding/start")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        verify(startOnboardingCommand, never()).start(anyString(), anyString());
    }
}
