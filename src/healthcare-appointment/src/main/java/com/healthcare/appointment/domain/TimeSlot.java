package com.healthcare.appointment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

@Embeddable
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TimeSlot {

    @Column(name = "scheduled_date", nullable = false)
    private LocalDate date;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "duration_minutes", nullable = false)
    private int durationMinutes;

    public static TimeSlot of(LocalDate date, LocalTime startTime, int durationMinutes) {
        Objects.requireNonNull(date, "Date is required");
        Objects.requireNonNull(startTime, "Start time is required");
        if (durationMinutes <= 0) {
            throw new IllegalArgumentException("Duration must be positive");
        }

        LocalTime endTime = startTime.plusMinutes(durationMinutes);
        return new TimeSlot(date, startTime, endTime, durationMinutes);
    }

    public LocalDateTime getStartDateTime() {
        return LocalDateTime.of(date, startTime);
    }

    public LocalDate getScheduledDate() {
        return date;
    }

    public LocalDateTime getEndDateTime() {
        return LocalDateTime.of(date, endTime);
    }

    public boolean overlaps(TimeSlot other) {
        if (!this.date.equals(other.date)) {
            return false;
        }
        return this.startTime.isBefore(other.endTime) && other.startTime.isBefore(this.endTime);
    }

    public boolean isPast() {
        return getStartDateTime().isBefore(LocalDateTime.now());
    }

    public boolean isToday() {
        return date.equals(LocalDate.now());
    }

    public boolean contains(LocalTime time) {
        return !time.isBefore(startTime) && time.isBefore(endTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeSlot timeSlot = (TimeSlot) o;
        return durationMinutes == timeSlot.durationMinutes &&
               Objects.equals(date, timeSlot.date) &&
               Objects.equals(startTime, timeSlot.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, startTime, durationMinutes);
    }

    @Override
    public String toString() {
        return date + " " + startTime + "-" + endTime;
    }
}
