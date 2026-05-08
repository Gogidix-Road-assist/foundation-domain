package com.gogidix.rapidassist.config.service.infrastructure.messaging.kafka;

import com.gogidix.rapidassist.config.service.domain.event.ConfigurationCreatedEvent;
import com.gogidix.rapidassist.config.service.domain.event.ConfigurationDeletedEvent;
import com.gogidix.rapidassist.config.service.domain.event.ConfigurationUpdatedEvent;
import com.gogidix.rapidassist.config.service.domain.model.Configuration;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.SettableListenableFuture;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ConfigurationEventPublisher.
 * Tests Kafka event publishing functionality including success and failure scenarios.
 */
@ExtendWith(MockitoExtension.class)
class ConfigurationEventPublisherTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    private ConfigurationEventPublisher publisher;
    private static final String TEST_TOPIC = "configuration.events";

    @BeforeEach
    void setUp() {
        publisher = new ConfigurationEventPublisher(kafkaTemplate, TEST_TOPIC);
    }

    private SendResult<String, Object> createMockSendResult(long offset) {
        @SuppressWarnings("unchecked")
        SendResult<String, Object> mockSendResult = mock(SendResult.class);
        RecordMetadata mockMetadata = mock(RecordMetadata.class);
        when(mockSendResult.getRecordMetadata()).thenReturn(mockMetadata);
        when(mockMetadata.offset()).thenReturn(offset);
        return mockSendResult;
    }

    private Configuration createTestConfiguration(String id, String tenantId, String configKey) {
        return new Configuration(
            id,
            tenantId,
            configKey,
            "production",
            "default",
            1,
            "test-value",
            Configuration.ConfigurationDataType.STRING,
            false,
            false,
            null,
            "Test configuration",
            Set.of("test"),
            Map.of("owner", "team-a"),
            null,
            Configuration.ConfigurationStatus.ACTIVE,
            "test-user",
            Instant.now(),
            "test-user",
            Instant.now(),
            null,
            Set.of()
        );
    }

    @Nested
    class PublishConfigurationCreatedEventTests {

        @Test
        void publishConfigurationCreated_WithValidEvent_CallsKafkaTemplate() {
            // Arrange
            Configuration config = createTestConfiguration("config-id-1", "tenant-1", "api.timeout");
            ConfigurationCreatedEvent event = new ConfigurationCreatedEvent(config, "test-user");

            @SuppressWarnings("unchecked")
            SendResult<String, Object> mockSendResult = mock(SendResult.class);
            RecordMetadata mockMetadata = mock(RecordMetadata.class);
            when(mockSendResult.getRecordMetadata()).thenReturn(mockMetadata);
            when(mockMetadata.offset()).thenReturn(123L);

            CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(mockSendResult);
            when(kafkaTemplate.send(eq(TEST_TOPIC), any(String.class), any(ConfigurationCreatedEvent.class)))
                .thenReturn(future);

            // Act
            CompletableFuture<Void> result = publisher.publishConfigurationCreated(event);

            // Assert
            assertNotNull(result);
            verify(kafkaTemplate, times(1)).send(
                eq(TEST_TOPIC),
                eq("tenant-1:config-id-1"),
                any(ConfigurationCreatedEvent.class)
            );
        }

        @Test
        void publishConfigurationCreated_WithDifferentTenants_UsesCorrectKeys() throws Exception {
            // Arrange
            Configuration config1 = createTestConfiguration("config-1", "tenant-1", "config1");
            Configuration config2 = createTestConfiguration("config-2", "tenant-2", "config2");

            ConfigurationCreatedEvent event1 = new ConfigurationCreatedEvent(config1, "user1");
            ConfigurationCreatedEvent event2 = new ConfigurationCreatedEvent(config2, "user2");

            @SuppressWarnings("unchecked")
            SendResult<String, Object> mockSendResult = mock(SendResult.class);
            RecordMetadata mockMetadata = mock(RecordMetadata.class);
            when(mockSendResult.getRecordMetadata()).thenReturn(mockMetadata);
            when(mockMetadata.offset()).thenReturn(1L);

            CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(mockSendResult);
            when(kafkaTemplate.send(eq(TEST_TOPIC), any(String.class), any(ConfigurationCreatedEvent.class)))
                .thenReturn(future);

            // Act
            publisher.publishConfigurationCreated(event1).join();
            publisher.publishConfigurationCreated(event2).join();

            // Assert
            ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);

            verify(kafkaTemplate, times(2)).send(
                eq(TEST_TOPIC),
                keyCaptor.capture(),
                any(ConfigurationCreatedEvent.class)
            );

            assertEquals("tenant-1:config-1", keyCaptor.getAllValues().get(0));
            assertEquals("tenant-2:config-2", keyCaptor.getAllValues().get(1));
        }

        @Test
        void publishConfigurationCreated_WhenKafkaFails_ThrowsException() {
            // Arrange
            Configuration config = createTestConfiguration("config-id", "tenant-1", "api.timeout");
            ConfigurationCreatedEvent event = new ConfigurationCreatedEvent(config, "test-user");

            CompletableFuture<SendResult<String, Object>> failedFuture = new CompletableFuture<>();
            failedFuture.completeExceptionally(new RuntimeException("Kafka connection failed"));

            when(kafkaTemplate.send(eq(TEST_TOPIC), any(String.class), any(ConfigurationCreatedEvent.class)))
                .thenReturn(failedFuture);

            // Act & Assert
            CompletableFuture<Void> result = publisher.publishConfigurationCreated(event);

            Exception exception = assertThrows(RuntimeException.class, () -> result.join());
            assertTrue(exception.getMessage().contains("Failed to publish event to Kafka"));
        }
    }

    @Nested
    class PublishConfigurationUpdatedEventTests {

        @Test
        void publishConfigurationUpdated_WithValidEvent_PublishesSuccessfully() throws Exception {
            // Arrange
            Configuration oldConfig = createTestConfiguration("config-id-1", "tenant-1", "api.timeout")
                .withValue(30);
            Configuration newConfig = createTestConfiguration("config-id-1", "tenant-1", "api.timeout")
                .withValue(60).withVersion(2);

            ConfigurationUpdatedEvent event = new ConfigurationUpdatedEvent(
                oldConfig, newConfig, "test-user", "Increase timeout"
            );

            @SuppressWarnings("unchecked")
            SendResult<String, Object> mockSendResult = mock(SendResult.class);
            RecordMetadata mockMetadata = mock(RecordMetadata.class);
            when(mockSendResult.getRecordMetadata()).thenReturn(mockMetadata);
            when(mockMetadata.offset()).thenReturn(456L);

            CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(mockSendResult);
            when(kafkaTemplate.send(eq(TEST_TOPIC), any(String.class), any(ConfigurationUpdatedEvent.class)))
                .thenReturn(future);

            // Act
            CompletableFuture<Void> result = publisher.publishConfigurationUpdated(event);

            // Assert
            assertNotNull(result);
            result.join();

            ArgumentCaptor<ConfigurationUpdatedEvent> eventCaptor = ArgumentCaptor.forClass(ConfigurationUpdatedEvent.class);

            verify(kafkaTemplate, times(1)).send(
                eq(TEST_TOPIC),
                any(String.class),
                eventCaptor.capture()
            );

            ConfigurationUpdatedEvent capturedEvent = eventCaptor.getValue();
            assertEquals("api.timeout", capturedEvent.configKey());
            assertEquals(30, capturedEvent.previousValue());
            assertEquals(60, capturedEvent.newValue());
            assertEquals(1, capturedEvent.previousVersion());
            assertEquals(2, capturedEvent.newVersion());
            assertEquals("Increase timeout", capturedEvent.reason());
        }

        @Test
        void publishConfigurationUpdated_WithNullReason_PublishesSuccessfully() {
            // Arrange
            Configuration oldConfig = createTestConfiguration("config-id", "tenant-1", "api.timeout");
            Configuration newConfig = createTestConfiguration("config-id", "tenant-1", "api.timeout")
                .withValue("new-value").withVersion(2);

            ConfigurationUpdatedEvent event = new ConfigurationUpdatedEvent(
                oldConfig, newConfig, "test-user", null
            );

            SendResult<String, Object> mockSendResult = createMockSendResult(1L);
            CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(mockSendResult);

            when(kafkaTemplate.send(eq(TEST_TOPIC), any(String.class), any(ConfigurationUpdatedEvent.class)))
                .thenReturn(future);

            // Act
            CompletableFuture<Void> result = publisher.publishConfigurationUpdated(event);
            result.join();

            // Assert
            verify(kafkaTemplate, times(1)).send(
                eq(TEST_TOPIC),
                eq("tenant-1:config-id"),
                any(ConfigurationUpdatedEvent.class)
            );
        }
    }

    @Nested
    class PublishConfigurationDeletedEventTests {

        @Test
        void publishConfigurationDeleted_WithValidEvent_PublishesSuccessfully() throws Exception {
            // Arrange
            ConfigurationDeletedEvent event = new ConfigurationDeletedEvent(
                "config-id-1",
                "tenant-1",
                "api.timeout",
                "production",
                "default",
                1,
                "old-value",
                "test-user",
                "Configuration deprecated"
            );

            @SuppressWarnings("unchecked")
            SendResult<String, Object> mockSendResult = mock(SendResult.class);
            RecordMetadata mockMetadata = mock(RecordMetadata.class);
            when(mockSendResult.getRecordMetadata()).thenReturn(mockMetadata);
            when(mockMetadata.offset()).thenReturn(789L);

            CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(mockSendResult);
            when(kafkaTemplate.send(eq(TEST_TOPIC), any(String.class), any(ConfigurationDeletedEvent.class)))
                .thenReturn(future);

            // Act
            CompletableFuture<Void> result = publisher.publishConfigurationDeleted(event);

            // Assert
            assertNotNull(result);
            result.join();

            ArgumentCaptor<ConfigurationDeletedEvent> eventCaptor = ArgumentCaptor.forClass(ConfigurationDeletedEvent.class);

            verify(kafkaTemplate, times(1)).send(
                eq(TEST_TOPIC),
                any(String.class),
                eventCaptor.capture()
            );

            ConfigurationDeletedEvent capturedEvent = eventCaptor.getValue();
            assertEquals("api.timeout", capturedEvent.configKey());
            assertEquals("old-value", capturedEvent.valueBeforeDeletion());
            assertEquals(1, capturedEvent.versionAtDeletion());
            assertEquals("Configuration deprecated", capturedEvent.reason());
        }

        @Test
        void publishConfigurationDeleted_WithNullReason_PublishesSuccessfully() {
            // Arrange
            ConfigurationDeletedEvent event = new ConfigurationDeletedEvent(
                "config-id",
                "tenant-1",
                "api.timeout",
                "production",
                "default",
                1,
                "value",
                "test-user",
                null
            );

            SendResult<String, Object> mockSendResult = createMockSendResult(1L);
            CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(mockSendResult);

            when(kafkaTemplate.send(eq(TEST_TOPIC), any(String.class), any(ConfigurationDeletedEvent.class)))
                .thenReturn(future);

            // Act
            CompletableFuture<Void> result = publisher.publishConfigurationDeleted(event);
            result.join();

            // Assert
            ArgumentCaptor<ConfigurationDeletedEvent> eventCaptor = ArgumentCaptor.forClass(ConfigurationDeletedEvent.class);

            verify(kafkaTemplate, times(1)).send(
                eq(TEST_TOPIC),
                eq("tenant-1:config-id"),
                eventCaptor.capture()
            );

            assertNull(eventCaptor.getValue().reason());
        }

        @Test
        void publishConfigurationDeleted_WithComplexValue_PublishesSuccessfully() {
            // Arrange
            Map<String, Object> complexValue = Map.of(
                "key1", "value1",
                "key2", 123,
                "nested", Map.of("inner", "value")
            );

            ConfigurationDeletedEvent event = new ConfigurationDeletedEvent(
                "config-id",
                "tenant-1",
                "complex.config",
                "production",
                "default",
                1,
                complexValue,
                "test-user",
                "Remove complex config"
            );

            SendResult<String, Object> mockSendResult = createMockSendResult(2L);
            CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(mockSendResult);

            when(kafkaTemplate.send(eq(TEST_TOPIC), any(String.class), any(ConfigurationDeletedEvent.class)))
                .thenReturn(future);

            // Act
            CompletableFuture<Void> result = publisher.publishConfigurationDeleted(event);
            result.join();

            // Assert
            verify(kafkaTemplate, times(1)).send(
                eq(TEST_TOPIC),
                eq("tenant-1:config-id"),
                any(ConfigurationDeletedEvent.class)
            );
        }
    }

    @Nested
    class PublishGenericEventTests {

        @Test
        void publishEvent_WithCreatedEvent_PublishesSuccessfully() {
            // Arrange
            Configuration config = createTestConfiguration("config-id", "tenant-1", "api.timeout");
            ConfigurationCreatedEvent event = new ConfigurationCreatedEvent(config, "test-user");

            SendResult<String, Object> mockSendResult = createMockSendResult(1L);
            CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(mockSendResult);

            when(kafkaTemplate.send(eq(TEST_TOPIC), any(String.class), any(ConfigurationCreatedEvent.class)))
                .thenReturn(future);

            // Act
            CompletableFuture<Void> result = publisher.publishEvent(event);
            result.join();

            // Assert
            verify(kafkaTemplate, times(1)).send(
                eq(TEST_TOPIC),
                eq("tenant-1:config-id"),
                eq(event)
            );
        }

        @Test
        void publishEvent_WithUpdatedEvent_PublishesSuccessfully() {
            // Arrange
            Configuration oldConfig = createTestConfiguration("config-id", "tenant-1", "api.timeout");
            Configuration newConfig = createTestConfiguration("config-id", "tenant-1", "api.timeout")
                .withVersion(2);
            ConfigurationUpdatedEvent event = new ConfigurationUpdatedEvent(
                oldConfig, newConfig, "test-user", "Update"
            );

            SendResult<String, Object> mockSendResult = createMockSendResult(2L);
            CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(mockSendResult);

            when(kafkaTemplate.send(eq(TEST_TOPIC), any(String.class), any(ConfigurationUpdatedEvent.class)))
                .thenReturn(future);

            // Act
            CompletableFuture<Void> result = publisher.publishEvent(event);
            result.join();

            // Assert
            verify(kafkaTemplate, times(1)).send(
                eq(TEST_TOPIC),
                eq("tenant-1:config-id"),
                eq(event)
            );
        }

        @Test
        void publishEvent_WithDeletedEvent_PublishesSuccessfully() {
            // Arrange
            ConfigurationDeletedEvent event = new ConfigurationDeletedEvent(
                "config-id", "tenant-1", "api.timeout", "production", "default",
                1, "value", "test-user", "Delete"
            );

            SendResult<String, Object> mockSendResult = createMockSendResult(3L);
            CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(mockSendResult);

            when(kafkaTemplate.send(eq(TEST_TOPIC), any(String.class), any(ConfigurationDeletedEvent.class)))
                .thenReturn(future);

            // Act
            CompletableFuture<Void> result = publisher.publishEvent(event);
            result.join();

            // Assert
            verify(kafkaTemplate, times(1)).send(
                eq(TEST_TOPIC),
                eq("tenant-1:config-id"),
                eq(event)
            );
        }
    }

    @Nested
    class KafkaAvailabilityTests {

        @Test
        void isKafkaAvailable_WhenKafkaThrowsException_ReturnsFalse() {
            // Arrange
            when(kafkaTemplate.getProducerFactory()).thenThrow(new RuntimeException("Kafka not available"));

            // Act
            boolean result = publisher.isKafkaAvailable();

            // Assert
            assertFalse(result);
        }
    }

    @Nested
    class ErrorHandlingTests {

        @Test
        void publishConfigurationCreated_WhenKafkaSendThrowsException_PropagatesException() {
            // Arrange
            Configuration config = createTestConfiguration("config-id", "tenant-1", "api.timeout");
            ConfigurationCreatedEvent event = new ConfigurationCreatedEvent(config, "test-user");

            CompletableFuture<SendResult<String, Object>> failedFuture = new CompletableFuture<>();
            failedFuture.completeExceptionally(new RuntimeException("Network error"));

            when(kafkaTemplate.send(eq(TEST_TOPIC), any(String.class), any(ConfigurationCreatedEvent.class)))
                .thenReturn(failedFuture);

            // Act & Assert
            CompletableFuture<Void> result = publisher.publishConfigurationCreated(event);

            RuntimeException exception = assertThrows(RuntimeException.class, () -> result.join());
            assertTrue(exception.getMessage().contains("Failed to publish event to Kafka"));
        }

        @Test
        void publishConfigurationUpdated_WhenKafkaSendThrowsException_PropagatesException() {
            // Arrange
            Configuration oldConfig = createTestConfiguration("config-id", "tenant-1", "api.timeout");
            Configuration newConfig = createTestConfiguration("config-id", "tenant-1", "api.timeout")
                .withVersion(2);
            ConfigurationUpdatedEvent event = new ConfigurationUpdatedEvent(
                oldConfig, newConfig, "test-user", "Update"
            );

            CompletableFuture<SendResult<String, Object>> failedFuture = new CompletableFuture<>();
            failedFuture.completeExceptionally(new RuntimeException("Kafka broker unavailable"));

            when(kafkaTemplate.send(eq(TEST_TOPIC), any(String.class), any(ConfigurationUpdatedEvent.class)))
                .thenReturn(failedFuture);

            // Act & Assert
            CompletableFuture<Void> result = publisher.publishConfigurationUpdated(event);

            RuntimeException exception = assertThrows(RuntimeException.class, () -> result.join());
            assertTrue(exception.getMessage().contains("Failed to publish event to Kafka"));
        }

        @Test
        void publishConfigurationDeleted_WhenKafkaSendThrowsException_PropagatesException() {
            // Arrange
            ConfigurationDeletedEvent event = new ConfigurationDeletedEvent(
                "config-id", "tenant-1", "api.timeout", "production", "default",
                1, "value", "test-user", "Delete"
            );

            CompletableFuture<SendResult<String, Object>> failedFuture = new CompletableFuture<>();
            failedFuture.completeExceptionally(new RuntimeException("Timeout"));

            when(kafkaTemplate.send(eq(TEST_TOPIC), any(String.class), any(ConfigurationDeletedEvent.class)))
                .thenReturn(failedFuture);

            // Act & Assert
            CompletableFuture<Void> result = publisher.publishConfigurationDeleted(event);

            RuntimeException exception = assertThrows(RuntimeException.class, () -> result.join());
            assertTrue(exception.getMessage().contains("Failed to publish event to Kafka"));
        }
    }

    @Nested
    class MessageKeyTests {

        @Test
        void publishConfigurationCreated_UsesCorrectMessageKeyFormat() {
            // Arrange
            Configuration config = createTestConfiguration("config-123", "tenant-abc", "api.timeout");
            ConfigurationCreatedEvent event = new ConfigurationCreatedEvent(config, "test-user");

            SendResult<String, Object> mockSendResult = createMockSendResult(1L);
            CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(mockSendResult);

            when(kafkaTemplate.send(eq(TEST_TOPIC), any(String.class), any(ConfigurationCreatedEvent.class)))
                .thenReturn(future);

            // Act
            publisher.publishConfigurationCreated(event).join();

            // Assert
            ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
            verify(kafkaTemplate).send(eq(TEST_TOPIC), keyCaptor.capture(), any(ConfigurationCreatedEvent.class));

            assertEquals("tenant-abc:config-123", keyCaptor.getValue());
        }

        @Test
        void publishConfigurationUpdated_UsesCorrectMessageKeyFormat() {
            // Arrange
            Configuration oldConfig = createTestConfiguration("config-456", "tenant-xyz", "api.timeout");
            Configuration newConfig = createTestConfiguration("config-456", "tenant-xyz", "api.timeout")
                .withVersion(2);
            ConfigurationUpdatedEvent event = new ConfigurationUpdatedEvent(
                oldConfig, newConfig, "test-user", "Update"
            );

            SendResult<String, Object> mockSendResult = createMockSendResult(2L);
            CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(mockSendResult);

            when(kafkaTemplate.send(eq(TEST_TOPIC), any(String.class), any(ConfigurationUpdatedEvent.class)))
                .thenReturn(future);

            // Act
            publisher.publishConfigurationUpdated(event).join();

            // Assert
            ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
            verify(kafkaTemplate).send(eq(TEST_TOPIC), keyCaptor.capture(), any(ConfigurationUpdatedEvent.class));

            assertEquals("tenant-xyz:config-456", keyCaptor.getValue());
        }

        @Test
        void publishConfigurationDeleted_UsesCorrectMessageKeyFormat() {
            // Arrange
            ConfigurationDeletedEvent event = new ConfigurationDeletedEvent(
                "config-789", "tenant-def", "api.timeout", "production", "default",
                1, "value", "test-user", "Delete"
            );

            SendResult<String, Object> mockSendResult = createMockSendResult(3L);
            CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(mockSendResult);

            when(kafkaTemplate.send(eq(TEST_TOPIC), any(String.class), any(ConfigurationDeletedEvent.class)))
                .thenReturn(future);

            // Act
            publisher.publishConfigurationDeleted(event).join();

            // Assert
            ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
            verify(kafkaTemplate).send(eq(TEST_TOPIC), keyCaptor.capture(), any(ConfigurationDeletedEvent.class));

            assertEquals("tenant-def:config-789", keyCaptor.getValue());
        }
    }
}
