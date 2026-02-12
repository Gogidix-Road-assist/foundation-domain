package com.gogidix.rapidassist.webhook.delivery.service.adapters.in.web;

import com.gogidix.rapidassist.webhook.delivery.service.domain.port.in.GetStatusQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StatusController.class)
class StatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetStatusQuery getStatusQuery;

    @BeforeEach
    void setUp() {
        when(getStatusQuery.getStatus()).thenReturn("Webhook Delivery Service is operational");
    }

    @Test
    void status_ShouldReturnServiceStatus() throws Exception {
        mockMvc.perform(get("/status"))
                .andExpect(status().isOk())
                .andExpect(content().string("Webhook Delivery Service is operational"));
    }

    @Test
    void status_WithDifferentMessage_ShouldReturnCustomMessage() throws Exception {
        when(getStatusQuery.getStatus()).thenReturn("Service is healthy");

        mockMvc.perform(get("/status"))
                .andExpect(status().isOk())
                .andExpect(content().string("Service is healthy"));
    }
}
