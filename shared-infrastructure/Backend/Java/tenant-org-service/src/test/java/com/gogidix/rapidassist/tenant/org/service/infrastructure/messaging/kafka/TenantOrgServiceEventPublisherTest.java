package com.gogidix.rapidassist.tenant.org.service.infrastructure.messaging.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"tenant-org-service.events"})
@DirtiesContext
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration,org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration,org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration"
})
@Timeout(30)
class TenantOrgServiceEventPublisherTest {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    void kafkaTemplateIsAvailable() {
        assertNotNull(kafkaTemplate);
    }

    @Test
    void canPublishEventToTopic() {
        String topic = "tenant-org-service.events";
        var future = kafkaTemplate.send(topic, "test-key", Map.of(
            "eventType", "TEST_EVENT",
            "service", "tenant-org-service",
            "timestamp", java.time.Instant.now().toString()
        ));
        assertDoesNotThrow(() -> future.get());
    }
}