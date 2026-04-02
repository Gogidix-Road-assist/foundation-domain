package com.gogidix.rapidassist.shared.audit.library.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.rapidassist.shared.audit.library.domain.model.AuditActor;
import com.gogidix.rapidassist.shared.audit.library.domain.model.AuditEntityRef;
import com.gogidix.rapidassist.shared.audit.library.domain.model.AuditEventEnvelope;
import com.gogidix.rapidassist.shared.audit.library.infrastructure.database.AuditLogEntity;
import com.gogidix.rapidassist.shared.audit.library.infrastructure.database.AuditLogRepository;
import com.gogidix.rapidassist.shared.audit.library.infrastructure.database.DatabaseAuditPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Integration tests for DatabaseAuditPublisher.
 * Note: This is a simplified unit test since full MongoDB integration requires embedded MongoDB.
 */
@ExtendWith(MockitoExtension.class)
class DatabaseAuditPublisherIntegrationTest {

    @Mock
    private AuditLogRepository repository;

    private DatabaseAuditPublisher publisher;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        publisher = new DatabaseAuditPublisher(repository, objectMapper);
    }

    @Test
    void testPublishSavesToRepository() {
        // Arrange
        Instant now = Instant.now();
        AuditActor actor = new AuditActor("USER", "user-123", "John Doe");
        AuditEntityRef entity = new AuditEntityRef("Customer", "customer-456", "Acme Corp");

        when(repository.save(any(AuditLogEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        publisher.publish(new AuditEventEnvelope(
                "1.0", "event-123", "USER_CREATED", now, "corr-456", "US",
                "tenant-001", null, actor, entity, null, Map.of("name", "John Doe")
        ));

        // Assert
        verify(repository, times(1)).save(any(AuditLogEntity.class));
    }

    @Test
    void testPublishWithNullAttributes() {
        // Arrange
        Instant now = Instant.now();
        AuditActor actor = new AuditActor("USER", "user-123", "John Doe");
        AuditEntityRef entity = new AuditEntityRef("Customer", "customer-456", "Acme Corp");

        when(repository.save(any(AuditLogEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        publisher.publish(new AuditEventEnvelope(
                "1.0", "event-123", "USER_CREATED", now, "corr-456", "US",
                "tenant-001", null, actor, entity, null, null
        ));

        // Assert
        verify(repository, times(1)).save(any(AuditLogEntity.class));
    }

    @Test
    void testPublishWithNullPayload() {
        // Arrange
        Instant now = Instant.now();
        AuditActor actor = new AuditActor("USER", "user-123", "John Doe");
        AuditEntityRef entity = new AuditEntityRef("Customer", "customer-456", "Acme Corp");

        when(repository.save(any(AuditLogEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        publisher.publish(new AuditEventEnvelope(
                "1.0", "event-123", "USER_CREATED", now, "corr-456", "US",
                "tenant-001", null, actor, entity, null, Map.of()
        ));

        // Assert
        verify(repository, times(1)).save(any(AuditLogEntity.class));
    }
}
