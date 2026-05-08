package com.gogidix.rapidassist.integration.adapters.service.adapters.in.web;

import com.gogidix.rapidassist.integration.adapters.service.domain.port.in.GetStatusQuery;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContext;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(StatusController.class)
class StatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetStatusQuery getStatusQuery;

    @BeforeEach
    void setUp() {
        RequestContextHolder.set(new RequestContext("corr-1", "US", "tenant-1", "user-1", "req-1"));
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.clear();
    }

    @Test
    void status_ReturnsOk() throws Exception {
        when(getStatusQuery.getStatus()).thenReturn("UP");

        mockMvc.perform(get("/status"))
                .andExpect(status().isOk())
                .andExpect(content().string("UP"));
    }

    @Test
    void status_ReturnsServiceStatus() throws Exception {
        when(getStatusQuery.getStatus()).thenReturn("INTEGRATION_ADAPTERS_SERVICE_RUNNING");

        mockMvc.perform(get("/status"))
                .andExpect(status().isOk())
                .andExpect(content().string("INTEGRATION_ADAPTERS_SERVICE_RUNNING"));
    }
}
