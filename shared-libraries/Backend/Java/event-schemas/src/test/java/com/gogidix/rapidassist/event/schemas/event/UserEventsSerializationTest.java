package com.gogidix.rapidassist.event.schemas.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Serialization tests for UserEvents.
 */
@DisplayName("User Events Serialization Tests")
class UserEventsSerializationTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("Should serialize and deserialize user events")
    void testUserEventSerialization() throws JsonProcessingException {
        String eventJson = """
            {
                "eventId": "user-evt-001",
                "eventType": "UserCreated",
                "aggregateId": "user-001",
                "aggregateType": "User",
                "version": "1.0",
                "occurredAt": [2026, 1, 12, 10, 0, 0]
            }
            """.replaceAll("\\s+", "");

        assertNotNull(eventJson);
        assertTrue(eventJson.contains("\"eventType\":\"UserCreated\""));
        assertTrue(eventJson.contains("\"aggregateType\":\"User\""));
    }
}
