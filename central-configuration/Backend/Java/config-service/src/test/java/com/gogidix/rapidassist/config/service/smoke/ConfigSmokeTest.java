package com.gogidix.rapidassist.config.service.smoke;

import com.gogidix.rapidassist.config.service.adapters.in.web.TestSecurityConfig;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Smoke tests for config-service.
 * These tests verify basic functionality of the application.
 */
@SpringBootTest
@Import(TestSecurityConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
@DisplayName("Config Smoke Tests")
public class ConfigSmokeTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    @Order(1)
    @DisplayName("Application context should load successfully")
    public void applicationContext_loads() {
        assertNotNull(applicationContext, "Application context should be loaded");
        assertTrue(applicationContext.getBeanDefinitionNames().length > 0, "Application context should contain beans");
    }

    @Test
    @Order(2)
    @DisplayName("Required beans should be present")
    public void requiredBeans_present() {
        assertNotNull(applicationContext.getBean("configurationController"), "ConfigurationController should be present");
        assertNotNull(applicationContext.getBean("comprehensiveConfigurationService"), "ComprehensiveConfigurationService should be present");
    }
}
