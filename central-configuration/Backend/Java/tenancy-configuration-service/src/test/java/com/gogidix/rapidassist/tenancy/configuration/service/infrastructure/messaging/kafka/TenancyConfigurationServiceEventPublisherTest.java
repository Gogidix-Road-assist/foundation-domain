package com.gogidix.rapidassist.tenancy.configuration.service.infrastructure.messaging.kafka;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TenancyConfigurationServiceEventPublisherTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    void kafkaTemplateIsAvailable() {
        assertNotNull(kafkaTemplate);
    }

    @Test
    void canPublishEventToTopic() {
        CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(null);
        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(future);

        String topic = "tenancy-configuration-service.events";
        var result = kafkaTemplate.send(topic, "test-key", Map.of(
            "eventType", "TEST_EVENT",
            "service", "tenancy-configuration-service",
            "timestamp", java.time.Instant.now().toString()
        ));

        assertDoesNotThrow(() -> result.get());
    }
}