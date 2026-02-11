package com.healthcare.notification.api.dto;

import com.healthcare.notification.domain.NotificationCategory;
import com.healthcare.notification.domain.NotificationType;

import java.util.Set;

public record UpdatePreferencesRequest(
    Boolean emailEnabled,
    Boolean smsEnabled,
    Boolean pushEnabled,
    Boolean inAppEnabled,
    Set<NotificationCategory> mutedCategories,
    Integer quietHoursStart,
    Integer quietHoursEnd,
    String timezone
) {}
