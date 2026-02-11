package com.healthcare.provider.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record ScheduleRequest(

    @NotNull(message = "Day of week is required")
    DayOfWeek dayOfWeek,

    @NotNull(message = "Start time is required")
    LocalTime startTime,

    @NotNull(message = "End time is required")
    LocalTime endTime,

    @Min(value = 5, message = "Slot duration must be at least 5 minutes")
    @Max(value = 120, message = "Slot duration must be at most 120 minutes")
    int slotDurationMinutes
) {

    public ScheduleRequest {
        if (slotDurationMinutes <= 0) {
            slotDurationMinutes = 30;
        }
    }
}
