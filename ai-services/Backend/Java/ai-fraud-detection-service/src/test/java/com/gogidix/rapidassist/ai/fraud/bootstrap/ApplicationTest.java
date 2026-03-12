package com.gogidix.rapidassist.ai.fraud.bootstrap;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test class for Application bootstrap
 *
 * This test verifies that the Spring application context loads correctly.
 */
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
class ApplicationTest {

    @Test
    void contextLoads() {
        // Test verifies that the Spring application context can be loaded
        // If there are any configuration issues, this test will fail
        assertNotNull(Application.class);
    }

    @Test
    void application_hasMainMethod() throws NoSuchMethodException {
        // Verify that the main method exists
        assertNotNull(Application.class.getMethod("main", String[].class));
    }
}
