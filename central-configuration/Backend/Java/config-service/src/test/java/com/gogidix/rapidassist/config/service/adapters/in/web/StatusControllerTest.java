package com.gogidix.rapidassist.config.service.adapters.in.web;

import com.gogidix.rapidassist.config.service.domain.port.in.GetStatusQuery;
import org.junit.jupiter.api.BeforeEach;
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

@ExtendWith(MockitoExtension.class)
class StatusControllerTest {

    @Mock
    private GetStatusQuery getStatusQuery;

    @InjectMocks
    private StatusController statusController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(statusController).build();
    }

    @Test
    void status_ReturnsStatusFromQuery() throws Exception {
        when(getStatusQuery.getStatus()).thenReturn("UP");

        mockMvc.perform(get("/status"))
                .andExpect(status().isOk())
                .andExpect(content().string("UP"));
    }

    @Test
    void status_WithDegradedService_ReturnsDegradedStatus() throws Exception {
        when(getStatusQuery.getStatus()).thenReturn("DEGRADED");

        mockMvc.perform(get("/status"))
                .andExpect(status().isOk())
                .andExpect(content().string("DEGRADED"));
    }

    @Test
    void status_WithDownService_ReturnsDownStatus() throws Exception {
        when(getStatusQuery.getStatus()).thenReturn("DOWN");

        mockMvc.perform(get("/status"))
                .andExpect(status().isOk())
                .andExpect(content().string("DOWN"));
    }
}
