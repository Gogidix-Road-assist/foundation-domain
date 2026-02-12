package com.gogidix.rapidassist.identity.access.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestRepositoryConfig.class)
class ContextLoadsTest {

    @Test
    void contextLoads() {
    }
}
