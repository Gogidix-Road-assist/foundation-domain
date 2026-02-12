package com.gogidix.rapidassist.maps.geocoding.adapter.service.adapters.in.web;

import com.gogidix.rapidassist.maps.geocoding.adapter.service.domain.port.in.GetStatusQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StatusController.class)
class StatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetStatusQuery getStatusQuery;

    @Test
    void status_ReturnsServiceStatus() throws Exception {
        when(getStatusQuery.getStatus()).thenReturn("Maps Geocoding Adapter Service is operational");

        mockMvc.perform(get("/status"))
                .andExpect(status().isOk())
                .andExpect(content().string("Maps Geocoding Adapter Service is operational"));
    }

    @Test
    void status_ReturnsDifferentStatusMessage() throws Exception {
        when(getStatusQuery.getStatus()).thenReturn("Service is starting");

        mockMvc.perform(get("/status"))
                .andExpect(status().isOk())
                .andExpect(content().string("Service is starting"));
    }
}
