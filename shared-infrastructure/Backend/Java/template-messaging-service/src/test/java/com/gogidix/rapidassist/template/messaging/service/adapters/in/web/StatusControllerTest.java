package com.gogidix.rapidassist.template.messaging.service.adapters.in.web;

import com.gogidix.rapidassist.template.messaging.service.domain.port.in.GetStatusQuery;
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

    @Test
    void status_ReturnsStatus() throws Exception {
        when(getStatusQuery.getStatus()).thenReturn("OK");

        mockMvc.perform(get("/status"))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));
    }

    @Test
    void status_ReturnsCustomStatus() throws Exception {
        when(getStatusQuery.getStatus()).thenReturn("RUNNING");

        mockMvc.perform(get("/status"))
                .andExpect(status().isOk())
                .andExpect(content().string("RUNNING"));
    }
}
