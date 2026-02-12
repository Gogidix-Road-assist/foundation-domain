package com.gogidix.rapidassist.data.privacy.consent.service.adapters.in.web;

import com.gogidix.rapidassist.data.privacy.consent.service.domain.model.ConsentPreferences;
import com.gogidix.rapidassist.data.privacy.consent.service.domain.port.in.GetConsentPreferencesQuery;
import com.gogidix.rapidassist.data.privacy.consent.service.domain.port.in.UpsertConsentPreferencesCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ConsentControllerTest {

    @Mock
    private GetConsentPreferencesQuery getConsentPreferencesQuery;

    @Mock
    private UpsertConsentPreferencesCommand upsertConsentPreferencesCommand;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        ConsentController controller = new ConsentController(getConsentPreferencesQuery, upsertConsentPreferencesCommand);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void me_ShouldReturnConsentPreferences() throws Exception {
        ConsentPreferences preferences = new ConsentPreferences("user-123", true, false, LocalDateTime.now());
        when(getConsentPreferencesQuery.get(anyString(), anyString())).thenReturn(preferences);

        mockMvc.perform(get("/api/v1/consent/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subject").value("user-123"))
                .andExpect(jsonPath("$.termsAccepted").value(true));
    }

    @Test
    void upsert_ShouldUpdateConsentPreferences() throws Exception {
        ConsentPreferences preferences = new ConsentPreferences("user-123", true, true, LocalDateTime.now());
        when(upsertConsentPreferencesCommand.upsert(anyString(), anyString(), any(), any())).thenReturn(preferences);

        mockMvc.perform(post("/api/v1/consent/me")
                        .contentType("application/json")
                        .content("{\"termsAccepted\":true,\"marketingEmails\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subject").value("user-123"))
                .andExpect(jsonPath("$.marketingEmails").value(true));
    }
}
