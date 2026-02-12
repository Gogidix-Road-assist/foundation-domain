package com.gogidix.rapidassist.onboarding.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Smoke Test: ContextLoadsTest
 *
 * Verifies that the Spring application context loads successfully.
 * This is a critical test that ensures all beans are wired correctly.
 */
@SpringBootTest
@Testcontainers
@DisplayName("Application Context Loading Test")
class ContextLoadsTest {

    @Test
    @DisplayName("Application context should load successfully")
    void contextLoads() {
        // If this test passes, the Spring context loaded successfully
        // This means all beans were created and wired correctly
    }
}
