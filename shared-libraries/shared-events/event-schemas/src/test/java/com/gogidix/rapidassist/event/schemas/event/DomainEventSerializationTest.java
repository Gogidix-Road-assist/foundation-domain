package com.gogidix.rapidassist.event.schemas.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive serialization/deserialization tests for DomainEvent and all event types.
 * Tests JSON binding with Jackson ObjectMapper.
 */
@DisplayName("Domain Event Serialization Tests")
class DomainEventSerializationTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("Should serialize ServiceCreated event to JSON")
    void testSerializeServiceCreatedEvent() throws JsonProcessingException {
        ServiceEvents.ServiceCreated event = new ServiceEvents.ServiceCreated();
        event.setEventId("evt-123");
        event.setEventType("ServiceCreated");
        event.setAggregateId("agg-456");
        event.setAggregateType("Service");
        event.setTenantId("tenant-789");
        event.setOccurredAt(LocalDateTime.of(2026, 1, 12, 10, 30, 0));
        event.setVersion("1.0");
        event.setServiceName("Towing Service");
        event.setServiceType("TOWING");
        event.setServiceCode("TOW-001");

        String json = objectMapper.writeValueAsString(event);

        assertNotNull(json);
        assertTrue(json.contains("\"eventId\":\"evt-123\""));
        assertTrue(json.contains("\"eventType\":\"ServiceCreated\""));
        assertTrue(json.contains("\"serviceName\":\"Towing Service\""));
        assertTrue(json.contains("\"serviceType\":\"TOWING\""));
        assertTrue(json.contains("\"serviceCode\":\"TOW-001\""));
        assertTrue(json.contains("\"version\":\"1.0\""));
        // LocalDateTime serializes as array, may contain spaces
        assertTrue(json.contains("\"occurredAt\""));
        assertTrue(json.contains("2026"));
    }

    @Test
    @DisplayName("Should deserialize ServiceCreated event from JSON")
    void testDeserializeServiceCreatedEvent() throws JsonProcessingException {
        String json = """
            {
                "eventId": "evt-123",
                "eventType": "ServiceCreated",
                "aggregateId": "agg-456",
                "aggregateType": "Service",
                "tenantId": "tenant-789",
                "occurredAt": [2026, 1, 12, 10, 30, 0],
                "version": "1.0",
                "serviceName": "Towing Service",
                "serviceType": "TOWING",
                "serviceCode": "TOW-001"
            }
            """;

        ServiceEvents.ServiceCreated event = objectMapper.readValue(json, ServiceEvents.ServiceCreated.class);

        assertNotNull(event);
        assertEquals("evt-123", event.getEventId());
        assertEquals("ServiceCreated", event.getEventType());
        assertEquals("agg-456", event.getAggregateId());
        assertEquals("Service", event.getAggregateType());
        assertEquals("tenant-789", event.getTenantId());
        assertEquals("1.0", event.getVersion());
        assertEquals("Towing Service", event.getServiceName());
        assertEquals("TOWING", event.getServiceType());
        assertEquals("TOW-001", event.getServiceCode());
    }

    @Test
    @DisplayName("Should serialize ServiceUpdated event to JSON")
    void testSerializeServiceUpdatedEvent() throws JsonProcessingException {
        ServiceEvents.ServiceUpdated event = new ServiceEvents.ServiceUpdated();
        event.setEventId("evt-124");
        event.setEventType("ServiceUpdated");
        event.setAggregateId("agg-456");
        event.setAggregateType("Service");
        event.setVersion("1.0");
        event.setServiceName("Towing Service");
        event.setUpdateType("PRICE_CHANGE");

        String json = objectMapper.writeValueAsString(event);

        assertNotNull(json);
        assertTrue(json.contains("\"serviceName\":\"Towing Service\""));
        assertTrue(json.contains("\"updateType\":\"PRICE_CHANGE\""));
    }

    @Test
    @DisplayName("Should deserialize ServiceUpdated event from JSON")
    void testDeserializeServiceUpdatedEvent() throws JsonProcessingException {
        String json = """
            {
                "eventId": "evt-124",
                "eventType": "ServiceUpdated",
                "aggregateId": "agg-456",
                "aggregateType": "Service",
                "version": "1.0",
                "serviceName": "Towing Service",
                "updateType": "PRICE_CHANGE"
            }
            """;

        ServiceEvents.ServiceUpdated event = objectMapper.readValue(json, ServiceEvents.ServiceUpdated.class);

        assertNotNull(event);
        assertEquals("evt-124", event.getEventId());
        assertEquals("ServiceUpdated", event.getEventType());
        assertEquals("Towing Service", event.getServiceName());
        assertEquals("PRICE_CHANGE", event.getUpdateType());
    }

    @Test
    @DisplayName("Should serialize ServiceDeleted event to JSON")
    void testSerializeServiceDeletedEvent() throws JsonProcessingException {
        ServiceEvents.ServiceDeleted event = new ServiceEvents.ServiceDeleted();
        event.setEventId("evt-125");
        event.setEventType("ServiceDeleted");
        event.setAggregateId("agg-456");
        event.setAggregateType("Service");
        event.setVersion("1.0");
        event.setServiceName("Towing Service");
        event.setDeletionReason("Obsolete");

        String json = objectMapper.writeValueAsString(event);

        assertNotNull(json);
        assertTrue(json.contains("\"serviceName\":\"Towing Service\""));
        assertTrue(json.contains("\"deletionReason\":\"Obsolete\""));
    }

    @Test
    @DisplayName("Should serialize ServiceStatusChanged event to JSON")
    void testSerializeServiceStatusChangedEvent() throws JsonProcessingException {
        ServiceEvents.ServiceStatusChanged event = new ServiceEvents.ServiceStatusChanged();
        event.setEventId("evt-126");
        event.setEventType("ServiceStatusChanged");
        event.setAggregateId("agg-456");
        event.setAggregateType("Service");
        event.setVersion("1.0");
        event.setServiceName("Towing Service");
        event.setOldStatus("ACTIVE");
        event.setNewStatus("INACTIVE");
        event.setStatusReason("Maintenance");

        String json = objectMapper.writeValueAsString(event);

        assertNotNull(json);
        assertTrue(json.contains("\"oldStatus\":\"ACTIVE\""));
        assertTrue(json.contains("\"newStatus\":\"INACTIVE\""));
        assertTrue(json.contains("\"statusReason\":\"Maintenance\""));
    }

    @Test
    @DisplayName("Should handle null values in serialization")
    void testSerializeWithNullValues() throws JsonProcessingException {
        ServiceEvents.ServiceCreated event = new ServiceEvents.ServiceCreated();
        event.setEventId("evt-123");
        event.setEventType("ServiceCreated");
        event.setAggregateId("agg-456");
        event.setAggregateType("Service");
        event.setServiceName("Towing Service");
        // serviceType and serviceCode are null

        String json = objectMapper.writeValueAsString(event);

        assertNotNull(json);
        assertTrue(json.contains("\"serviceName\":\"Towing Service\""));
    }

    @Test
    @DisplayName("Should deserialize event with null values")
    void testDeserializeWithNullValues() throws JsonProcessingException {
        String json = """
            {
                "eventId": "evt-123",
                "eventType": "ServiceCreated",
                "aggregateId": "agg-456",
                "aggregateType": "Service",
                "serviceName": "Towing Service"
            }
            """;

        ServiceEvents.ServiceCreated event = objectMapper.readValue(json, ServiceEvents.ServiceCreated.class);

        assertNotNull(event);
        assertEquals("Towing Service", event.getServiceName());
        assertNull(event.getServiceType());
        assertNull(event.getServiceCode());
    }

    @Test
    @DisplayName("Should preserve event key structure")
    void testEventKeyStructure() {
        ServiceEvents.ServiceCreated event = new ServiceEvents.ServiceCreated();
        event.setEventType("ServiceCreated");
        event.setAggregateType("Service");

        String eventKey = event.getEventKey();

        assertEquals("Service.ServiceCreated", eventKey);
    }

    @Test
    @DisplayName("Should handle date/time serialization correctly")
    void testDateTimeSerialization() throws JsonProcessingException {
        ServiceEvents.ServiceCreated event = new ServiceEvents.ServiceCreated();
        event.setEventId("evt-123");
        event.setEventType("ServiceCreated");
        event.setAggregateId("agg-456");
        event.setAggregateType("Service");
        LocalDateTime testTime = LocalDateTime.of(2026, 1, 12, 15, 45, 30);
        event.setOccurredAt(testTime);

        String json = objectMapper.writeValueAsString(event);
        assertNotNull(json);
        assertTrue(json.contains("occurredAt"));

        ServiceEvents.ServiceCreated deserialized = objectMapper.readValue(json, ServiceEvents.ServiceCreated.class);
        assertNotNull(deserialized);
        assertEquals(testTime, deserialized.getOccurredAt());
    }

    @Test
    @DisplayName("Should serialize all correlation fields")
    void testCorrelationFieldsSerialization() throws JsonProcessingException {
        ServiceEvents.ServiceCreated event = new ServiceEvents.ServiceCreated();
        event.setEventId("evt-123");
        event.setCorrelationId("corr-123");
        event.setCausationId("cause-123");
        event.setUserId("user-123");
        event.setUsername("testuser");
        event.setTenantId("tenant-123");
        event.setOrganizationId("org-123");

        String json = objectMapper.writeValueAsString(event);

        assertNotNull(json);
        assertTrue(json.contains("\"correlationId\":\"corr-123\""));
        assertTrue(json.contains("\"causationId\":\"cause-123\""));
        assertTrue(json.contains("\"userId\":\"user-123\""));
        assertTrue(json.contains("\"username\":\"testuser\""));
        assertTrue(json.contains("\"tenantId\":\"tenant-123\""));
        assertTrue(json.contains("\"organizationId\":\"org-123\""));
    }

    @Test
    @DisplayName("Should deserialize all correlation fields")
    void testCorrelationFieldsDeserialization() throws JsonProcessingException {
        String json = """
            {
                "eventId": "evt-123",
                "eventType": "ServiceCreated",
                "aggregateId": "agg-456",
                "aggregateType": "Service",
                "correlationId": "corr-123",
                "causationId": "cause-123",
                "userId": "user-123",
                "username": "testuser",
                "tenantId": "tenant-123",
                "organizationId": "org-123"
            }
            """;

        ServiceEvents.ServiceCreated event = objectMapper.readValue(json, ServiceEvents.ServiceCreated.class);

        assertEquals("corr-123", event.getCorrelationId());
        assertEquals("cause-123", event.getCausationId());
        assertEquals("user-123", event.getUserId());
        assertEquals("testuser", event.getUsername());
        assertEquals("tenant-123", event.getTenantId());
        assertEquals("org-123", event.getOrganizationId());
    }
}
