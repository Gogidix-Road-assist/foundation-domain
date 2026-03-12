package com.gogidix.rapidassist.ai.fraud.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FraudRiskLevel Enum Tests")
class FraudRiskLevelTest {

    @Test
    @DisplayName("Enum should contain all expected risk levels")
    void testEnumValues_AllLevelsPresent() {
        FraudRiskLevel[] levels = FraudRiskLevel.values();

        assertEquals(4, levels.length);

        assertTrue(containsLevel(levels, "LOW"));
        assertTrue(containsLevel(levels, "MEDIUM"));
        assertTrue(containsLevel(levels, "HIGH"));
        assertTrue(containsLevel(levels, "CRITICAL"));
    }

    @Test
    @DisplayName("Enum should have correct ordinal ordering")
    void testEnumOrdering_CorrectOrder() {
        assertEquals(0, FraudRiskLevel.LOW.ordinal());
        assertEquals(1, FraudRiskLevel.MEDIUM.ordinal());
        assertEquals(2, FraudRiskLevel.HIGH.ordinal());
        assertEquals(3, FraudRiskLevel.CRITICAL.ordinal());
    }

    @Test
    @DisplayName("valueOf should return correct enum values")
    void testValueOf_ReturnsCorrectValues() {
        assertEquals(FraudRiskLevel.LOW, FraudRiskLevel.valueOf("LOW"));
        assertEquals(FraudRiskLevel.MEDIUM, FraudRiskLevel.valueOf("MEDIUM"));
        assertEquals(FraudRiskLevel.HIGH, FraudRiskLevel.valueOf("HIGH"));
        assertEquals(FraudRiskLevel.CRITICAL, FraudRiskLevel.valueOf("CRITICAL"));
    }

    @Test
    @DisplayName("valueOf should throw exception for invalid value")
    void testValueOf_InvalidValueThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            FraudRiskLevel.valueOf("INVALID");
        });
    }

    @Test
    @DisplayName("Enum values should be comparable")
    void testEnumValues_Comparable() {
        assertTrue(FraudRiskLevel.LOW.ordinal() < FraudRiskLevel.MEDIUM.ordinal());
        assertTrue(FraudRiskLevel.MEDIUM.ordinal() < FraudRiskLevel.HIGH.ordinal());
        assertTrue(FraudRiskLevel.HIGH.ordinal() < FraudRiskLevel.CRITICAL.ordinal());
    }

    @Test
    @DisplayName("Enum name should match declared value")
    void testEnumName_MatchDeclaredValue() {
        assertEquals("LOW", FraudRiskLevel.LOW.name());
        assertEquals("MEDIUM", FraudRiskLevel.MEDIUM.name());
        assertEquals("HIGH", FraudRiskLevel.HIGH.name());
        assertEquals("CRITICAL", FraudRiskLevel.CRITICAL.name());
    }

    private boolean containsLevel(FraudRiskLevel[] levels, String name) {
        for (FraudRiskLevel level : levels) {
            if (level.name().equals(name)) {
                return true;
            }
        }
        return false;
    }
}
