package com.healthcare.patient.util;

import java.time.LocalDate;
import java.time.Period;

public final class PatientUtils {

    private PatientUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static int calculateAge(LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            return 0;
        }
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    public static boolean isMinor(LocalDate dateOfBirth) {
        return calculateAge(dateOfBirth) < 18;
    }

    public static boolean isSenior(LocalDate dateOfBirth) {
        return calculateAge(dateOfBirth) >= 65;
    }

    public static String formatFullName(String firstName, String middleName, String lastName) {
        StringBuilder fullName = new StringBuilder();
        if (firstName != null && !firstName.isEmpty()) {
            fullName.append(firstName);
        }
        if (middleName != null && !middleName.isEmpty()) {
            if (fullName.length() > 0) {
                fullName.append(" ");
            }
            fullName.append(middleName);
        }
        if (lastName != null && !lastName.isEmpty()) {
            if (fullName.length() > 0) {
                fullName.append(" ");
            }
            fullName.append(lastName);
        }
        return fullName.toString();
    }
}
