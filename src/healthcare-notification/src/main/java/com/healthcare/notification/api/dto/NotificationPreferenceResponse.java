package com.healthcare.notification.api.dto;

import com.healthcare.notification.domain.NotificationCategory;

import java.util.Set;
import java.util.UUID;

public record NotificationPreferenceResponse(
    UUID id,
    UUID userId,
    boolean emailEnabled,
    boolean smsEnabled,
    boolean pushEnabled,
    boolean inAppEnabled,
    Set<NotificationCategory> mutedCategories,
    Integer quietHoursStart,
    Integer quietHoursEnd,
    String timezone
) {}
