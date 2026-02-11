package com.healthcare.notification.api;

import com.healthcare.notification.api.dto.*;
import com.healthcare.notification.domain.NotificationCategory;
import com.healthcare.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notifications", description = "Notification management API")
public class NotificationController {

    private static final Logger log = LoggerFactory.getLogger(NotificationController.class);

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('notification:write')")
    @Operation(summary = "Send a notification",
               description = "Send a notification to a user")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Notification sent"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<NotificationResponse> send(
            @Valid @RequestBody SendNotificationRequest request) {
        log.info("REST request to send notification: userId={}, type={}",
            request.userId(), request.type());
        NotificationResponse response = notificationService.send(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/template")
    @PreAuthorize("hasAuthority('notification:write')")
    @Operation(summary = "Send notification from template",
               description = "Send a notification using a predefined template")
    public ResponseEntity<NotificationResponse> sendFromTemplate(
            @Valid @RequestBody SendTemplateNotificationRequest request) {
        log.info("REST request to send template notification: userId={}, template={}",
            request.userId(), request.templateCode());
        NotificationResponse response = notificationService.sendFromTemplate(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/schedule")
    @PreAuthorize("hasAuthority('notification:write')")
    @Operation(summary = "Schedule a notification",
               description = "Schedule a notification for future delivery")
    public ResponseEntity<NotificationResponse> schedule(
            @Valid @RequestBody SendNotificationRequest request) {
        log.info("REST request to schedule notification: userId={}, scheduledAt={}",
            request.userId(), request.scheduledAt());
        NotificationResponse response = notificationService.schedule(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/bulk")
    @PreAuthorize("hasAuthority('notification:write')")
    @Operation(summary = "Send bulk notifications",
               description = "Send notifications to multiple users")
    public ResponseEntity<List<NotificationResponse>> sendBulk(
            @Valid @RequestBody List<SendNotificationRequest> requests) {
        log.info("REST request to send bulk notifications: count={}", requests.size());
        List<NotificationResponse> responses = notificationService.sendBulk(requests);
        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    @GetMapping("/{notificationId}")
    @PreAuthorize("hasAuthority('notification:read')")
    @Operation(summary = "Get notification by ID",
               description = "Retrieve a notification by its ID")
    public ResponseEntity<NotificationResponse> getById(
            @PathVariable UUID notificationId) {
        log.debug("REST request to get notification: {}", notificationId);
        NotificationResponse response = notificationService.getById(notificationId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('notification:read')")
    @Operation(summary = "Get user notifications",
               description = "Get notifications for a user with pagination")
    public ResponseEntity<Page<NotificationSummaryResponse>> getByUser(
            @PathVariable UUID userId,
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("REST request to get notifications for user: {}", userId);
        Page<NotificationSummaryResponse> page = notificationService.getByUser(userId, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/user/{userId}/unread")
    @PreAuthorize("hasAuthority('notification:read')")
    @Operation(summary = "Get unread notifications",
               description = "Get all unread notifications for a user")
    public ResponseEntity<List<NotificationSummaryResponse>> getUnread(
            @PathVariable UUID userId) {
        log.debug("REST request to get unread notifications for user: {}", userId);
        List<NotificationSummaryResponse> notifications = notificationService.getUnread(userId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/user/{userId}/unread/count")
    @PreAuthorize("hasAuthority('notification:read')")
    @Operation(summary = "Get unread count",
               description = "Get count of unread notifications for a user")
    public ResponseEntity<UnreadCountResponse> getUnreadCount(
            @PathVariable UUID userId) {
        log.debug("REST request to get unread count for user: {}", userId);
        UnreadCountResponse count = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/user/{userId}/category/{category}")
    @PreAuthorize("hasAuthority('notification:read')")
    @Operation(summary = "Get notifications by category",
               description = "Get notifications for a user filtered by category")
    public ResponseEntity<Page<NotificationSummaryResponse>> getByCategory(
            @PathVariable UUID userId,
            @PathVariable NotificationCategory category,
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("REST request to get notifications for user {} category {}", userId, category);
        Page<NotificationSummaryResponse> page = notificationService.getByCategory(userId, category, pageable);
        return ResponseEntity.ok(page);
    }

    @PatchMapping("/{notificationId}/read")
    @PreAuthorize("hasAuthority('notification:read')")
    @Operation(summary = "Mark as read",
               description = "Mark a notification as read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable UUID notificationId) {
        log.info("REST request to mark notification as read: {}", notificationId);
        notificationService.markAsRead(notificationId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/user/{userId}/read-all")
    @PreAuthorize("hasAuthority('notification:read')")
    @Operation(summary = "Mark all as read",
               description = "Mark all notifications as read for a user")
    public ResponseEntity<Void> markAllAsRead(
            @PathVariable UUID userId) {
        log.info("REST request to mark all notifications as read for user: {}", userId);
        notificationService.markAllAsRead(userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{notificationId}")
    @PreAuthorize("hasAuthority('notification:write')")
    @Operation(summary = "Cancel notification",
               description = "Cancel a pending or scheduled notification")
    public ResponseEntity<Void> cancel(
            @PathVariable UUID notificationId) {
        log.info("REST request to cancel notification: {}", notificationId);
        notificationService.cancel(notificationId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{notificationId}/retry")
    @PreAuthorize("hasAuthority('notification:write')")
    @Operation(summary = "Retry notification",
               description = "Retry a failed notification")
    public ResponseEntity<Void> retry(
            @PathVariable UUID notificationId) {
        log.info("REST request to retry notification: {}", notificationId);
        notificationService.retry(notificationId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/preferences/{userId}")
    @PreAuthorize("hasAuthority('notification:read')")
    @Operation(summary = "Get preferences",
               description = "Get notification preferences for a user")
    public ResponseEntity<NotificationPreferenceResponse> getPreferences(
            @PathVariable UUID userId) {
        log.debug("REST request to get preferences for user: {}", userId);
        NotificationPreferenceResponse preferences = notificationService.getPreferences(userId);
        return ResponseEntity.ok(preferences);
    }

    @PutMapping("/preferences/{userId}")
    @PreAuthorize("hasAuthority('notification:write')")
    @Operation(summary = "Update preferences",
               description = "Update notification preferences for a user")
    public ResponseEntity<NotificationPreferenceResponse> updatePreferences(
            @PathVariable UUID userId,
            @Valid @RequestBody UpdatePreferencesRequest request) {
        log.info("REST request to update preferences for user: {}", userId);
        NotificationPreferenceResponse preferences = notificationService.updatePreferences(userId, request);
        return ResponseEntity.ok(preferences);
    }
}
