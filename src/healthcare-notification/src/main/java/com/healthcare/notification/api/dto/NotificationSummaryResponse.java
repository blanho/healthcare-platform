package com.healthcare.notification.api.dto;

import com.healthcare.notification.domain.NotificationCategory;
import com.healthcare.notification.domain.NotificationStatus;
import com.healthcare.notification.domain.NotificationType;

import java.time.Instant;
import java.util.UUID;

public record NotificationSummaryResponse(
    UUID id,
    NotificationType type,
    NotificationCategory category,
    String title,
    NotificationStatus status,
    boolean isRead,
    Instant createdAt
) {}
