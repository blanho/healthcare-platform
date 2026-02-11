package com.healthcare.notification.api.dto;

import com.healthcare.notification.domain.NotificationCategory;
import com.healthcare.notification.domain.NotificationStatus;
import com.healthcare.notification.domain.NotificationType;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record NotificationResponse(
    UUID id,
    UUID userId,
    UUID patientId,
    NotificationType type,
    NotificationCategory category,
    String title,
    String message,
    NotificationStatus status,
    Instant scheduledAt,
    Instant sentAt,
    Instant deliveredAt,
    Instant readAt,
    Instant failedAt,
    String failureReason,
    int retryCount,
    Map<String, Object> metadata,
    Instant createdAt
) {}
