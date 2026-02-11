package com.healthcare.notification.api.dto;

import com.healthcare.notification.domain.NotificationCategory;
import com.healthcare.notification.domain.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;
import java.util.UUID;

public record SendTemplateNotificationRequest(
    @NotNull(message = "User ID is required")
    UUID userId,

    UUID patientId,

    @NotBlank(message = "Template code is required")
    String templateCode,

    NotificationType typeOverride,

    Map<String, String> templateVariables,

    Map<String, Object> metadata
) {}
