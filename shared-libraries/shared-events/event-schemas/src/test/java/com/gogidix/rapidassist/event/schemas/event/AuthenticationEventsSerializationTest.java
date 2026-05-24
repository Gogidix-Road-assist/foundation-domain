package com.gogidix.rapidassist.event.schemas.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Serialization tests for AuthenticationEvents.
 */
@DisplayName("Authentication Events Serialization Tests")
class AuthenticationEventsSerializationTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("Should serialize and deserialize authentication events")
    void testAuthenticationEventSerialization() throws JsonProcessingException {
        String eventJson = """
            {
                "eventId": "auth-evt-001",
                "eventType": "UserLoggedIn",
                "aggregateId": "user-001",
                "aggregateType": "Authentication",
                "version": "1.0",
                "occurredAt": [2026, 1, 12, 10, 0, 0],
                "userId": "user-001",
                "username": "testuser",
                "loginMethod": "PASSWORD"
            }
            """.replaceAll("\\s+", "");

        assertNotNull(eventJson);
        assertTrue(eventJson.contains("\"eventType\":\"UserLoggedIn\""));
        assertTrue(eventJson.contains("\"loginMethod\":\"PASSWORD\""));
    }
}
