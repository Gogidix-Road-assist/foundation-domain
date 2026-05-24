package com.gogidix.rapidassist.common.domain.models.common;

import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * DateTimeRange value object representing a time period.
 */
@Embeddable
public class DateTimeRange {

    private LocalDateTime start;

    private LocalDateTime end;

    public DateTimeRange() {
    }

    public DateTimeRange(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters and Setters
    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public boolean isValid() {
        return start != null && end != null && !end.isBefore(start);
    }

    public boolean contains(LocalDateTime dateTime) {
        return dateTime != null &&
               !dateTime.isBefore(start) &&
               !dateTime.isAfter(end);
    }

    public boolean overlaps(DateTimeRange other) {
        return other != null &&
               this.start != null && this.end != null &&
               other.start != null && other.end != null &&
               !this.end.isBefore(other.start) &&
               !this.start.isAfter(other.end);
    }

    public long getDurationInMinutes() {
        if (start == null || end == null) return 0;
        return ChronoUnit.MINUTES.between(start, end);
    }

    public long getDurationInHours() {
        if (start == null || end == null) return 0;
        return ChronoUnit.HOURS.between(start, end);
    }

    public long getDurationInDays() {
        if (start == null || end == null) return 0;
        return ChronoUnit.DAYS.between(start, end);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DateTimeRange)) return false;
        DateTimeRange that = (DateTimeRange) o;
        return Objects.equals(start, that.start) && Objects.equals(end, that.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }

    @Override
    public String toString() {
        return start + " to " + end;
    }

    public static class Builder {
        private DateTimeRange range = new DateTimeRange();

        public Builder start(LocalDateTime start) {
            range.setStart(start);
            return this;
        }

        public Builder end(LocalDateTime end) {
            range.setEnd(end);
            return this;
        }

        public DateTimeRange build() {
            return range;
        }
    }
}
