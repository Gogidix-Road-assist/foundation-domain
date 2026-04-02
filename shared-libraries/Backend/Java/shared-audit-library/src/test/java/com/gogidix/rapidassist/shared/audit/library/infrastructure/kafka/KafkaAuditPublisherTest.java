package com.gogidix.rapidassist.shared.audit.library.infrastructure.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.rapidassist.shared.audit.library.domain.model.AuditActor;
import com.gogidix.rapidassist.shared.audit.library.domain.model.AuditEntityRef;
import com.gogidix.rapidassist.shared.audit.library.domain.model.AuditEventEnvelope;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for KafkaAuditPublisher.
 */
@ExtendWith(MockitoExtension.class)
class KafkaAuditPublisherTest {

    @Mock
    private KafkaTemplate<String, AuditEventEnvelope> kafkaTemplate;

    private ObjectMapper objectMapper;
    private KafkaAuditPublisher publisher;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        publisher = new KafkaAuditPublisher(kafkaTemplate, objectMapper, "audit-events");
    }

    @Test
    void testPublishSuccess() {
        // Arrange
        Instant now = Instant.now();
        AuditActor actor = new AuditActor("USER", "user-123", "John Doe");
        AuditEntityRef entity = new AuditEntityRef("Customer", "customer-456", "Acme Corp");

        AuditEventEnvelope event = new AuditEventEnvelope(
                "1.0",
                "event-123",
                "USER_CREATED",
                now,
                "corr-456",
                "US",
                "tenant-001",
                "subtenant-001",
                actor,
                entity,
                null,
                Map.of("name", "John Doe")
        );

        @SuppressWarnings("unchecked")
        SendResult<String, AuditEventEnvelope> sendResult = mock(SendResult.class);
        CompletableFuture<SendResult<String, AuditEventEnvelope>> future =
                CompletableFuture.completedFuture(sendResult);

        when(kafkaTemplate.send(eq("audit-events"), any(String.class), eq(event)))
                .thenReturn(future);

        // Act
        publisher.publish(event);

        // Assert
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate, times(1)).send(eq("audit-events"), keyCaptor.capture(), eq(event));

        String key = keyCaptor.getValue();
        assertEquals("tenant-001|Customer|customer-456", key);
    }

    @Test
    void testPublishWithSendFailure() {
        // Arrange
        Instant now = Instant.now();
        AuditActor actor = new AuditActor("USER", "user-123", "John Doe");
        AuditEntityRef entity = new AuditEntityRef("Customer", "customer-456", "Acme Corp");

        AuditEventEnvelope event = new AuditEventEnvelope(
                "1.0",
                "event-123",
                "USER_CREATED",
                now,
                "corr-456",
                "US",
                "tenant-001",
                null,
                actor,
                entity,
                null,
                Map.of("name", "John Doe")
        );

        CompletableFuture<SendResult<String, AuditEventEnvelope>> failedFuture =
                new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Kafka error"));

        when(kafkaTemplate.send(eq("audit-events"), any(String.class), eq(event)))
                .thenReturn(failedFuture);

        // Act - should not throw exception
        assertDoesNotThrow(() -> publisher.publish(event));

        // Assert - still attempted to send
        verify(kafkaTemplate, times(1)).send(eq("audit-events"), any(String.class), eq(event));
    }

    @Test
    void testPublishWithKafkaException() {
        // Arrange
        Instant now = Instant.now();
        AuditActor actor = new AuditActor("USER", "user-123", "John Doe");
        AuditEntityRef entity = new AuditEntityRef("Customer", "customer-456", "Acme Corp");

        AuditEventEnvelope event = new AuditEventEnvelope(
                "1.0",
                "event-123",
                "USER_CREATED",
                now,
                "corr-456",
                "US",
                "tenant-001",
                null,
                actor,
                entity,
                null,
                Map.of("name", "John Doe")
        );

        when(kafkaTemplate.send(eq("audit-events"), any(String.class), eq(event)))
                .thenThrow(new RuntimeException("Kafka template error"));

        // Act - should not throw exception
        assertDoesNotThrow(() -> publisher.publish(event));

        // Assert - still attempted to send
        verify(kafkaTemplate, times(1)).send(eq("audit-events"), any(String.class), eq(event));
    }

    @Test
    void testGetTopic() {
        assertEquals("audit-events", publisher.getTopic());

        KafkaAuditPublisher customPublisher =
                new KafkaAuditPublisher(kafkaTemplate, objectMapper, "custom-topic");
        assertEquals("custom-topic", customPublisher.getTopic());
    }

    @Test
    void testMessageKeyFormat() {
        // Arrange
        Instant now = Instant.now();
        AuditActor actor = new AuditActor("USER", "user-123", "John Doe");
        AuditEntityRef entity = new AuditEntityRef("Provider", "provider-789", "ABC Provider");

        AuditEventEnvelope event = new AuditEventEnvelope(
                "1.0",
                "event-456",
                "PROVIDER_UPDATED",
                now,
                "corr-789",
                "CA",
                "tenant-002",
                null,
                actor,
                entity,
                null,
                Map.of("name", "ABC Provider")
        );

        @SuppressWarnings("unchecked")
        SendResult<String, AuditEventEnvelope> sendResult = mock(SendResult.class);
        CompletableFuture<SendResult<String, AuditEventEnvelope>> future =
                CompletableFuture.completedFuture(sendResult);

        when(kafkaTemplate.send(eq("audit-events"), any(String.class), eq(event)))
                .thenReturn(future);

        // Act
        publisher.publish(event);

        // Assert
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate, times(1)).send(eq("audit-events"), keyCaptor.capture(), eq(event));

        String key = keyCaptor.getValue();
        assertEquals("tenant-002|Provider|provider-789", key);
    }
}
