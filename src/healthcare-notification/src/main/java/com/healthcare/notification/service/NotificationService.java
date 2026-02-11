package com.healthcare.notification.service;

import com.healthcare.notification.api.dto.*;
import com.healthcare.notification.domain.Notification;
import com.healthcare.notification.domain.NotificationCategory;
import com.healthcare.notification.domain.NotificationStatus;
import com.healthcare.notification.domain.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface NotificationService {

    NotificationResponse send(SendNotificationRequest request);

    NotificationResponse sendFromTemplate(SendTemplateNotificationRequest request);

    NotificationResponse schedule(SendNotificationRequest request);

    List<NotificationResponse> sendBulk(List<SendNotificationRequest> requests);

    NotificationResponse getById(UUID notificationId);

    Page<NotificationSummaryResponse> getByUser(UUID userId, Pageable pageable);

    List<NotificationSummaryResponse> getUnread(UUID userId);

    UnreadCountResponse getUnreadCount(UUID userId);

    Page<NotificationSummaryResponse> getByCategory(UUID userId, NotificationCategory category, Pageable pageable);

    void markAsRead(UUID notificationId);

    void markAllAsRead(UUID userId);

    void cancel(UUID notificationId);

    void retry(UUID notificationId);

    NotificationPreferenceResponse getPreferences(UUID userId);

    NotificationPreferenceResponse updatePreferences(UUID userId, UpdatePreferencesRequest request);

    void processPendingNotifications();

    void retryFailedNotifications(int maxRetries);
}
