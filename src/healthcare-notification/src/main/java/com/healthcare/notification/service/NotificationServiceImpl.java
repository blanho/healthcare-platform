package com.healthcare.notification.service;

import com.healthcare.notification.api.dto.*;
import com.healthcare.notification.domain.*;
import com.healthcare.notification.domain.event.NotificationFailedEvent;
import com.healthcare.notification.domain.event.NotificationReadEvent;
import com.healthcare.notification.domain.event.NotificationSentEvent;
import com.healthcare.notification.exception.NotificationDeliveryException;
import com.healthcare.notification.exception.NotificationNotFoundException;
import com.healthcare.notification.repository.NotificationPreferenceRepository;
import com.healthcare.notification.repository.NotificationRepository;
import com.healthcare.notification.service.channel.NotificationChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final NotificationRepository notificationRepository;
    private final NotificationPreferenceRepository preferenceRepository;
    private final TemplateService templateService;
    private final ApplicationEventPublisher eventPublisher;
    private final Map<NotificationType, NotificationChannel> channels;

    public NotificationServiceImpl(
            NotificationRepository notificationRepository,
            NotificationPreferenceRepository preferenceRepository,
            TemplateService templateService,
            ApplicationEventPublisher eventPublisher,
            List<NotificationChannel> channelList) {
        this.notificationRepository = notificationRepository;
        this.preferenceRepository = preferenceRepository;
        this.templateService = templateService;
        this.eventPublisher = eventPublisher;
        this.channels = channelList.stream()
            .collect(Collectors.toMap(NotificationChannel::getType, Function.identity()));
    }

    @Override
    public NotificationResponse send(SendNotificationRequest request) {
        log.info("Sending notification: userId={}, type={}, category={}",
            request.userId(), request.type(), request.category());

        NotificationPreference preferences = getOrCreatePreferences(request.userId());
        if (!preferences.shouldSend(request.type(), request.category())) {
            log.info("Notification skipped due to user preferences: userId={}", request.userId());
            return null;
        }

        Notification notification = Notification.builder()
            .userId(request.userId())
            .patientId(request.patientId())
            .type(request.type())
            .category(request.category())
            .title(request.title())
            .message(request.message())
            .scheduledAt(request.scheduledAt())
            .metadata(request.metadata())
            .build();

        notification = notificationRepository.save(notification);

        if (notification.isReadyToSend()) {
            deliverAsync(notification);
        }

        return toResponse(notification);
    }

    @Override
    public NotificationResponse sendFromTemplate(SendTemplateNotificationRequest request) {
        log.info("Sending notification from template: userId={}, template={}",
            request.userId(), request.templateCode());

        NotificationTemplate template = templateService.getTemplate(request.templateCode());

        String title = templateService.renderSubject(template, request.templateVariables());
        String message = templateService.renderBody(template, request.templateVariables());

        NotificationType type = request.typeOverride() != null ?
            request.typeOverride() : template.getType();

        SendNotificationRequest notificationRequest = new SendNotificationRequest(
            request.userId(),
            request.patientId(),
            type,
            template.getCategory(),
            title,
            message,
            null,
            request.metadata()
        );

        return send(notificationRequest);
    }

    @Override
    public NotificationResponse schedule(SendNotificationRequest request) {
        if (request.scheduledAt() == null || request.scheduledAt().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Scheduled time must be in the future");
        }
        return send(request);
    }

    @Override
    public List<NotificationResponse> sendBulk(List<SendNotificationRequest> requests) {
        log.info("Sending bulk notifications: count={}", requests.size());
        return requests.stream()
            .map(this::send)
            .filter(response -> response != null)
            .toList();
    }

    @Async
    protected void deliverAsync(Notification notification) {
        try {
            deliver(notification);
        } catch (Exception e) {
            log.error("Async delivery failed: notificationId={}", notification.getId(), e);
        }
    }

    private void deliver(Notification notification) {
        NotificationChannel channel = channels.get(notification.getType());
        if (channel == null) {
            log.error("No channel found for type: {}", notification.getType());
            notification.markFailed("No delivery channel available");
            notificationRepository.save(notification);
            return;
        }

        try {
            notification.markSending();
            notificationRepository.save(notification);

            channel.send(notification);

            notification.markSent();
            notificationRepository.save(notification);

            eventPublisher.publishEvent(new NotificationSentEvent(
                notification.getId(),
                notification.getUserId(),
                notification.getType(),
                notification.getCategory()
            ));

            log.info("Notification delivered: id={}, type={}",
                notification.getId(), notification.getType());

        } catch (NotificationDeliveryException e) {
            log.error("Notification delivery failed: id={}", notification.getId(), e);
            notification.markFailed(e.getMessage());
            notificationRepository.save(notification);

            eventPublisher.publishEvent(new NotificationFailedEvent(
                notification.getId(),
                notification.getUserId(),
                notification.getType(),
                notification.getCategory(),
                e.getMessage(),
                notification.getRetryCount()
            ));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationResponse getById(UUID notificationId) {
        return notificationRepository.findById(notificationId)
            .map(this::toResponse)
            .orElseThrow(() -> NotificationNotFoundException.byId(notificationId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationSummaryResponse> getByUser(UUID userId, Pageable pageable) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
            .map(this::toSummary);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationSummaryResponse> getUnread(UUID userId) {
        return notificationRepository.findUnreadByUserId(userId).stream()
            .map(this::toSummary)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UnreadCountResponse getUnreadCount(UUID userId) {
        long count = notificationRepository.countUnreadByUserId(userId);
        return new UnreadCountResponse(count);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationSummaryResponse> getByCategory(
            UUID userId, NotificationCategory category, Pageable pageable) {
        return notificationRepository.findByUserIdAndCategoryOrderByCreatedAtDesc(userId, category, pageable)
            .map(this::toSummary);
    }

    @Override
    public void markAsRead(UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> NotificationNotFoundException.byId(notificationId));

        notification.markRead();
        notificationRepository.save(notification);

        eventPublisher.publishEvent(new NotificationReadEvent(
            notificationId, notification.getUserId()));
    }

    @Override
    public void markAllAsRead(UUID userId) {
        int updated = notificationRepository.markAllAsRead(userId, Instant.now());
        log.info("Marked {} notifications as read for user: {}", updated, userId);
    }

    @Override
    public void cancel(UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> NotificationNotFoundException.byId(notificationId));

        notification.cancel();
        notificationRepository.save(notification);

        log.info("Notification cancelled: id={}", notificationId);
    }

    @Override
    public void retry(UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> NotificationNotFoundException.byId(notificationId));

        notification.retry();
        notificationRepository.save(notification);

        deliverAsync(notification);

        log.info("Notification retry initiated: id={}, retryCount={}",
            notificationId, notification.getRetryCount());
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationPreferenceResponse getPreferences(UUID userId) {
        NotificationPreference preferences = getOrCreatePreferences(userId);
        return toPreferenceResponse(preferences);
    }

    @Override
    public NotificationPreferenceResponse updatePreferences(UUID userId, UpdatePreferencesRequest request) {
        NotificationPreference preferences = getOrCreatePreferences(userId);

        if (request.emailEnabled() != null) {
            if (request.emailEnabled()) {
                preferences.enableChannel(NotificationType.EMAIL);
            } else {
                preferences.disableChannel(NotificationType.EMAIL);
            }
        }
        if (request.smsEnabled() != null) {
            if (request.smsEnabled()) {
                preferences.enableChannel(NotificationType.SMS);
            } else {
                preferences.disableChannel(NotificationType.SMS);
            }
        }
        if (request.pushEnabled() != null) {
            if (request.pushEnabled()) {
                preferences.enableChannel(NotificationType.PUSH);
            } else {
                preferences.disableChannel(NotificationType.PUSH);
            }
        }
        if (request.inAppEnabled() != null) {
            if (request.inAppEnabled()) {
                preferences.enableChannel(NotificationType.IN_APP);
            } else {
                preferences.disableChannel(NotificationType.IN_APP);
            }
        }
        if (request.mutedCategories() != null) {
            for (NotificationCategory category : request.mutedCategories()) {
                preferences.muteCategory(category);
            }
        }
        if (request.quietHoursStart() != null && request.quietHoursEnd() != null) {
            preferences.setQuietHours(request.quietHoursStart(), request.quietHoursEnd());
        }

        preferences = preferenceRepository.save(preferences);
        return toPreferenceResponse(preferences);
    }

    private NotificationPreference getOrCreatePreferences(UUID userId) {
        return preferenceRepository.findByUserId(userId)
            .orElseGet(() -> {
                NotificationPreference newPref = NotificationPreference.createDefault(userId);
                return preferenceRepository.save(newPref);
            });
    }

    @Override
    public void processPendingNotifications() {
        List<Notification> pending = notificationRepository.findReadyToSend(Instant.now());
        log.info("Processing {} pending notifications", pending.size());

        for (Notification notification : pending) {
            try {
                deliver(notification);
            } catch (Exception e) {
                log.error("Failed to process notification: id={}", notification.getId(), e);
            }
        }
    }

    @Override
    public void retryFailedNotifications(int maxRetries) {
        List<Notification> failed = notificationRepository.findRetryable(maxRetries);
        log.info("Retrying {} failed notifications", failed.size());

        for (Notification notification : failed) {
            try {
                notification.retry();
                notificationRepository.save(notification);
                deliver(notification);
            } catch (Exception e) {
                log.error("Failed to retry notification: id={}", notification.getId(), e);
            }
        }
    }

    private NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(
            notification.getId(),
            notification.getUserId(),
            notification.getPatientId(),
            notification.getType(),
            notification.getCategory(),
            notification.getTitle(),
            notification.getMessage(),
            notification.getStatus(),
            notification.getScheduledAt(),
            notification.getSentAt(),
            notification.getDeliveredAt(),
            notification.getReadAt(),
            notification.getFailedAt(),
            notification.getFailureReason(),
            notification.getRetryCount(),
            notification.getMetadata(),
            notification.getCreatedAt()
        );
    }

    private NotificationSummaryResponse toSummary(Notification notification) {
        return new NotificationSummaryResponse(
            notification.getId(),
            notification.getType(),
            notification.getCategory(),
            notification.getTitle(),
            notification.getStatus(),
            notification.getStatus() == NotificationStatus.READ,
            notification.getCreatedAt()
        );
    }

    private NotificationPreferenceResponse toPreferenceResponse(NotificationPreference pref) {
        return new NotificationPreferenceResponse(
            pref.getId(),
            pref.getUserId(),
            pref.isEmailEnabled(),
            pref.isSmsEnabled(),
            pref.isPushEnabled(),
            pref.isInAppEnabled(),
            pref.getMutedCategories(),
            pref.getQuietHoursStart(),
            pref.getQuietHoursEnd(),
            pref.getTimezone()
        );
    }
}
