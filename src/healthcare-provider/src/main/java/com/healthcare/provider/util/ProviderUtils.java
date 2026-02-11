package com.healthcare.provider.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public final class ProviderUtils {

    private ProviderUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static boolean isAvailable(LocalDateTime checkTime,
                                     LocalTime workStartTime,
                                     LocalTime workEndTime) {
        if (checkTime == null || workStartTime == null || workEndTime == null) {
            return false;
        }

        LocalTime timeToCheck = checkTime.toLocalTime();
        return !timeToCheck.isBefore(workStartTime) && !timeToCheck.isAfter(workEndTime);
    }

    public static boolean isWeekend(LocalDate date) {
        if (date == null) {
            return false;
        }
        DayOfWeek day = date.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }

    public static String formatProviderName(String firstName, String lastName, String qualification) {
        StringBuilder name = new StringBuilder();
        if (firstName != null && !firstName.isEmpty()) {
            name.append(firstName).append(" ");
        }
        if (lastName != null && !lastName.isEmpty()) {
            name.append(lastName);
        }
        if (qualification != null && !qualification.isEmpty()) {
            name.append(", ").append(qualification);
        }
        return name.toString().trim();
    }

    public static int calculateYearsInPractice(LocalDate licenseDate) {
        if (licenseDate == null) {
            return 0;
        }
        return java.time.Period.between(licenseDate, LocalDate.now()).getYears();
    }
}
