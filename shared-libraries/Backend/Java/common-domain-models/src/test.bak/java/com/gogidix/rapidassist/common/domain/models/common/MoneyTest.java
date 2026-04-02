package com.gogidix.rapidassist.common.domain.models.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Money Value Object Tests")
class MoneyTest {

    private static final String USD = "USD";
    private static final String EUR = "EUR";
    private static final BigDecimal AMOUNT = new BigDecimal("100.50");
    private static final BigDecimal ANOTHER_AMOUNT = new BigDecimal("50.25");

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Default constructor creates USD zero")
        void defaultConstructor_CreatesUSDZero() {
            Money money = new Money();

            assertEquals("USD", money.getCurrency());
            assertNull(money.getAmount());
        }

        @Test
        @DisplayName("Constructor with amount and currency")
        void constructorWithAmountAndCurrency() {
            Money money = new Money(AMOUNT, USD);

            assertEquals(USD, money.getCurrency());
            assertEquals(0, AMOUNT.compareTo(money.getAmount()));
        }

        @Test
        @DisplayName("Constructor scales amount to 2 decimal places")
        void constructor_ScalesAmount() {
            Money money = new Money(new BigDecimal("100.456"), USD);

            assertEquals(0, new BigDecimal("100.46").compareTo(money.getAmount()));
        }

        @Test
        @DisplayName("Constructor with string amount and currency")
        void constructorWithStringAmountAndCurrency() {
            Money money = new Money("100.50", USD);

            assertEquals(USD, money.getCurrency());
            assertEquals(0, AMOUNT.compareTo(money.getAmount()));
        }
    }

    @Nested
    @DisplayName("Factory Methods Tests")
    class FactoryMethodsTests {

        @Test
        @DisplayName("of() creates money with amount and currency")
        void of_CreatesMoney() {
            Money money = Money.of(AMOUNT, USD);

            assertEquals(USD, money.getCurrency());
            assertEquals(0, AMOUNT.compareTo(money.getAmount()));
        }

        @Test
        @DisplayName("usd() creates USD money")
        void usd_CreatesUSDMoney() {
            Money money = Money.usd(AMOUNT);

            assertEquals(USD, money.getCurrency());
            assertEquals(0, AMOUNT.compareTo(money.getAmount()));
        }

        @Test
        @DisplayName("eur() creates EUR money")
        void eur_CreatesEURMoney() {
            Money money = Money.eur(AMOUNT);

            assertEquals(EUR, money.getCurrency());
            assertEquals(0, AMOUNT.compareTo(money.getAmount()));
        }

        @Test
        @DisplayName("gbp() creates GBP money")
        void gbp_CreatesGBPMoney() {
            Money money = Money.gbp(AMOUNT);

            assertEquals("GBP", money.getCurrency());
            assertEquals(0, AMOUNT.compareTo(money.getAmount()));
        }

        @Test
        @DisplayName("ZERO constant is zero USD")
        void zeroConstant_IsZeroUSD() {
            Money zero = Money.ZERO;

            assertTrue(zero.isZero());
            assertEquals(USD, zero.getCurrency());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Set and get amount")
        void setGetAmount() {
            Money money = new Money();
            money.setAmount(AMOUNT);

            assertEquals(0, AMOUNT.compareTo(money.getAmount()));
        }

        @Test
        @DisplayName("Set amount scales to 2 decimal places")
        void setAmount_ScalesToTwoDecimals() {
            Money money = new Money();
            money.setAmount(new BigDecimal("100.456"));

            assertEquals(0, new BigDecimal("100.46").compareTo(money.getAmount()));
        }

        @Test
        @DisplayName("Set and get currency")
        void setGetCurrency() {
            Money money = new Money();
            money.setCurrency(EUR);

            assertEquals(EUR, money.getCurrency());
        }
    }

    @Nested
    @DisplayName("Arithmetic Operations Tests")
    class ArithmeticOperationsTests {

        @Test
        @DisplayName("Add same currency money")
        void add_SameCurrency() {
            Money money1 = Money.of(AMOUNT, USD);
            Money money2 = Money.of(ANOTHER_AMOUNT, USD);

            Money result = money1.add(money2);

            assertEquals(0, new BigDecimal("150.75").compareTo(result.getAmount()));
            assertEquals(USD, result.getCurrency());
        }

        @Test
        @DisplayName("Add different currency throws exception")
        void add_DifferentCurrency_ThrowsException() {
            Money money1 = Money.of(AMOUNT, USD);
            Money money2 = Money.of(ANOTHER_AMOUNT, EUR);

            assertThrows(IllegalArgumentException.class, () -> money1.add(money2));
        }

        @Test
        @DisplayName("Subtract same currency money")
        void subtract_SameCurrency() {
            Money money1 = Money.of(AMOUNT, USD);
            Money money2 = Money.of(ANOTHER_AMOUNT, USD);

            Money result = money1.subtract(money2);

            assertEquals(0, new BigDecimal("50.25").compareTo(result.getAmount()));
            assertEquals(USD, result.getCurrency());
        }

        @Test
        @DisplayName("Subtract different currency throws exception")
        void subtract_DifferentCurrency_ThrowsException() {
            Money money1 = Money.of(AMOUNT, USD);
            Money money2 = Money.of(ANOTHER_AMOUNT, EUR);

            assertThrows(IllegalArgumentException.class, () -> money1.subtract(money2));
        }

        @Test
        @DisplayName("Multiply by decimal")
        void multiply_ByDecimal() {
            Money money = Money.of(new BigDecimal("100"), USD);

            Money result = money.multiply(new BigDecimal("2.5"));

            assertEquals(0, new BigDecimal("250.00").compareTo(result.getAmount()));
            assertEquals(USD, result.getCurrency());
        }

        @Test
        @DisplayName("Divide by decimal")
        void divide_ByDecimal() {
            Money money = Money.of(new BigDecimal("100"), USD);

            Money result = money.divide(new BigDecimal("4"));

            assertEquals(0, new BigDecimal("25.00").compareTo(result.getAmount()));
            assertEquals(USD, result.getCurrency());
        }

        @Test
        @DisplayName("Calculate percentage")
        void percentage() {
            Money money = Money.of(new BigDecimal("200"), USD);

            Money result = money.percentage(new BigDecimal("15"));

            assertEquals(0, new BigDecimal("30.00").compareTo(result.getAmount()));
            assertEquals(USD, result.getCurrency());
        }
    }

    @Nested
    @DisplayName("Comparison Operations Tests")
    class ComparisonOperationsTests {

        @Test
        @DisplayName("Is greater than returns true when greater")
        void isGreaterThan_TrueWhenGreater() {
            Money money1 = Money.of(new BigDecimal("100"), USD);
            Money money2 = Money.of(new BigDecimal("50"), USD);

            assertTrue(money1.isGreaterThan(money2));
        }

        @Test
        @DisplayName("Is greater than returns false when not greater")
        void isGreaterThan_FalseWhenNotGreater() {
            Money money1 = Money.of(new BigDecimal("50"), USD);
            Money money2 = Money.of(new BigDecimal("100"), USD);

            assertFalse(money1.isGreaterThan(money2));
        }

        @Test
        @DisplayName("Is greater than different currency throws exception")
        void isGreaterThan_DifferentCurrency_ThrowsException() {
            Money money1 = Money.of(AMOUNT, USD);
            Money money2 = Money.of(ANOTHER_AMOUNT, EUR);

            assertThrows(IllegalArgumentException.class, () -> money1.isGreaterThan(money2));
        }

        @Test
        @DisplayName("Is less than returns true when less")
        void isLessThan_TrueWhenLess() {
            Money money1 = Money.of(new BigDecimal("50"), USD);
            Money money2 = Money.of(new BigDecimal("100"), USD);

            assertTrue(money1.isLessThan(money2));
        }

        @Test
        @DisplayName("Is less than returns false when not less")
        void isLessThan_FalseWhenNotLess() {
            Money money1 = Money.of(new BigDecimal("100"), USD);
            Money money2 = Money.of(new BigDecimal("50"), USD);

            assertFalse(money1.isLessThan(money2));
        }

        @Test
        @DisplayName("Is zero returns true when zero")
        void isZero_TrueWhenZero() {
            Money money = Money.of(BigDecimal.ZERO, USD);

            assertTrue(money.isZero());
        }

        @Test
        @DisplayName("Is zero returns true when null")
        void isZero_TrueWhenNull() {
            Money money = new Money();

            assertTrue(money.isZero());
        }

        @Test
        @DisplayName("Is zero returns false when not zero")
        void isZero_FalseWhenNotZero() {
            Money money = Money.of(AMOUNT, USD);

            assertFalse(money.isZero());
        }

        @Test
        @DisplayName("Is positive returns true when positive")
        void isPositive_TrueWhenPositive() {
            Money money = Money.of(AMOUNT, USD);

            assertTrue(money.isPositive());
        }

        @Test
        @DisplayName("Is positive returns false when zero")
        void isPositive_FalseWhenZero() {
            Money money = Money.of(BigDecimal.ZERO, USD);

            assertFalse(money.isPositive());
        }

        @Test
        @DisplayName("Is positive returns false when null")
        void isPositive_FalseWhenNull() {
            Money money = new Money();

            assertFalse(money.isPositive());
        }

        @Test
        @DisplayName("Is negative returns true when negative")
        void isNegative_TrueWhenNegative() {
            Money money = Money.of(new BigDecimal("-10"), USD);

            assertTrue(money.isNegative());
        }

        @Test
        @DisplayName("Is negative returns false when positive")
        void isNegative_FalseWhenPositive() {
            Money money = Money.of(AMOUNT, USD);

            assertFalse(money.isNegative());
        }
    }

    @Nested
    @DisplayName("Formatting Tests")
    class FormattingTests {

        @Test
        @DisplayName("Get formatted string")
        void getFormattedString() {
            Money money = Money.of(new BigDecimal("1234.56"), USD);

            String formatted = money.getFormattedString();

            assertTrue(formatted.contains("$"));
            assertTrue(formatted.contains("1,234.56"));
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Same amount and currency are equal")
        void sameAmountAndCurrency_AreEqual() {
            Money money1 = Money.of(AMOUNT, USD);
            Money money2 = Money.of(AMOUNT, USD);

            assertEquals(money1, money2);
            assertEquals(money1.hashCode(), money2.hashCode());
        }

        @Test
        @DisplayName("Different amounts are not equal")
        void differentAmounts_AreNotEqual() {
            Money money1 = Money.of(AMOUNT, USD);
            Money money2 = Money.of(ANOTHER_AMOUNT, USD);

            assertNotEquals(money1, money2);
        }

        @Test
        @DisplayName("Different currencies are not equal")
        void differentCurrencies_AreNotEqual() {
            Money money1 = Money.of(AMOUNT, USD);
            Money money2 = Money.of(AMOUNT, EUR);

            assertNotEquals(money1, money2);
        }
    }
}
