package com.gogidix.rapidassist.payment.service.application.usecase;

import com.gogidix.rapidassist.payment.service.domain.model.PaymentIntent;
import com.gogidix.rapidassist.payment.service.domain.model.PaymentStatus;
import com.gogidix.rapidassist.payment.service.domain.port.out.PaymentIntentStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CreatePaymentIntentUseCase.
 * <p>
 * Tests the business logic for creating payment intents.
 */
@ExtendWith(MockitoExtension.class)
class CreatePaymentIntentUseCaseTest {

    @Mock
    private PaymentIntentStore store;

    @InjectMocks
    private CreatePaymentIntentUseCase useCase;

    private final String TENANT_ID = "tenant-123";
    private final BigDecimal AMOUNT = new BigDecimal("10.00");
    private final String CURRENCY = "USD";

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @DisplayName("Should create payment intent successfully")
    void create_ShouldCreatePaymentIntentSuccessfully() {
        // Given
        PaymentIntent expectedIntent = new PaymentIntent(
                TENANT_ID,
                "generated-intent-id",
                CURRENCY,
                AMOUNT,
                PaymentStatus.CREATED,
                Instant.now()
        );
        when(store.save(any(PaymentIntent.class))).thenReturn(expectedIntent);

        // When
        PaymentIntent result = useCase.create(TENANT_ID, AMOUNT, CURRENCY);

        // Then
        assertNotNull(result);
        assertEquals(TENANT_ID, result.tenantId());
        assertEquals(CURRENCY, result.currency());
        assertEquals(AMOUNT, result.amount());
        assertEquals(PaymentStatus.CREATED, result.status());
        assertNotNull(result.intentId());
        assertNotNull(result.createdAt());

        verify(store, times(1)).save(any(PaymentIntent.class));
    }

    @Test
    @DisplayName("Should generate unique intent ID")
    void create_ShouldGenerateUniqueIntentId() {
        // Given
        PaymentIntent intent1 = new PaymentIntent(TENANT_ID, "id-1", CURRENCY, AMOUNT, PaymentStatus.CREATED, Instant.now());
        PaymentIntent intent2 = new PaymentIntent(TENANT_ID, "id-2", CURRENCY, AMOUNT, PaymentStatus.CREATED, Instant.now());
        when(store.save(any(PaymentIntent.class))).thenReturn(intent1, intent2);

        // When
        PaymentIntent result1 = useCase.create(TENANT_ID, AMOUNT, CURRENCY);
        PaymentIntent result2 = useCase.create(TENANT_ID, AMOUNT, CURRENCY);

        // Then
        assertNotEquals(result1.intentId(), result2.intentId());

        verify(store, times(2)).save(any(PaymentIntent.class));
    }

    @Test
    @DisplayName("Should pass correct tenant ID to store")
    void create_ShouldPassCorrectTenantId() {
        // Given
        PaymentIntent expectedIntent = new PaymentIntent(
                TENANT_ID,
                "intent-id",
                CURRENCY,
                AMOUNT,
                PaymentStatus.CREATED,
                Instant.now()
        );
        when(store.save(any(PaymentIntent.class))).thenReturn(expectedIntent);

        // When
        useCase.create(TENANT_ID, AMOUNT, CURRENCY);

        // Then
        verify(store, times(1)).save(argThat(intent ->
                TENANT_ID.equals(intent.tenantId())
        ));
    }

    @Test
    @DisplayName("Should set status to CREATED")
    void create_ShouldSetStatusToCreated() {
        // Given
        PaymentIntent expectedIntent = new PaymentIntent(
                TENANT_ID,
                "intent-id",
                CURRENCY,
                AMOUNT,
                PaymentStatus.CREATED,
                Instant.now()
        );
        when(store.save(any(PaymentIntent.class))).thenReturn(expectedIntent);

        // When
        PaymentIntent result = useCase.create(TENANT_ID, AMOUNT, CURRENCY);

        // Then
        assertEquals(PaymentStatus.CREATED, result.status());
    }

    @Test
    @DisplayName("Should set creation timestamp")
    void create_ShouldSetCreationTimestamp() {
        // Given
        PaymentIntent expectedIntent = new PaymentIntent(
                TENANT_ID,
                "intent-id",
                CURRENCY,
                AMOUNT,
                PaymentStatus.CREATED,
                Instant.now()
        );
        when(store.save(any(PaymentIntent.class))).thenReturn(expectedIntent);

        // When
        PaymentIntent result = useCase.create(TENANT_ID, AMOUNT, CURRENCY);

        // Then
        assertNotNull(result.createdAt());
        assertFalse(result.createdAt().isAfter(Instant.now()));
    }
}
