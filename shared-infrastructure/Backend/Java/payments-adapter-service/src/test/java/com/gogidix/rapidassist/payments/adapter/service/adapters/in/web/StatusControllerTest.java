package com.gogidix.rapidassist.payments.adapter.service.adapters.in.web;

import com.gogidix.rapidassist.payments.adapter.service.domain.port.in.GetStatusQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for StatusController.
 */
@ExtendWith(MockitoExtension.class)
class StatusControllerTest {

    @Mock
    private GetStatusQuery getStatusQuery;

    @InjectMocks
    private StatusController statusController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(statusController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET /status - Should return service status")
    void status_ShouldReturnStatus() throws Exception {
        // Given
        when(getStatusQuery.getStatus()).thenReturn("UP");

        // When & Then
        mockMvc.perform(get("/status"))
                .andExpect(status().isOk())
                .andExpect(content().string("UP"));
    }

    @Test
    @DisplayName("GET /status - Should return DOWN status")
    void status_WhenServiceIsDown_ShouldReturnDown() throws Exception {
        // Given
        when(getStatusQuery.getStatus()).thenReturn("DOWN");

        // When & Then
        mockMvc.perform(get("/status"))
                .andExpect(status().isOk())
                .andExpect(content().string("DOWN"));
    }
}
