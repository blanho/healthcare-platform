package com.healthcare.notification.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "notification_preferences", indexes = {
    @Index(name = "idx_pref_user", columnList = "user_id", unique = true)
})
public class NotificationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(name = "email_enabled", nullable = false)
    private boolean emailEnabled;

    @Column(name = "sms_enabled", nullable = false)
    private boolean smsEnabled;

    @Column(name = "push_enabled", nullable = false)
    private boolean pushEnabled;

    @Column(name = "in_app_enabled", nullable = false)
    private boolean inAppEnabled;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
        name = "notification_preference_categories",
        joinColumns = @JoinColumn(name = "preference_id")
    )
    @Column(name = "category")
    @Enumerated(EnumType.STRING)
    @org.hibernate.annotations.BatchSize(size = 25)
    private Set<NotificationCategory> enabledCategories;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
        name = "notification_preference_muted",
        joinColumns = @JoinColumn(name = "preference_id")
    )
    @Column(name = "category")
    @Enumerated(EnumType.STRING)
    @org.hibernate.annotations.BatchSize(size = 25)
    private Set<NotificationCategory> mutedCategories;

    @Column(name = "quiet_hours_start")
    private Integer quietHoursStart;

    @Column(name = "quiet_hours_end")
    private Integer quietHoursEnd;

    @Column(name = "timezone", length = 50)
    private String timezone;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    protected NotificationPreference() {

    }

    public static NotificationPreference createDefault(UUID userId) {
        NotificationPreference pref = new NotificationPreference();
        pref.userId = userId;
        pref.emailEnabled = true;
        pref.smsEnabled = false;
        pref.pushEnabled = true;
        pref.inAppEnabled = true;
        pref.enabledCategories = EnumSet.allOf(NotificationCategory.class);
        pref.mutedCategories = EnumSet.noneOf(NotificationCategory.class);
        pref.timezone = "UTC";
        pref.createdAt = Instant.now();
        return pref;
    }

    public boolean isChannelEnabled(NotificationType type) {
        return switch (type) {
            case EMAIL -> emailEnabled;
            case SMS -> smsEnabled;
            case PUSH -> pushEnabled;
            case IN_APP -> inAppEnabled;
        };
    }

    public boolean isCategoryEnabled(NotificationCategory category) {
        if (mutedCategories.contains(category)) {
            return false;
        }

        if (category.isUrgent()) {
            return true;
        }
        return enabledCategories.contains(category);
    }

    public boolean isWithinQuietHours(int currentHour) {
        if (quietHoursStart == null || quietHoursEnd == null) {
            return false;
        }
        if (quietHoursStart <= quietHoursEnd) {
            return currentHour >= quietHoursStart && currentHour < quietHoursEnd;
        } else {

            return currentHour >= quietHoursStart || currentHour < quietHoursEnd;
        }
    }

    public boolean shouldSend(NotificationType type, NotificationCategory category) {

        if (category.isUrgent()) {
            return true;
        }
        return isChannelEnabled(type) && isCategoryEnabled(category);
    }

    public void enableChannel(NotificationType type) {
        switch (type) {
            case EMAIL -> this.emailEnabled = true;
            case SMS -> this.smsEnabled = true;
            case PUSH -> this.pushEnabled = true;
            case IN_APP -> this.inAppEnabled = true;
        }
        this.updatedAt = Instant.now();
    }

    public void disableChannel(NotificationType type) {
        switch (type) {
            case EMAIL -> this.emailEnabled = false;
            case SMS -> this.smsEnabled = false;
            case PUSH -> this.pushEnabled = false;
            case IN_APP -> this.inAppEnabled = false;
        }
        this.updatedAt = Instant.now();
    }

    public void muteCategory(NotificationCategory category) {
        if (!category.isUrgent()) {
            this.mutedCategories.add(category);
            this.updatedAt = Instant.now();
        }
    }

    public void unmuteCategory(NotificationCategory category) {
        this.mutedCategories.remove(category);
        this.updatedAt = Instant.now();
    }

    public void setQuietHours(Integer start, Integer end) {
        this.quietHoursStart = start;
        this.quietHoursEnd = end;
        this.updatedAt = Instant.now();
    }

    public void clearQuietHours() {
        this.quietHoursStart = null;
        this.quietHoursEnd = null;
        this.updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public boolean isEmailEnabled() { return emailEnabled; }
    public boolean isSmsEnabled() { return smsEnabled; }
    public boolean isPushEnabled() { return pushEnabled; }
    public boolean isInAppEnabled() { return inAppEnabled; }
    public Set<NotificationCategory> getEnabledCategories() { return enabledCategories; }
    public Set<NotificationCategory> getMutedCategories() { return mutedCategories; }
    public Integer getQuietHoursStart() { return quietHoursStart; }
    public Integer getQuietHoursEnd() { return quietHoursEnd; }
    public String getTimezone() { return timezone; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
