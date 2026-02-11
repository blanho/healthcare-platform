package com.healthcare.appointment.util;

import java.time.Duration;
import java.time.LocalDateTime;

public final class AppointmentUtils {

    private AppointmentUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static Duration calculateDuration(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Start and end times cannot be null");
        }
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("End time must be after start time");
        }
        return Duration.between(start, end);
    }

    public static boolean isInPast(LocalDateTime appointmentTime) {
        return appointmentTime != null && appointmentTime.isBefore(LocalDateTime.now());
    }

    public static boolean isWithinNextHours(LocalDateTime appointmentTime, int hours) {
        if (appointmentTime == null) {
            return false;
        }
        LocalDateTime threshold = LocalDateTime.now().plusHours(hours);
        return appointmentTime.isBefore(threshold) && appointmentTime.isAfter(LocalDateTime.now());
    }
}
