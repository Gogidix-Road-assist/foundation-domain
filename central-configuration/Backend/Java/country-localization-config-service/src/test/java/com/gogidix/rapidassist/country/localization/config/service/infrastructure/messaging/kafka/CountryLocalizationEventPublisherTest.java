package com.gogidix.rapidassist.country.localization.config.service.infrastructure.messaging.kafka;

import com.gogidix.rapidassist.country.localization.config.service.domain.event.CountryLocalizationCreatedEvent;
import com.gogidix.rapidassist.country.localization.config.service.domain.event.CountryLocalizationDeletedEvent;
import com.gogidix.rapidassist.country.localization.config.service.domain.event.CountryLocalizationUpdatedEvent;
import com.gogidix.rapidassist.country.localization.config.service.domain.model.CountryLocalization;
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

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CountryLocalizationEventPublisher.
 * Tests Kafka event publishing functionality including success and failure scenarios.
 */
@ExtendWith(MockitoExtension.class)
class CountryLocalizationEventPublisherTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    private CountryLocalizationEventPublisher publisher;
    private static final String TEST_TOPIC = "country-localization.events";

    @BeforeEach
    void setUp() {
        publisher = new CountryLocalizationEventPublisher(kafkaTemplate, TEST_TOPIC);
    }

    private SendResult<String, Object> createMockSendResult(long offset) {
        @SuppressWarnings("unchecked")
        SendResult<String, Object> mockSendResult = mock(SendResult.class);
        RecordMetadata mockMetadata = mock(RecordMetadata.class);
        when(mockSendResult.getRecordMetadata()).thenReturn(mockMetadata);
        when(mockMetadata.offset()).thenReturn(offset);
        return mockSendResult;
    }

    private CountryLocalization createTestCountryLocalization() {
        return CountryLocalization.builder()
            .id("country-id-1")
            .countryCode("IE")
            .countryName("Ireland")
            .locale(CountryLocalization.LocaleConfig.of("en", "IE"))
            .currency(CountryLocalization.CurrencyConfig.of("EUR", "\u20ac"))
            .dateTime(CountryLocalization.DateTimeConfig.of("Europe/Dublin"))
            .addressFormat(new CountryLocalization.AddressFormat("{street}, {city}", List.of("street", "city"), false, "Eircode", false))
            .phoneFormat(CountryLocalization.PhoneFormat.of("+353"))
            .emergencyServices(CountryLocalization.EmergencyServices.of("112", "999"))
            .legalRequirements(CountryLocalization.LegalRequirements.standard())
            .measurementSystem(CountryLocalization.MeasurementSystem.METRIC)
            .active(true)
            .createdBy("test-user")
            .version(1)
            .build();
    }

    @Nested
    class PublishCountryLocalizationCreatedTests {

        @Test
        void publishCountryLocalizationCreated_WithValidEvent_CallsKafkaTemplate() {
            // Arrange
            CountryLocalization country = createTestCountryLocalization();
            CountryLocalizationCreatedEvent event = new CountryLocalizationCreatedEvent(country, "test-user");

            SendResult<String, Object> mockSendResult = createMockSendResult(123L);
            CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(mockSendResult);
            when(kafkaTemplate.send(eq(TEST_TOPIC), any(String.class), any(CountryLocalizationCreatedEvent.class)))
                .thenReturn(future);

            // Act
            CompletableFuture<Void> result = publisher.publishCountryLocalizationCreated(event);

            // Assert
            assertNotNull(result);
            verify(kafkaTemplate, times(1)).send(
                eq(TEST_TOPIC),
                eq("default:country-id-1"),
                any(CountryLocalizationCreatedEvent.class)
            );
        }

        @Test
        void publishCountryLocalizationCreated_WhenKafkaFails_ThrowsException() {
            // Arrange
            CountryLocalization country = createTestCountryLocalization();
            CountryLocalizationCreatedEvent event = new CountryLocalizationCreatedEvent(country, "test-user");

            CompletableFuture<SendResult<String, Object>> failedFuture = new CompletableFuture<>();
            failedFuture.completeExceptionally(new RuntimeException("Kafka connection failed"));

            when(kafkaTemplate.send(eq(TEST_TOPIC), any(String.class), any(CountryLocalizationCreatedEvent.class)))
                .thenReturn(failedFuture);

            // Act & Assert
            CompletableFuture<Void> result = publisher.publishCountryLocalizationCreated(event);

            Exception exception = assertThrows(RuntimeException.class, () -> result.join());
            assertTrue(exception.getMessage().contains("Failed to publish event to Kafka"));
        }
    }

    @Nested
    class PublishCountryLocalizationUpdatedTests {

        @Test
        void publishCountryLocalizationUpdated_WithValidEvent_PublishesSuccessfully() throws Exception {
            // Arrange
            CountryLocalization oldCountry = createTestCountryLocalization();
            CountryLocalization newCountry = CountryLocalization.builder()
                .id("country-id-1")
                .countryCode("IE")
                .countryName("Ireland Updated")
                .locale(CountryLocalization.LocaleConfig.of("en", "IE"))
                .currency(CountryLocalization.CurrencyConfig.of("EUR", "\u20ac"))
                .dateTime(CountryLocalization.DateTimeConfig.of("Europe/Dublin"))
                .addressFormat(new CountryLocalization.AddressFormat("{street}, {city}", List.of("street", "city"), false, "Eircode", false))
                .phoneFormat(CountryLocalization.PhoneFormat.of("+353"))
                .emergencyServices(CountryLocalization.EmergencyServices.of("112", "999"))
                .legalRequirements(CountryLocalization.LegalRequirements.standard())
                .measurementSystem(CountryLocalization.MeasurementSystem.METRIC)
                .active(true)
                .createdBy("test-user")
                .version(2)
                .build();

            CountryLocalizationUpdatedEvent event = new CountryLocalizationUpdatedEvent(
                oldCountry, newCountry, "test-user", "Update country name"
            );

            SendResult<String, Object> mockSendResult = createMockSendResult(456L);
            CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(mockSendResult);
            when(kafkaTemplate.send(eq(TEST_TOPIC), any(String.class), any(CountryLocalizationUpdatedEvent.class)))
                .thenReturn(future);

            // Act
            CompletableFuture<Void> result = publisher.publishCountryLocalizationUpdated(event);

            // Assert
            assertNotNull(result);
            result.join();

            ArgumentCaptor<CountryLocalizationUpdatedEvent> eventCaptor = ArgumentCaptor.forClass(CountryLocalizationUpdatedEvent.class);

            verify(kafkaTemplate, times(1)).send(
                eq(TEST_TOPIC),
                any(String.class),
                eventCaptor.capture()
            );

            CountryLocalizationUpdatedEvent capturedEvent = eventCaptor.getValue();
            assertEquals("IE", capturedEvent.countryCode());
            assertEquals("Ireland", capturedEvent.oldValue().countryName());
            assertEquals("Ireland Updated", capturedEvent.newValue().countryName());
            assertEquals(1, capturedEvent.oldValue().version());
            assertEquals(2, capturedEvent.newValue().version());
            assertEquals("Update country name", capturedEvent.reason());
        }

        @Test
        void publishCountryLocalizationUpdated_WithNullReason_PublishesSuccessfully() {
            // Arrange
            CountryLocalization oldCountry = createTestCountryLocalization();
            CountryLocalization newCountry = CountryLocalization.builder()
                .id("country-id-1")
                .countryCode("IE")
                .countryName("Ireland")
                .locale(CountryLocalization.LocaleConfig.of("en", "IE"))
                .currency(CountryLocalization.CurrencyConfig.of("EUR", "\u20ac"))
                .dateTime(CountryLocalization.DateTimeConfig.of("Europe/Dublin"))
                .addressFormat(new CountryLocalization.AddressFormat("{street}, {city}", List.of("street", "city"), false, "Eircode", false))
                .phoneFormat(CountryLocalization.PhoneFormat.of("+353"))
                .emergencyServices(CountryLocalization.EmergencyServices.of("112", "999"))
                .legalRequirements(CountryLocalization.LegalRequirements.standard())
                .measurementSystem(CountryLocalization.MeasurementSystem.METRIC)
                .active(true)
                .createdBy("test-user")
                .version(2)
                .build();

            CountryLocalizationUpdatedEvent event = new CountryLocalizationUpdatedEvent(
                oldCountry, newCountry, "test-user", null
            );

            SendResult<String, Object> mockSendResult = createMockSendResult(1L);
            CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(mockSendResult);

            when(kafkaTemplate.send(eq(TEST_TOPIC), any(String.class), any(CountryLocalizationUpdatedEvent.class)))
                .thenReturn(future);

            // Act
            CompletableFuture<Void> result = publisher.publishCountryLocalizationUpdated(event);
            result.join();

            // Assert
            verify(kafkaTemplate, times(1)).send(
                eq(TEST_TOPIC),
                eq("default:country-id-1"),
                any(CountryLocalizationUpdatedEvent.class)
            );
        }
    }

    @Nested
    class PublishCountryLocalizationDeletedTests {

        @Test
        void publishCountryLocalizationDeleted_WithValidEvent_PublishesSuccessfully() throws Exception {
            // Arrange
            CountryLocalizationDeletedEvent event = new CountryLocalizationDeletedEvent(
                "country-id-1",
                "default",
                "IE",
                "Ireland",
                1,
                "test-user",
                "Country removed from system"
            );

            SendResult<String, Object> mockSendResult = createMockSendResult(789L);
            CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(mockSendResult);
            when(kafkaTemplate.send(eq(TEST_TOPIC), any(String.class), any(CountryLocalizationDeletedEvent.class)))
                .thenReturn(future);

            // Act
            CompletableFuture<Void> result = publisher.publishCountryLocalizationDeleted(event);

            // Assert
            assertNotNull(result);
            result.join();

            ArgumentCaptor<CountryLocalizationDeletedEvent> eventCaptor = ArgumentCaptor.forClass(CountryLocalizationDeletedEvent.class);

            verify(kafkaTemplate, times(1)).send(
                eq(TEST_TOPIC),
                any(String.class),
                eventCaptor.capture()
            );

            CountryLocalizationDeletedEvent capturedEvent = eventCaptor.getValue();
            assertEquals("IE", capturedEvent.countryCode());
            assertEquals("Ireland", capturedEvent.countryName());
            assertEquals(1, capturedEvent.version());
            assertEquals("Country removed from system", capturedEvent.reason());
        }

        @Test
        void publishCountryLocalizationDeleted_WithNullReason_PublishesSuccessfully() {
            // Arrange
            CountryLocalizationDeletedEvent event = new CountryLocalizationDeletedEvent(
                "country-id",
                "default",
                "IE",
                "Ireland",
                1,
                "test-user",
                null
            );

            SendResult<String, Object> mockSendResult = createMockSendResult(1L);
            CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(mockSendResult);

            when(kafkaTemplate.send(eq(TEST_TOPIC), any(String.class), any(CountryLocalizationDeletedEvent.class)))
                .thenReturn(future);

            // Act
            CompletableFuture<Void> result = publisher.publishCountryLocalizationDeleted(event);
            result.join();

            // Assert
            ArgumentCaptor<CountryLocalizationDeletedEvent> eventCaptor = ArgumentCaptor.forClass(CountryLocalizationDeletedEvent.class);

            verify(kafkaTemplate, times(1)).send(
                eq(TEST_TOPIC),
                eq("default:country-id"),
                eventCaptor.capture()
            );

            assertNull(eventCaptor.getValue().reason());
        }
    }

    @Nested
    class PublishGenericEventTests {

        @Test
        void publishEvent_WithCreatedEvent_PublishesSuccessfully() {
            // Arrange
            CountryLocalization country = createTestCountryLocalization();
            CountryLocalizationCreatedEvent event = new CountryLocalizationCreatedEvent(country, "test-user");

            SendResult<String, Object> mockSendResult = createMockSendResult(1L);
            CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(mockSendResult);

            when(kafkaTemplate.send(eq(TEST_TOPIC), any(String.class), any(CountryLocalizationCreatedEvent.class)))
                .thenReturn(future);

            // Act
            CompletableFuture<Void> result = publisher.publishEvent(event);
            result.join();

            // Assert
            verify(kafkaTemplate, times(1)).send(
                eq(TEST_TOPIC),
                eq("default:country-id-1"),
                eq(event)
            );
        }

        @Test
        void publishEvent_WithUpdatedEvent_PublishesSuccessfully() {
            // Arrange
            CountryLocalization oldCountry = createTestCountryLocalization();
            CountryLocalization newCountry = CountryLocalization.builder()
                .id("country-id-1")
                .countryCode("IE")
                .countryName("Ireland")
                .locale(CountryLocalization.LocaleConfig.of("en", "IE"))
                .currency(CountryLocalization.CurrencyConfig.of("EUR", "\u20ac"))
                .dateTime(CountryLocalization.DateTimeConfig.of("Europe/Dublin"))
                .addressFormat(new CountryLocalization.AddressFormat("{street}, {city}", List.of("street", "city"), false, "Eircode", false))
                .phoneFormat(CountryLocalization.PhoneFormat.of("+353"))
                .emergencyServices(CountryLocalization.EmergencyServices.of("112", "999"))
                .legalRequirements(CountryLocalization.LegalRequirements.standard())
                .measurementSystem(CountryLocalization.MeasurementSystem.METRIC)
                .active(true)
                .createdBy("test-user")
                .version(2)
                .build();

            CountryLocalizationUpdatedEvent event = new CountryLocalizationUpdatedEvent(
                oldCountry, newCountry, "test-user", "Update"
            );

            SendResult<String, Object> mockSendResult = createMockSendResult(2L);
            CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(mockSendResult);

            when(kafkaTemplate.send(eq(TEST_TOPIC), any(String.class), any(CountryLocalizationUpdatedEvent.class)))
                .thenReturn(future);

            // Act
            CompletableFuture<Void> result = publisher.publishEvent(event);
            result.join();

            // Assert
            verify(kafkaTemplate, times(1)).send(
                eq(TEST_TOPIC),
                eq("default:country-id-1"),
                eq(event)
            );
        }

        @Test
        void publishEvent_WithDeletedEvent_PublishesSuccessfully() {
            // Arrange
            CountryLocalizationDeletedEvent event = new CountryLocalizationDeletedEvent(
                "country-id", "default", "IE", "Ireland", 1, "test-user", "Delete"
            );

            SendResult<String, Object> mockSendResult = createMockSendResult(3L);
            CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(mockSendResult);

            when(kafkaTemplate.send(eq(TEST_TOPIC), any(String.class), any(CountryLocalizationDeletedEvent.class)))
                .thenReturn(future);

            // Act
            CompletableFuture<Void> result = publisher.publishEvent(event);
            result.join();

            // Assert
            verify(kafkaTemplate, times(1)).send(
                eq(TEST_TOPIC),
                eq("default:country-id"),
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
        void publishCountryLocalizationCreated_WhenKafkaSendThrowsException_PropagatesException() {
            // Arrange
            CountryLocalization country = createTestCountryLocalization();
            CountryLocalizationCreatedEvent event = new CountryLocalizationCreatedEvent(country, "test-user");

            CompletableFuture<SendResult<String, Object>> failedFuture = new CompletableFuture<>();
            failedFuture.completeExceptionally(new RuntimeException("Network error"));

            when(kafkaTemplate.send(eq(TEST_TOPIC), any(String.class), any(CountryLocalizationCreatedEvent.class)))
                .thenReturn(failedFuture);

            // Act & Assert
            CompletableFuture<Void> result = publisher.publishCountryLocalizationCreated(event);

            RuntimeException exception = assertThrows(RuntimeException.class, () -> result.join());
            assertTrue(exception.getMessage().contains("Failed to publish event to Kafka"));
        }

        @Test
        void publishCountryLocalizationUpdated_WhenKafkaSendThrowsException_PropagatesException() {
            // Arrange
            CountryLocalization oldCountry = createTestCountryLocalization();
            CountryLocalization newCountry = CountryLocalization.builder()
                .id("country-id-1")
                .countryCode("IE")
                .countryName("Ireland")
                .locale(CountryLocalization.LocaleConfig.of("en", "IE"))
                .currency(CountryLocalization.CurrencyConfig.of("EUR", "\u20ac"))
                .dateTime(CountryLocalization.DateTimeConfig.of("Europe/Dublin"))
                .addressFormat(new CountryLocalization.AddressFormat("{street}, {city}", List.of("street", "city"), false, "Eircode", false))
                .phoneFormat(CountryLocalization.PhoneFormat.of("+353"))
                .emergencyServices(CountryLocalization.EmergencyServices.of("112", "999"))
                .legalRequirements(CountryLocalization.LegalRequirements.standard())
                .measurementSystem(CountryLocalization.MeasurementSystem.METRIC)
                .active(true)
                .createdBy("test-user")
                .version(2)
                .build();

            CountryLocalizationUpdatedEvent event = new CountryLocalizationUpdatedEvent(
                oldCountry, newCountry, "test-user", "Update"
            );

            CompletableFuture<SendResult<String, Object>> failedFuture = new CompletableFuture<>();
            failedFuture.completeExceptionally(new RuntimeException("Kafka broker unavailable"));

            when(kafkaTemplate.send(eq(TEST_TOPIC), any(String.class), any(CountryLocalizationUpdatedEvent.class)))
                .thenReturn(failedFuture);

            // Act & Assert
            CompletableFuture<Void> result = publisher.publishCountryLocalizationUpdated(event);

            RuntimeException exception = assertThrows(RuntimeException.class, () -> result.join());
            assertTrue(exception.getMessage().contains("Failed to publish event to Kafka"));
        }

        @Test
        void publishCountryLocalizationDeleted_WhenKafkaSendThrowsException_PropagatesException() {
            // Arrange
            CountryLocalizationDeletedEvent event = new CountryLocalizationDeletedEvent(
                "country-id", "default", "IE", "Ireland", 1, "test-user", "Delete"
            );

            CompletableFuture<SendResult<String, Object>> failedFuture = new CompletableFuture<>();
            failedFuture.completeExceptionally(new RuntimeException("Timeout"));

            when(kafkaTemplate.send(eq(TEST_TOPIC), any(String.class), any(CountryLocalizationDeletedEvent.class)))
                .thenReturn(failedFuture);

            // Act & Assert
            CompletableFuture<Void> result = publisher.publishCountryLocalizationDeleted(event);

            RuntimeException exception = assertThrows(RuntimeException.class, () -> result.join());
            assertTrue(exception.getMessage().contains("Failed to publish event to Kafka"));
        }
    }

    @Nested
    class MessageKeyTests {

        @Test
        void publishCountryLocalizationCreated_UsesCorrectMessageKeyFormat() {
            // Arrange
            CountryLocalization country = createTestCountryLocalization();
            CountryLocalizationCreatedEvent event = new CountryLocalizationCreatedEvent(country, "test-user");

            SendResult<String, Object> mockSendResult = createMockSendResult(1L);
            CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(mockSendResult);

            when(kafkaTemplate.send(eq(TEST_TOPIC), any(String.class), any(CountryLocalizationCreatedEvent.class)))
                .thenReturn(future);

            // Act
            publisher.publishCountryLocalizationCreated(event).join();

            // Assert
            ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
            verify(kafkaTemplate).send(eq(TEST_TOPIC), keyCaptor.capture(), any(CountryLocalizationCreatedEvent.class));

            assertEquals("default:country-id-1", keyCaptor.getValue());
        }

        @Test
        void publishCountryLocalizationUpdated_UsesCorrectMessageKeyFormat() {
            // Arrange
            CountryLocalization oldCountry = createTestCountryLocalization();
            CountryLocalization newCountry = CountryLocalization.builder()
                .id("country-id-1")
                .countryCode("IE")
                .countryName("Ireland")
                .locale(CountryLocalization.LocaleConfig.of("en", "IE"))
                .currency(CountryLocalization.CurrencyConfig.of("EUR", "\u20ac"))
                .dateTime(CountryLocalization.DateTimeConfig.of("Europe/Dublin"))
                .addressFormat(new CountryLocalization.AddressFormat("{street}, {city}", List.of("street", "city"), false, "Eircode", false))
                .phoneFormat(CountryLocalization.PhoneFormat.of("+353"))
                .emergencyServices(CountryLocalization.EmergencyServices.of("112", "999"))
                .legalRequirements(CountryLocalization.LegalRequirements.standard())
                .measurementSystem(CountryLocalization.MeasurementSystem.METRIC)
                .active(true)
                .createdBy("test-user")
                .version(2)
                .build();

            CountryLocalizationUpdatedEvent event = new CountryLocalizationUpdatedEvent(
                oldCountry, newCountry, "test-user", "Update"
            );

            SendResult<String, Object> mockSendResult = createMockSendResult(2L);
            CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(mockSendResult);

            when(kafkaTemplate.send(eq(TEST_TOPIC), any(String.class), any(CountryLocalizationUpdatedEvent.class)))
                .thenReturn(future);

            // Act
            publisher.publishCountryLocalizationUpdated(event).join();

            // Assert
            ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
            verify(kafkaTemplate).send(eq(TEST_TOPIC), keyCaptor.capture(), any(CountryLocalizationUpdatedEvent.class));

            assertEquals("default:country-id-1", keyCaptor.getValue());
        }

        @Test
        void publishCountryLocalizationDeleted_UsesCorrectMessageKeyFormat() {
            // Arrange
            CountryLocalizationDeletedEvent event = new CountryLocalizationDeletedEvent(
                "country-id", "default", "IE", "Ireland", 1, "test-user", "Delete"
            );

            SendResult<String, Object> mockSendResult = createMockSendResult(3L);
            CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(mockSendResult);

            when(kafkaTemplate.send(eq(TEST_TOPIC), any(String.class), any(CountryLocalizationDeletedEvent.class)))
                .thenReturn(future);

            // Act
            publisher.publishCountryLocalizationDeleted(event).join();

            // Assert
            ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
            verify(kafkaTemplate).send(eq(TEST_TOPIC), keyCaptor.capture(), any(CountryLocalizationDeletedEvent.class));

            assertEquals("default:country-id", keyCaptor.getValue());
        }
    }
}
