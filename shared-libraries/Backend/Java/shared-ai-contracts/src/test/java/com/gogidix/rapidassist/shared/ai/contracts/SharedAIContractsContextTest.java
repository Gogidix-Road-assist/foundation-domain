package com.gogidix.rapidassist.shared.ai.contracts;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Spring context test for Shared AI Contracts library.
 * Verifies that the Spring context loads correctly.
 */
@SpringBootTest(classes = SharedAIContractsApplication.class)
@ActiveProfiles("test")
class SharedAIContractsContextTest {

    @Test
    void contextLoads() {
        // If this test runs, the Spring context loaded successfully
    }
}
