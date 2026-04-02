package com.gogidix.rapidassist.access.control.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

/**
 * Smoke Test: ContextLoadsTest
 *
 * Verifies that the Spring application context loads successfully.
 * This is a critical test that ensures all beans are wired correctly.
 */
@SpringBootTest
@Testcontainers
@Disabled("Docker required - run in cloud CI/CD")
@DisplayName("Application Context Loading Test")
class ContextLoadsTest {

    @Container
    static final MongoDBContainer mongoDB = new MongoDBContainer(
            DockerImageName.parse("mongo:6"))
            .withReuse(true);

    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDB::getReplicaSetUrl);
    }

    @Test
    @DisplayName("Application context should load successfully")
    void contextLoads() {
        // If this test passes, the Spring context loaded successfully
        // This means all beans were created and wired correctly
    }
}
