package com.gogidix.rapidassist.common.domain.models.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DateTimeRange Value Object Tests")
class DateTimeRangeTest {

    private final LocalDateTime start = LocalDateTime.of(2024, Month.JANUARY, 1, 10, 0);
    private final LocalDateTime end = LocalDateTime.of(2024, Month.JANUARY, 1, 12, 0);
    private final LocalDateTime during = LocalDateTime.of(2024, Month.JANUARY, 1, 11, 0);
    private final LocalDateTime before = LocalDateTime.of(2024, Month.JANUARY, 1, 9, 0);
    private final LocalDateTime after = LocalDateTime.of(2024, Month.JANUARY, 1, 13, 0);

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Default constructor creates empty range")
        void defaultConstructor_CreatesEmptyRange() {
            DateTimeRange range = new DateTimeRange();

            assertNull(range.getStart());
            assertNull(range.getEnd());
        }

        @Test
        @DisplayName("Constructor with start and end")
        void constructorWithStartAndEnd_SetsRange() {
            DateTimeRange range = new DateTimeRange(start, end);

            assertEquals(start, range.getStart());
            assertEquals(end, range.getEnd());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Set and get start")
        void setGetStart() {
            DateTimeRange range = new DateTimeRange();
            range.setStart(start);

            assertEquals(start, range.getStart());
        }

        @Test
        @DisplayName("Set and get end")
        void setGetEnd() {
            DateTimeRange range = new DateTimeRange();
            range.setEnd(end);

            assertEquals(end, range.getEnd());
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Valid range returns true")
        void isValid_ValidRange_ReturnsTrue() {
            DateTimeRange range = new DateTimeRange(start, end);

            assertTrue(range.isValid());
        }

        @Test
        @DisplayName("Null start returns false")
        void isValid_NullStart_ReturnsFalse() {
            DateTimeRange range = new DateTimeRange(null, end);

            assertFalse(range.isValid());
        }

        @Test
        @DisplayName("Null end returns false")
        void isValid_NullEnd_ReturnsFalse() {
            DateTimeRange range = new DateTimeRange(start, null);

            assertFalse(range.isValid());
        }

        @Test
        @DisplayName("End before start returns false")
        void isValid_EndBeforeStart_ReturnsFalse() {
            DateTimeRange range = new DateTimeRange(end, start);

            assertFalse(range.isValid());
        }

        @Test
        @DisplayName("Same start and end is valid")
        void isValid_SameStartAndEnd_IsValid() {
            DateTimeRange range = new DateTimeRange(start, start);

            assertTrue(range.isValid());
        }
    }

    @Nested
    @DisplayName("Contains Tests")
    class ContainsTests {

        @Test
        @DisplayName("Contains datetime during range")
        void contains_DuringRange_ReturnsTrue() {
            DateTimeRange range = new DateTimeRange(start, end);

            assertTrue(range.contains(during));
        }

        @Test
        @DisplayName("Contains start time")
        void contains_StartTime_ReturnsTrue() {
            DateTimeRange range = new DateTimeRange(start, end);

            assertTrue(range.contains(start));
        }

        @Test
        @DisplayName("Contains end time")
        void contains_EndTime_ReturnsTrue() {
            DateTimeRange range = new DateTimeRange(start, end);

            assertTrue(range.contains(end));
        }

        @Test
        @DisplayName("Does not contain time before range")
        void contains_BeforeRange_ReturnsFalse() {
            DateTimeRange range = new DateTimeRange(start, end);

            assertFalse(range.contains(before));
        }

        @Test
        @DisplayName("Does not contain time after range")
        void contains_AfterRange_ReturnsFalse() {
            DateTimeRange range = new DateTimeRange(start, end);

            assertFalse(range.contains(after));
        }

        @Test
        @DisplayName("Contains null returns false")
        void contains_Null_ReturnsFalse() {
            DateTimeRange range = new DateTimeRange(start, end);

            assertFalse(range.contains(null));
        }
    }

    @Nested
    @DisplayName("Overlaps Tests")
    class OverlapsTests {

        @Test
        @DisplayName("Overlapping ranges return true")
        void overlaps_OverlappingRanges_ReturnsTrue() {
            DateTimeRange range1 = new DateTimeRange(start, end);
            DateTimeRange range2 = new DateTimeRange(during, after);

            assertTrue(range1.overlaps(range2));
            assertTrue(range2.overlaps(range1));
        }

        @Test
        @DisplayName("Adjacent ranges overlap")
        void overlaps_AdacentRanges_ReturnsTrue() {
            DateTimeRange range1 = new DateTimeRange(start, during);
            DateTimeRange range2 = new DateTimeRange(during, end);

            assertTrue(range1.overlaps(range2));
        }

        @Test
        @DisplayName("Non-overlapping ranges return false")
        void overlaps_NonOverlappingRanges_ReturnsFalse() {
            DateTimeRange range1 = new DateTimeRange(start, during);
            DateTimeRange range2 = new DateTimeRange(end, after);

            assertFalse(range1.overlaps(range2));
        }

        @Test
        @DisplayName("Overlaps with null range returns false")
        void overlaps_NullRange_ReturnsFalse() {
            DateTimeRange range1 = new DateTimeRange(start, end);

            assertFalse(range1.overlaps(null));
        }
    }

    @Nested
    @DisplayName("Duration Tests")
    class DurationTests {

        @Test
        @DisplayName("Get duration in minutes")
        void getDurationInMinutes_ReturnsCorrectDuration() {
            DateTimeRange range = new DateTimeRange(start, end);

            assertEquals(120, range.getDurationInMinutes());
        }

        @Test
        @DisplayName("Get duration in hours")
        void getDurationInHours_ReturnsCorrectDuration() {
            DateTimeRange range = new DateTimeRange(start, end);

            assertEquals(2, range.getDurationInHours());
        }

        @Test
        @DisplayName("Get duration in days")
        void getDurationInDays_ReturnsCorrectDuration() {
            LocalDateTime start = LocalDateTime.of(2024, Month.JANUARY, 1, 0, 0);
            LocalDateTime end = LocalDateTime.of(2024, Month.JANUARY, 3, 0, 0);
            DateTimeRange range = new DateTimeRange(start, end);

            assertEquals(2, range.getDurationInDays());
        }

        @Test
        @DisplayName("Duration with null dates returns zero")
        void getDuration_NullDates_ReturnsZero() {
            DateTimeRange range = new DateTimeRange();

            assertEquals(0, range.getDurationInMinutes());
            assertEquals(0, range.getDurationInHours());
            assertEquals(0, range.getDurationInDays());
        }
    }

    @Nested
    @DisplayName("Builder Pattern Tests")
    class BuilderTests {

        @Test
        @DisplayName("Builder creates complete range")
        void builder_CompleteRange() {
            DateTimeRange range = DateTimeRange.builder()
                .start(start)
                .end(end)
                .build();

            assertEquals(start, range.getStart());
            assertEquals(end, range.getEnd());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Same ranges are equal")
        void sameRanges_AreEqual() {
            DateTimeRange range1 = new DateTimeRange(start, end);
            DateTimeRange range2 = new DateTimeRange(start, end);

            assertEquals(range1, range2);
            assertEquals(range1.hashCode(), range2.hashCode());
        }

        @Test
        @DisplayName("Different start are not equal")
        void differentStart_AreNotEqual() {
            DateTimeRange range1 = new DateTimeRange(start, end);
            DateTimeRange range2 = new DateTimeRange(during, end);

            assertNotEquals(range1, range2);
        }

        @Test
        @DisplayName("Different end are not equal")
        void differentEnd_AreNotEqual() {
            DateTimeRange range1 = new DateTimeRange(start, end);
            DateTimeRange range2 = new DateTimeRange(start, during);

            assertNotEquals(range1, range2);
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("ToString returns formatted range")
        void toString_ReturnsFormattedRange() {
            DateTimeRange range = new DateTimeRange(start, end);

            String result = range.toString();

            assertTrue(result.contains(start.toString()));
            assertTrue(result.contains(end.toString()));
            assertTrue(result.contains("to"));
        }
    }
}
