package com.healthcare.notification.api.dto;

import com.healthcare.notification.domain.NotificationCategory;
import com.healthcare.notification.domain.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record SendNotificationRequest(
    @NotNull(message = "User ID is required")
    UUID userId,

    UUID patientId,

    @NotNull(message = "Notification type is required")
    NotificationType type,

    @NotNull(message = "Notification category is required")
    NotificationCategory category,

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    String title,

    @NotBlank(message = "Message is required")
    @Size(max = 10000, message = "Message must not exceed 10000 characters")
    String message,

    Instant scheduledAt,

    Map<String, Object> metadata
) {}
