package com.gogidix.rapidassist.insurer.adapter.service.adapters.in.web;

import com.gogidix.rapidassist.insurer.adapter.service.domain.port.in.GetStatusQuery;
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
    void status_ReturnsOk() throws Exception {
        when(getStatusQuery.getStatus()).thenReturn("UP");

        mockMvc.perform(get("/status"))
                .andExpect(status().isOk())
                .andExpect(content().string("UP"));
    }

    @Test
    void status_ReturnsServiceStatus() throws Exception {
        when(getStatusQuery.getStatus()).thenReturn("INSURER_ADAPTER_SERVICE_RUNNING");

        mockMvc.perform(get("/status"))
                .andExpect(status().isOk())
                .andExpect(content().string("INSURER_ADAPTER_SERVICE_RUNNING"));
    }
}
