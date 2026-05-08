package com.gogidix.rapidassist.feature.flags.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration"
})
@EnableAutoConfiguration(exclude = {RedisAutoConfiguration.class})
class ContextLoadsTest {

    @Test
    void contextLoads() {
    }
}
