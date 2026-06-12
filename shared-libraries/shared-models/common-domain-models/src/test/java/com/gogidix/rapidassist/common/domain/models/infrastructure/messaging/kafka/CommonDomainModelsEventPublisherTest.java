package com.gogidix.rapidassist.common.domain.models.infrastructure.messaging.kafka;

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
class CommonDomainModelsEventPublisherTest {

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

        String topic = "common-domain-models.events";
        var result = kafkaTemplate.send(topic, "test-key", Map.of(
            "eventType", "TEST_EVENT",
            "service", "common-domain-models",
            "timestamp", java.time.Instant.now().toString()
        ));

        assertDoesNotThrow(() -> result.get());
    }
}