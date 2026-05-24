package com.gogidix.rapidassist.common.domain.models.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PhoneNumber Value Object Tests")
class PhoneNumberTest {

    private static final String COUNTRY_CODE = "+1";
    private static final String NUMBER = "5551234567";
    private static final String EXTENSION = "123";
    private static final String TYPE = "MOBILE";
    private static final String CARRIER = "Verizon";

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Default constructor creates empty phone")
        void defaultConstructor_CreatesEmptyPhone() {
            PhoneNumber phone = new PhoneNumber();

            assertNull(phone.getCountryCode());
            assertNull(phone.getNumber());
            assertNull(phone.getExtension());
            assertNull(phone.getType());
            assertFalse(phone.getVerified());
            assertNull(phone.getCarrier());
        }

        @Test
        @DisplayName("Constructor with number sets phone number")
        void constructorWithNumber_SetsPhoneNumber() {
            PhoneNumber phone = new PhoneNumber(NUMBER);

            assertEquals(NUMBER, phone.getNumber());
            assertFalse(phone.getVerified());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Set and get country code")
        void setGetCountryCode() {
            PhoneNumber phone = new PhoneNumber();
            phone.setCountryCode(COUNTRY_CODE);

            assertEquals(COUNTRY_CODE, phone.getCountryCode());
        }

        @Test
        @DisplayName("Set and get number")
        void setGetNumber() {
            PhoneNumber phone = new PhoneNumber();
            phone.setNumber(NUMBER);

            assertEquals(NUMBER, phone.getNumber());
        }

        @Test
        @DisplayName("Set and get extension")
        void setGetExtension() {
            PhoneNumber phone = new PhoneNumber();
            phone.setExtension(EXTENSION);

            assertEquals(EXTENSION, phone.getExtension());
        }

        @Test
        @DisplayName("Set and get type")
        void setGetType() {
            PhoneNumber phone = new PhoneNumber();
            phone.setType(TYPE);

            assertEquals(TYPE, phone.getType());
        }

        @Test
        @DisplayName("Set and get verified")
        void setGetVerified() {
            PhoneNumber phone = new PhoneNumber();
            phone.setVerified(true);

            assertTrue(phone.getVerified());
        }

        @Test
        @DisplayName("Set and get carrier")
        void setGetCarrier() {
            PhoneNumber phone = new PhoneNumber();
            phone.setCarrier(CARRIER);

            assertEquals(CARRIER, phone.getCarrier());
        }
    }

    @Nested
    @DisplayName("Full Number Tests")
    class FullNumberTests {

        @Test
        @DisplayName("Get full number with country code")
        void getFullNumber_WithCountryCode() {
            PhoneNumber phone = new PhoneNumber();
            phone.setCountryCode(COUNTRY_CODE);
            phone.setNumber(NUMBER);

            assertEquals("+15551234567", phone.getFullNumber());
        }

        @Test
        @DisplayName("Get full number without + in country code")
        void getFullNumber_CountryCodeWithoutPlus() {
            PhoneNumber phone = new PhoneNumber();
            phone.setCountryCode("1");
            phone.setNumber(NUMBER);

            assertEquals("+15551234567", phone.getFullNumber());
        }

        @Test
        @DisplayName("Get full number with extension")
        void getFullNumber_WithExtension() {
            PhoneNumber phone = new PhoneNumber();
            phone.setCountryCode(COUNTRY_CODE);
            phone.setNumber(NUMBER);
            phone.setExtension(EXTENSION);

            assertEquals("+15551234567 ext.123", phone.getFullNumber());
        }

        @Test
        @DisplayName("Get full number without country code")
        void getFullNumber_WithoutCountryCode() {
            PhoneNumber phone = new PhoneNumber();
            phone.setNumber(NUMBER);

            assertEquals("5551234567", phone.getFullNumber());
        }
    }

    @Nested
    @DisplayName("National Format Tests")
    class NationalFormatTests {

        @Test
        @DisplayName("Get national format returns number")
        void getNationalFormat_ReturnsNumber() {
            PhoneNumber phone = new PhoneNumber();
            phone.setNumber(NUMBER);

            assertEquals(NUMBER, phone.getNationalFormat());
        }
    }

    @Nested
    @DisplayName("Builder Pattern Tests")
    class BuilderTests {

        @Test
        @DisplayName("Builder creates complete phone number")
        void builder_CompletePhoneNumber() {
            PhoneNumber phone = PhoneNumber.builder()
                .countryCode(COUNTRY_CODE)
                .number(NUMBER)
                .extension(EXTENSION)
                .type(TYPE)
                .verified(true)
                .build();

            assertEquals(COUNTRY_CODE, phone.getCountryCode());
            assertEquals(NUMBER, phone.getNumber());
            assertEquals(EXTENSION, phone.getExtension());
            assertEquals(TYPE, phone.getType());
            assertTrue(phone.getVerified());
        }

        @Test
        @DisplayName("Builder with minimal fields")
        void builder_MinimalFields() {
            PhoneNumber phone = PhoneNumber.builder()
                .number(NUMBER)
                .build();

            assertEquals(NUMBER, phone.getNumber());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Same phone numbers are equal")
        void samePhoneNumbers_AreEqual() {
            PhoneNumber phone1 = PhoneNumber.builder()
                .countryCode(COUNTRY_CODE)
                .number(NUMBER)
                .build();
            PhoneNumber phone2 = PhoneNumber.builder()
                .countryCode(COUNTRY_CODE)
                .number(NUMBER)
                .build();

            assertEquals(phone1, phone2);
            assertEquals(phone1.hashCode(), phone2.hashCode());
        }

        @Test
        @DisplayName("Different country codes are not equal")
        void differentCountryCodes_AreNotEqual() {
            PhoneNumber phone1 = PhoneNumber.builder()
                .countryCode(COUNTRY_CODE)
                .number(NUMBER)
                .build();
            PhoneNumber phone2 = PhoneNumber.builder()
                .countryCode("+44")
                .number(NUMBER)
                .build();

            assertNotEquals(phone1, phone2);
        }

        @Test
        @DisplayName("Different numbers are not equal")
        void differentNumbers_AreNotEqual() {
            PhoneNumber phone1 = PhoneNumber.builder()
                .number(NUMBER)
                .build();
            PhoneNumber phone2 = PhoneNumber.builder()
                .number("9998887777")
                .build();

            assertNotEquals(phone1, phone2);
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("ToString returns full number")
        void toString_ReturnsFullNumber() {
            PhoneNumber phone = PhoneNumber.builder()
                .countryCode(COUNTRY_CODE)
                .number(NUMBER)
                .build();

            assertEquals(phone.getFullNumber(), phone.toString());
        }
    }
}
