package com.gogidix.rapidassist.courier.adapter.service.adapters.in.web;

import com.gogidix.rapidassist.courier.adapter.service.domain.port.in.GetStatusQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        StatusController controller = new StatusController(getStatusQuery);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void status_ShouldReturnServiceStatus() throws Exception {
        when(getStatusQuery.getStatus()).thenReturn("UP");

        mockMvc.perform(get("/status"))
                .andExpect(status().isOk())
                .andExpect(content().string("UP"));
    }
}
