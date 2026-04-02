package com.gogidix.rapidassist.common.domain.models.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GeoLocation Value Object Tests")
class GeoLocationTest {

    private static final Double VALID_LATITUDE = 40.7128;
    private static final Double VALID_LONGITUDE = -74.0060;
    private static final Double ALTITUDE = 10.0;
    private static final Double ACCURACY = 5.0;

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Default constructor creates empty location")
        void defaultConstructor_CreatesEmptyLocation() {
            GeoLocation location = new GeoLocation();

            assertNull(location.getLatitude());
            assertNull(location.getLongitude());
            assertNull(location.getAltitude());
            assertNull(location.getAccuracy());
        }

        @Test
        @DisplayName("Constructor with latitude and longitude")
        void constructorWithLatLong_SetsCoordinates() {
            GeoLocation location = new GeoLocation(VALID_LATITUDE, VALID_LONGITUDE);

            assertEquals(VALID_LATITUDE, location.getLatitude());
            assertEquals(VALID_LONGITUDE, location.getLongitude());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Set and get latitude")
        void setGetLatitude() {
            GeoLocation location = new GeoLocation();
            location.setLatitude(VALID_LATITUDE);

            assertEquals(VALID_LATITUDE, location.getLatitude());
        }

        @Test
        @DisplayName("Set and get longitude")
        void setGetLongitude() {
            GeoLocation location = new GeoLocation();
            location.setLongitude(VALID_LONGITUDE);

            assertEquals(VALID_LONGITUDE, location.getLongitude());
        }

        @Test
        @DisplayName("Set and get altitude")
        void setGetAltitude() {
            GeoLocation location = new GeoLocation();
            location.setAltitude(ALTITUDE);

            assertEquals(ALTITUDE, location.getAltitude());
        }

        @Test
        @DisplayName("Set and get accuracy")
        void setGetAccuracy() {
            GeoLocation location = new GeoLocation();
            location.setAccuracy(ACCURACY);

            assertEquals(ACCURACY, location.getAccuracy());
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Valid coordinates return true")
        void isValid_ValidCoordinates_ReturnsTrue() {
            GeoLocation location = new GeoLocation(VALID_LATITUDE, VALID_LONGITUDE);

            assertTrue(location.isValid());
        }

        @Test
        @DisplayName("Null latitude returns false")
        void isValid_NullLatitude_ReturnsFalse() {
            GeoLocation location = new GeoLocation(null, VALID_LONGITUDE);

            assertFalse(location.isValid());
        }

        @Test
        @DisplayName("Null longitude returns false")
        void isValid_NullLongitude_ReturnsFalse() {
            GeoLocation location = new GeoLocation(VALID_LATITUDE, null);

            assertFalse(location.isValid());
        }

        @Test
        @DisplayName("Latitude above 90 returns false")
        void isValid_LatitudeAbove90_ReturnsFalse() {
            GeoLocation location = new GeoLocation(91.0, VALID_LONGITUDE);

            assertFalse(location.isValid());
        }

        @Test
        @DisplayName("Latitude below -90 returns false")
        void isValid_LatitudeBelowMinus90_ReturnsFalse() {
            GeoLocation location = new GeoLocation(-91.0, VALID_LONGITUDE);

            assertFalse(location.isValid());
        }

        @Test
        @DisplayName("Longitude above 180 returns false")
        void isValid_LongitudeAbove180_ReturnsFalse() {
            GeoLocation location = new GeoLocation(VALID_LATITUDE, 181.0);

            assertFalse(location.isValid());
        }

        @Test
        @DisplayName("Longitude below -180 returns false")
        void isValid_LongitudeBelowMinus180_ReturnsFalse() {
            GeoLocation location = new GeoLocation(VALID_LATITUDE, -181.0);

            assertFalse(location.isValid());
        }

        @Test
        @DisplayName("Boundary values are valid")
        void isValid_BoundaryValues_AreValid() {
            GeoLocation location1 = new GeoLocation(90.0, 180.0);
            GeoLocation location2 = new GeoLocation(-90.0, -180.0);

            assertTrue(location1.isValid());
            assertTrue(location2.isValid());
        }
    }

    @Nested
    @DisplayName("Distance Calculation Tests")
    class DistanceCalculationTests {

        @Test
        @DisplayName("Distance to same location is zero")
        void distanceTo_SameLocation_IsZero() {
            GeoLocation location = new GeoLocation(VALID_LATITUDE, VALID_LONGITUDE);

            double distance = location.distanceTo(location);

            assertEquals(0.0, distance, 0.001);
        }

        @Test
        @DisplayName("Distance between New York and Los Angeles")
        void distanceTo_NYToLA_ReturnsApproximateDistance() {
            GeoLocation ny = new GeoLocation(40.7128, -74.0060);
            GeoLocation la = new GeoLocation(34.0522, -118.2437);

            double distance = ny.distanceTo(la);

            // Distance should be approximately 3944 km
            assertTrue(distance > 3900 && distance < 4000);
        }

        @Test
        @DisplayName("Distance calculation with invalid coordinates throws exception")
        void distanceTo_InvalidCoordinates_ThrowsException() {
            GeoLocation location1 = new GeoLocation(null, VALID_LONGITUDE);
            GeoLocation location2 = new GeoLocation(VALID_LATITUDE, VALID_LONGITUDE);

            assertThrows(IllegalArgumentException.class, () -> location1.distanceTo(location2));
        }
    }

    @Nested
    @DisplayName("String Representation Tests")
    class StringRepresentationTests {

        @Test
        @DisplayName("To string returns formatted coordinates")
        void toString_ReturnsFormattedCoordinates() {
            GeoLocation location = new GeoLocation(40.7128, -74.0060);

            String result = location.toString();

            assertTrue(result.contains("40.712800"));
            assertTrue(result.contains("-74.006000"));
        }

        @Test
        @DisplayName("To coordinates string returns comma separated")
        void toCoordinatesString_ReturnsCommaSeparated() {
            GeoLocation location = new GeoLocation(40.7128, -74.0060);

            String result = location.toCoordinatesString();

            assertEquals("40.7128,-74.006", result);
        }

        @Test
        @DisplayName("From string creates location")
        void fromString_CreatesLocation() {
            String coordinates = "40.7128,-74.0060";

            GeoLocation location = GeoLocation.fromString(coordinates);

            assertEquals(40.7128, location.getLatitude());
            assertEquals(-74.0060, location.getLongitude());
        }

        @Test
        @DisplayName("From string with spaces")
        void fromString_WithSpaces_CreatesLocation() {
            String coordinates = " 40.7128 , -74.0060 ";

            GeoLocation location = GeoLocation.fromString(coordinates);

            assertEquals(40.7128, location.getLatitude());
            assertEquals(-74.0060, location.getLongitude());
        }

        @Test
        @DisplayName("From string with null returns null")
        void fromString_Null_ReturnsNull() {
            GeoLocation location = GeoLocation.fromString(null);

            assertNull(location);
        }

        @Test
        @DisplayName("From string without comma returns null")
        void fromString_NoComma_ReturnsNull() {
            GeoLocation location = GeoLocation.fromString("invalid");

            assertNull(location);
        }
    }

    @Nested
    @DisplayName("Builder Pattern Tests")
    class BuilderTests {

        @Test
        @DisplayName("Builder creates complete location")
        void builder_CompleteLocation() {
            GeoLocation location = GeoLocation.builder()
                .latitude(VALID_LATITUDE)
                .longitude(VALID_LONGITUDE)
                .altitude(ALTITUDE)
                .accuracy(ACCURACY)
                .build();

            assertEquals(VALID_LATITUDE, location.getLatitude());
            assertEquals(VALID_LONGITUDE, location.getLongitude());
            assertEquals(ALTITUDE, location.getAltitude());
            assertEquals(ACCURACY, location.getAccuracy());
        }

        @Test
        @DisplayName("Builder with minimal fields")
        void builder_MinimalFields() {
            GeoLocation location = GeoLocation.builder()
                .latitude(VALID_LATITUDE)
                .longitude(VALID_LONGITUDE)
                .build();

            assertEquals(VALID_LATITUDE, location.getLatitude());
            assertEquals(VALID_LONGITUDE, location.getLongitude());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Same coordinates are equal")
        void sameCoordinates_AreEqual() {
            GeoLocation location1 = new GeoLocation(VALID_LATITUDE, VALID_LONGITUDE);
            GeoLocation location2 = new GeoLocation(VALID_LATITUDE, VALID_LONGITUDE);

            assertEquals(location1, location2);
            assertEquals(location1.hashCode(), location2.hashCode());
        }

        @Test
        @DisplayName("Different latitude are not equal")
        void differentLatitude_AreNotEqual() {
            GeoLocation location1 = new GeoLocation(VALID_LATITUDE, VALID_LONGITUDE);
            GeoLocation location2 = new GeoLocation(35.0, VALID_LONGITUDE);

            assertNotEquals(location1, location2);
        }

        @Test
        @DisplayName("Different longitude are not equal")
        void differentLongitude_AreNotEqual() {
            GeoLocation location1 = new GeoLocation(VALID_LATITUDE, VALID_LONGITUDE);
            GeoLocation location2 = new GeoLocation(VALID_LATITUDE, -70.0);

            assertNotEquals(location1, location2);
        }
    }
}
