package com.healthcare.provider.domain;

import com.healthcare.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Objects;

@Entity
@Table(name = "provider_schedules",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_provider_schedule_day",
        columnNames = {"provider_id", "day_of_week"}
    ),
    indexes = {
        @Index(name = "idx_provider_schedule_provider", columnList = "provider_id"),
        @Index(name = "idx_provider_schedule_active", columnList = "is_active")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProviderSchedule extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    @Setter(AccessLevel.PACKAGE)
    private Provider provider;

    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(name = "start_time", nullable = false)
    @Setter
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    @Setter
    private LocalTime endTime;

    @Column(name = "slot_duration_minutes", nullable = false)
    @Setter
    private int slotDurationMinutes = 30;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Builder
    private ProviderSchedule(
            Provider provider,
            DayOfWeek dayOfWeek,
            LocalTime startTime,
            LocalTime endTime,
            int slotDurationMinutes,
            boolean active) {
        this.provider = provider;
        this.dayOfWeek = Objects.requireNonNull(dayOfWeek, "Day of week is required");
        this.startTime = Objects.requireNonNull(startTime, "Start time is required");
        this.endTime = Objects.requireNonNull(endTime, "End time is required");
        this.slotDurationMinutes = slotDurationMinutes > 0 ? slotDurationMinutes : 30;
        this.active = active;

        validateTimeRange();
    }

    private void validateTimeRange() {
        if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
    }

    public Duration getWorkDuration() {
        return Duration.between(startTime, endTime);
    }

    public int getAvailableSlotCount() {
        long minutes = getWorkDuration().toMinutes();
        return (int) (minutes / slotDurationMinutes);
    }

    public boolean isTimeWithinSchedule(LocalTime time) {
        return !time.isBefore(startTime) && time.isBefore(endTime);
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public void updateTimeSlot(LocalTime startTime, LocalTime endTime) {
        this.startTime = Objects.requireNonNull(startTime, "Start time is required");
        this.endTime = Objects.requireNonNull(endTime, "End time is required");
        validateTimeRange();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProviderSchedule that = (ProviderSchedule) o;
        return Objects.equals(provider != null ? provider.getId() : null,
                              that.provider != null ? that.provider.getId() : null) &&
               dayOfWeek == that.dayOfWeek;
    }

    @Override
    public int hashCode() {
        return Objects.hash(provider != null ? provider.getId() : null, dayOfWeek);
    }
}
