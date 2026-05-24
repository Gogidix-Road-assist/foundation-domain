package com.gogidix.rapidassist.common.domain.models.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("EmailAddress Value Object Tests")
class EmailAddressTest {

    private static final String VALID_EMAIL = "test@example.com";
    private static final String ANOTHER_EMAIL = "another@test.com";

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Default constructor creates empty email")
        void defaultConstructor_CreatesEmptyEmail() {
            EmailAddress email = new EmailAddress();

            assertNull(email.getEmail());
            assertFalse(email.getVerified());
            assertFalse(email.getPrimary());
        }

        @Test
        @DisplayName("Constructor with email sets email address")
        void constructorWithEmail_SetsEmailAddress() {
            EmailAddress email = new EmailAddress(VALID_EMAIL);

            assertEquals(VALID_EMAIL, email.getEmail());
            assertFalse(email.getVerified());
            assertFalse(email.getPrimary());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Set and get email")
        void setGetEmail() {
            EmailAddress email = new EmailAddress();
            email.setEmail(VALID_EMAIL);

            assertEquals(VALID_EMAIL, email.getEmail());
        }

        @Test
        @DisplayName("Set and get verified status")
        void setGetVerified() {
            EmailAddress email = new EmailAddress();
            email.setVerified(true);

            assertTrue(email.getVerified());
        }

        @Test
        @DisplayName("Set and get primary status")
        void setGetPrimary() {
            EmailAddress email = new EmailAddress();
            email.setPrimary(true);

            assertTrue(email.getPrimary());
        }
    }

    @Nested
    @DisplayName("Email Parsing Tests")
    class EmailParsingTests {

        @Test
        @DisplayName("Get local part from valid email")
        void getLocalPart_ValidEmail() {
            EmailAddress email = new EmailAddress("user@domain.com");

            assertEquals("user", email.getLocalPart());
        }

        @Test
        @DisplayName("Get domain from valid email")
        void getDomain_ValidEmail() {
            EmailAddress email = new EmailAddress("user@domain.com");

            assertEquals("domain.com", email.getDomain());
        }

        @Test
        @DisplayName("Get local part returns null for null email")
        void getLocalPart_NullEmail() {
            EmailAddress email = new EmailAddress();

            assertNull(email.getLocalPart());
        }

        @Test
        @DisplayName("Get local part returns email when no @ symbol")
        void getLocalPart_NoAtSymbol() {
            EmailAddress email = new EmailAddress("invalidemail");

            assertEquals("invalidemail", email.getLocalPart());
        }

        @Test
        @DisplayName("Get domain returns null for null email")
        void getDomain_NullEmail() {
            EmailAddress email = new EmailAddress();

            assertNull(email.getDomain());
        }

        @Test
        @DisplayName("Get domain returns null when no @ symbol")
        void getDomain_NoAtSymbol() {
            EmailAddress email = new EmailAddress("invalidemail");

            assertNull(email.getDomain());
        }
    }

    @Nested
    @DisplayName("Builder Pattern Tests")
    class BuilderTests {

        @Test
        @DisplayName("Builder creates complete email address")
        void builder_CompleteEmailAddress() {
            EmailAddress email = EmailAddress.builder()
                .email(VALID_EMAIL)
                .verified(true)
                .primary(true)
                .build();

            assertEquals(VALID_EMAIL, email.getEmail());
            assertTrue(email.getVerified());
            assertTrue(email.getPrimary());
        }

        @Test
        @DisplayName("Builder with only email")
        void builder_OnlyEmail() {
            EmailAddress email = EmailAddress.builder()
                .email(VALID_EMAIL)
                .build();

            assertEquals(VALID_EMAIL, email.getEmail());
            assertFalse(email.getVerified());
            assertFalse(email.getPrimary());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Same email addresses are equal")
        void sameEmailAddresses_AreEqual() {
            EmailAddress email1 = new EmailAddress(VALID_EMAIL);
            EmailAddress email2 = new EmailAddress(VALID_EMAIL);

            assertEquals(email1, email2);
            assertEquals(email1.hashCode(), email2.hashCode());
        }

        @Test
        @DisplayName("Different email addresses are not equal")
        void differentEmailAddresses_AreNotEqual() {
            EmailAddress email1 = new EmailAddress(VALID_EMAIL);
            EmailAddress email2 = new EmailAddress(ANOTHER_EMAIL);

            assertNotEquals(email1, email2);
        }

        @Test
        @DisplayName("Email address equals itself")
        void emailAddress_EqualsItself() {
            EmailAddress email = new EmailAddress(VALID_EMAIL);

            assertEquals(email, email);
        }

        @Test
        @DisplayName("Email address not equal to null")
        void emailAddress_NotEqualToNull() {
            EmailAddress email = new EmailAddress(VALID_EMAIL);

            assertNotEquals(null, email);
        }

        @Test
        @DisplayName("Email address not equal to different type")
        void emailAddress_NotEqualToDifferentType() {
            EmailAddress email = new EmailAddress(VALID_EMAIL);

            assertNotEquals(email, "string");
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("ToString returns email string")
        void toString_ReturnsEmailString() {
            EmailAddress email = new EmailAddress(VALID_EMAIL);

            assertEquals(VALID_EMAIL, email.toString());
        }

        @Test
        @DisplayName("ToString returns null for null email")
        void toString_NullEmail() {
            EmailAddress email = new EmailAddress();

            assertNull(email.toString());
        }
    }
}
