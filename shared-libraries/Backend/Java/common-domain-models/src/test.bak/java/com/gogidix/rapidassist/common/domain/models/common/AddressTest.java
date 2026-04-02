package com.gogidix.rapidassist.common.domain.models.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Address Value Object Tests")
class AddressTest {

    private static final String STREET_LINE_1 = "123 Main St";
    private static final String STREET_LINE_2 = "Apt 4B";
    private static final String CITY = "New York";
    private static final String STATE = "NY";
    private static final String POSTAL_CODE = "10001";
    private static final String COUNTRY = "USA";
    private static final String COUNTRY_CODE = "US";
    private static final Double LATITUDE = 40.7128;
    private static final Double LONGITUDE = -74.0060;

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Default constructor creates empty address")
        void defaultConstructor_CreatesEmptyAddress() {
            Address address = new Address();

            assertNull(address.getStreetLine1());
            assertNull(address.getCity());
            assertNull(address.getState());
            assertNull(address.getPostalCode());
            assertNull(address.getCountry());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Set and get street line 1")
        void setGetStreetLine1() {
            Address address = new Address();
            address.setStreetLine1(STREET_LINE_1);

            assertEquals(STREET_LINE_1, address.getStreetLine1());
        }

        @Test
        @DisplayName("Set and get street line 2")
        void setGetStreetLine2() {
            Address address = new Address();
            address.setStreetLine2(STREET_LINE_2);

            assertEquals(STREET_LINE_2, address.getStreetLine2());
        }

        @Test
        @DisplayName("Set and get city")
        void setGetCity() {
            Address address = new Address();
            address.setCity(CITY);

            assertEquals(CITY, address.getCity());
        }

        @Test
        @DisplayName("Set and get state")
        void setGetState() {
            Address address = new Address();
            address.setState(STATE);

            assertEquals(STATE, address.getState());
        }

        @Test
        @DisplayName("Set and get postal code")
        void setGetPostalCode() {
            Address address = new Address();
            address.setPostalCode(POSTAL_CODE);

            assertEquals(POSTAL_CODE, address.getPostalCode());
        }

        @Test
        @DisplayName("Set and get country")
        void setGetCountry() {
            Address address = new Address();
            address.setCountry(COUNTRY);

            assertEquals(COUNTRY, address.getCountry());
        }

        @Test
        @DisplayName("Set and get country code")
        void setGetCountryCode() {
            Address address = new Address();
            address.setCountryCode(COUNTRY_CODE);

            assertEquals(COUNTRY_CODE, address.getCountryCode());
        }

        @Test
        @DisplayName("Set and get latitude")
        void setGetLatitude() {
            Address address = new Address();
            address.setLatitude(LATITUDE);

            assertEquals(LATITUDE, address.getLatitude());
        }

        @Test
        @DisplayName("Set and get longitude")
        void setGetLongitude() {
            Address address = new Address();
            address.setLongitude(LONGITUDE);

            assertEquals(LONGITUDE, address.getLongitude());
        }

        @Test
        @DisplayName("Set and get address type")
        void setGetAddressType() {
            Address address = new Address();
            address.setAddressType("HOME");

            assertEquals("HOME", address.getAddressType());
        }
    }

    @Nested
    @DisplayName("Has Coordinates Tests")
    class HasCoordinatesTests {

        @Test
        @DisplayName("Has coordinates returns true when both set")
        void hasCoordinates_BothSet_ReturnsTrue() {
            Address address = new Address();
            address.setLatitude(LATITUDE);
            address.setLongitude(LONGITUDE);

            assertTrue(address.hasCoordinates());
        }

        @Test
        @DisplayName("Has coordinates returns false when latitude null")
        void hasCoordinates_LatitudeNull_ReturnsFalse() {
            Address address = new Address();
            address.setLongitude(LONGITUDE);

            assertFalse(address.hasCoordinates());
        }

        @Test
        @DisplayName("Has coordinates returns false when longitude null")
        void hasCoordinates_LongitudeNull_ReturnsFalse() {
            Address address = new Address();
            address.setLatitude(LATITUDE);

            assertFalse(address.hasCoordinates());
        }

        @Test
        @DisplayName("Has coordinates returns false when both null")
        void hasCoordinates_BothNull_ReturnsFalse() {
            Address address = new Address();

            assertFalse(address.hasCoordinates());
        }
    }

    @Nested
    @DisplayName("Get Full Address Tests")
    class GetFullAddressTests {

        @Test
        @DisplayName("Get full address returns formatted address when set")
        void getFullAddress_FormattedAddressSet_ReturnsFormatted() {
            Address address = new Address();
            address.setFormattedAddress("Custom Formatted Address");

            assertEquals("Custom Formatted Address", address.getFullAddress());
        }

        @Test
        @DisplayName("Get full address builds from components")
        void getFullAddress_BuildsFromComponents() {
            Address address = new Address();
            address.setStreetLine1(STREET_LINE_1);
            address.setStreetLine2(STREET_LINE_2);
            address.setCity(CITY);
            address.setState(STATE);
            address.setPostalCode(POSTAL_CODE);
            address.setCountry(COUNTRY);

            String fullAddress = address.getFullAddress();

            assertTrue(fullAddress.contains(STREET_LINE_1));
            assertTrue(fullAddress.contains(STREET_LINE_2));
            assertTrue(fullAddress.contains(CITY));
            assertTrue(fullAddress.contains(STATE));
            assertTrue(fullAddress.contains(POSTAL_CODE));
            assertTrue(fullAddress.contains(COUNTRY));
        }

        @Test
        @DisplayName("Get full address with only street line 1")
        void getFullAddress_OnlyStreetLine1() {
            Address address = new Address();
            address.setStreetLine1(STREET_LINE_1);

            assertEquals(STREET_LINE_1, address.getFullAddress());
        }
    }

    @Nested
    @DisplayName("Builder Pattern Tests")
    class BuilderTests {

        @Test
        @DisplayName("Builder creates complete address")
        void builder_CompleteAddress() {
            Address address = Address.builder()
                .streetLine1(STREET_LINE_1)
                .streetLine2(STREET_LINE_2)
                .city(CITY)
                .state(STATE)
                .postalCode(POSTAL_CODE)
                .country(COUNTRY)
                .countryCode(COUNTRY_CODE)
                .latitude(LATITUDE)
                .longitude(LONGITUDE)
                .addressType("HOME")
                .build();

            assertEquals(STREET_LINE_1, address.getStreetLine1());
            assertEquals(CITY, address.getCity());
            assertEquals(STATE, address.getState());
            assertEquals(LATITUDE, address.getLatitude());
            assertEquals(LONGITUDE, address.getLongitude());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Same addresses are equal")
        void sameAddresses_AreEqual() {
            Address address1 = createTestAddress();
            Address address2 = createTestAddress();

            assertEquals(address1, address2);
            assertEquals(address1.hashCode(), address2.hashCode());
        }

        @Test
        @DisplayName("Different street line 1 are not equal")
        void differentStreetLine1_AreNotEqual() {
            Address address1 = createTestAddress();
            Address address2 = createTestAddress();
            address2.setStreetLine1("456 Different St");

            assertNotEquals(address1, address2);
        }

        @Test
        @DisplayName("Different city are not equal")
        void differentCity_AreNotEqual() {
            Address address1 = createTestAddress();
            Address address2 = createTestAddress();
            address2.setCity("Los Angeles");

            assertNotEquals(address1, address2);
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("ToString returns full address")
        void toString_ReturnsFullAddress() {
            Address address = createTestAddress();

            assertEquals(address.getFullAddress(), address.toString());
        }
    }

    private Address createTestAddress() {
        Address address = new Address();
        address.setStreetLine1(STREET_LINE_1);
        address.setCity(CITY);
        address.setState(STATE);
        address.setPostalCode(POSTAL_CODE);
        address.setCountryCode(COUNTRY_CODE);
        return address;
    }
}
