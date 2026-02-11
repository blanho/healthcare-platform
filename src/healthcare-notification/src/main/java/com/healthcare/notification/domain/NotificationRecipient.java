package com.healthcare.notification.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

@Embeddable
public record NotificationRecipient(
    @Column(name = "recipient_name")
    String name,

    @Column(name = "recipient_email")
    @Email
    String email,

    @Column(name = "recipient_phone")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    String phoneNumber,

    @Column(name = "device_token")
    String deviceToken
) {
    public static NotificationRecipient withEmail(String name, String email) {
        return new NotificationRecipient(name, email, null, null);
    }

    public static NotificationRecipient withPhone(String name, String phoneNumber) {
        return new NotificationRecipient(name, null, phoneNumber, null);
    }

    public static NotificationRecipient withDeviceToken(String name, String deviceToken) {
        return new NotificationRecipient(name, null, null, deviceToken);
    }

    public boolean hasEmail() {
        return email != null && !email.isBlank();
    }

    public boolean hasPhoneNumber() {
        return phoneNumber != null && !phoneNumber.isBlank();
    }

    public boolean hasDeviceToken() {
        return deviceToken != null && !deviceToken.isBlank();
    }

    public boolean canReceive(NotificationType type) {
        return switch (type) {
            case EMAIL -> hasEmail();
            case SMS -> hasPhoneNumber();
            case PUSH -> hasDeviceToken();
            case IN_APP -> true;
        };
    }
}
