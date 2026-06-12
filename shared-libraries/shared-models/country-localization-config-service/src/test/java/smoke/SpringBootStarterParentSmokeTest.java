package com.gogidix.rapidassist.country.localization.config.service.smoke;

import com.gogidix.rapidassist.country.localization.config.service.config.TestSecurityConfiguration;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Disabled("Requires MongoDB - run in smoke-test stage")
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfiguration.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
@DisplayName("SpringBootStarterParent Smoke Tests")
public class SpringBootStarterParentSmokeTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Order(1)
    @DisplayName("Health endpoint should return UP status")
    public void healthEndpoint_returnsUP() throws Exception {
        mockMvc.perform(get("/api/v1/actuator/health"))
            .andExpect(status().isOk())
            ;
    }

    @Test
    @Order(2)
    @DisplayName("Service info endpoint should respond")
    public void infoEndpoint_responds() throws Exception {
        mockMvc.perform(get("/api/v1/actuator/info"))
            .andExpect(status().isOk());
    }
}
