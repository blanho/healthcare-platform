package com.healthcare.notification.service;

import com.healthcare.notification.domain.Notification;
import com.healthcare.notification.domain.NotificationCategory;
import com.healthcare.notification.domain.NotificationType;
import com.healthcare.notification.api.dto.SendNotificationRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;

@Service
public class HealthcareNotificationFacade {

    private final NotificationService notificationService;
    private final TemplateService templateService;

    public HealthcareNotificationFacade(
            NotificationService notificationService,
            TemplateService templateService) {
        this.notificationService = notificationService;
        this.templateService = templateService;
    }

    public void sendAppointmentConfirmation(
            UUID userId,
            UUID patientId,
            String appointmentNumber,
            String providerName,
            Instant appointmentTime,
            String location) {

        String title = "Appointment Confirmed";
        String message = String.format(
            "Your appointment #%s with %s on %s at %s has been confirmed.",
            appointmentNumber, providerName,
            formatDate(appointmentTime), location
        );

        send(userId, patientId, NotificationType.EMAIL,
            NotificationCategory.APPOINTMENT_CONFIRMATION, title, message,
            Map.of(
                "appointmentNumber", appointmentNumber,
                "providerName", providerName,
                "appointmentTime", appointmentTime.toString(),
                "location", location
            ));
    }

    public void scheduleAppointmentReminder(
            UUID userId,
            UUID patientId,
            String appointmentNumber,
            String providerName,
            Instant appointmentTime,
            int hoursBeforeAppointment) {

        String title = "Appointment Reminder";
        String message = String.format(
            "Reminder: You have an appointment #%s with %s on %s.",
            appointmentNumber, providerName, formatDate(appointmentTime)
        );

        Instant scheduledTime = appointmentTime.minus(hoursBeforeAppointment, ChronoUnit.HOURS);

        if (scheduledTime.isBefore(Instant.now())) {
            return;
        }

        SendNotificationRequest request = new SendNotificationRequest(
            userId, patientId, NotificationType.SMS,
            NotificationCategory.APPOINTMENT_REMINDER,
            title, message, scheduledTime,
            Map.of("appointmentNumber", appointmentNumber)
        );

        notificationService.schedule(request);
    }

    public void sendAppointmentCancellation(
            UUID userId,
            UUID patientId,
            String appointmentNumber,
            String reason) {

        String title = "Appointment Cancelled";
        String message = String.format(
            "Your appointment #%s has been cancelled. Reason: %s. " +
            "Please contact us to reschedule.",
            appointmentNumber, reason
        );

        send(userId, patientId, NotificationType.EMAIL,
            NotificationCategory.APPOINTMENT_CANCELLATION, title, message,
            Map.of("appointmentNumber", appointmentNumber, "reason", reason));
    }

    public void sendAppointmentRescheduled(
            UUID userId,
            UUID patientId,
            String appointmentNumber,
            String providerName,
            Instant oldTime,
            Instant newTime) {

        String title = "Appointment Rescheduled";
        String message = String.format(
            "Your appointment #%s with %s has been rescheduled from %s to %s.",
            appointmentNumber,
            providerName,
            DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' hh:mm a")
                .withZone(ZoneId.systemDefault())
                .format(oldTime),
            DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' hh:mm a")
                .withZone(ZoneId.systemDefault())
                .format(newTime)
        );

        send(userId, patientId, NotificationType.EMAIL,
            NotificationCategory.APPOINTMENT_RESCHEDULED, title, message,
            Map.of("appointmentNumber", appointmentNumber,
                   "providerName", providerName,
                   "oldTime", oldTime.toString(),
                   "newTime", newTime.toString()));
    }

    public void sendLabResultsReady(
            UUID userId,
            UUID patientId,
            String testName) {

        String title = "Lab Results Available";
        String message = String.format(
            "Your %s results are now available. " +
            "Please log in to view your results or contact your provider.",
            testName
        );

        send(userId, patientId, NotificationType.EMAIL,
            NotificationCategory.LAB_RESULT_READY, title, message,
            Map.of("testName", testName));
    }

    public void sendPrescriptionReady(
            UUID userId,
            UUID patientId,
            String pharmacyName,
            String pharmacyAddress) {

        String title = "Prescription Ready";
        String message = String.format(
            "Your prescription is ready for pickup at %s (%s).",
            pharmacyName, pharmacyAddress
        );

        send(userId, patientId, NotificationType.SMS,
            NotificationCategory.PRESCRIPTION_READY, title, message,
            Map.of("pharmacyName", pharmacyName));
    }

    public void sendCriticalVitalsAlert(
            UUID userId,
            UUID patientId,
            String vitalType,
            String value,
            String normalRange) {

        String title = "⚠️ Critical Vitals Alert";
        String message = String.format(
            "Critical %s detected: %s (Normal range: %s). " +
            "Please seek immediate medical attention.",
            vitalType, value, normalRange
        );

        send(userId, patientId, NotificationType.PUSH,
            NotificationCategory.CRITICAL_VITALS, title, message,
            Map.of("vitalType", vitalType, "value", value));

        send(userId, patientId, NotificationType.SMS,
            NotificationCategory.CRITICAL_VITALS, title, message,
            Map.of("vitalType", vitalType, "value", value));
    }

    public void sendInvoiceGenerated(
            UUID userId,
            UUID patientId,
            String invoiceNumber,
            String amount,
            Instant dueDate) {

        String title = "New Invoice";
        String message = String.format(
            "Invoice #%s for %s has been generated. Due date: %s. " +
            "Please log in to view and pay.",
            invoiceNumber, amount, formatDate(dueDate)
        );

        send(userId, patientId, NotificationType.EMAIL,
            NotificationCategory.INVOICE_GENERATED, title, message,
            Map.of("invoiceNumber", invoiceNumber, "amount", amount));
    }

    public void sendPaymentReceived(
            UUID userId,
            UUID patientId,
            String invoiceNumber,
            String amount) {

        String title = "Payment Received";
        String message = String.format(
            "Thank you! Your payment of %s for invoice #%s has been received.",
            amount, invoiceNumber
        );

        send(userId, patientId, NotificationType.EMAIL,
            NotificationCategory.PAYMENT_RECEIVED, title, message,
            Map.of("invoiceNumber", invoiceNumber, "amount", amount));
    }

    public void sendPaymentReminder(
            UUID userId,
            UUID patientId,
            String invoiceNumber,
            String amount,
            Instant dueDate) {

        String title = "Payment Reminder";
        String message = String.format(
            "Reminder: Invoice #%s for %s is due on %s. " +
            "Please log in to make a payment.",
            invoiceNumber, amount, formatDate(dueDate)
        );

        send(userId, patientId, NotificationType.EMAIL,
            NotificationCategory.PAYMENT_DUE, title, message,
            Map.of("invoiceNumber", invoiceNumber, "amount", amount));
    }

    public void sendPaymentReminder(
            UUID userId,
            UUID patientId,
            String invoiceNumber,
            String amount,
            String dueDate) {

        String title = "Payment Overdue";
        String message = String.format(
            "Your invoice #%s for %s was due on %s and is now overdue. " +
            "Please submit payment to avoid late fees.",
            invoiceNumber, amount, dueDate
        );

        send(userId, patientId, NotificationType.EMAIL,
            NotificationCategory.PAYMENT_DUE, title, message,
            Map.of("invoiceNumber", invoiceNumber, "amount", amount, "overdue", "true"));
    }

    public void sendClaimUpdate(
            UUID userId,
            UUID patientId,
            String claimNumber,
            String status,
            String action) {

        String title = "Insurance Claim Update";
        String message = String.format(
            "Your insurance claim %s has been updated. Status: %s. Action: %s.",
            claimNumber, status, action
        );

        send(userId, patientId, NotificationType.EMAIL,
            NotificationCategory.BILLING, title, message,
            Map.of("claimNumber", claimNumber, "status", status, "action", action));
    }

    public void sendAppointmentNoShow(
            UUID userId,
            UUID patientId,
            String appointmentNumber) {

        String title = "Missed Appointment";
        String message = String.format(
            "You missed your scheduled appointment (%s). " +
            "Please contact us to reschedule. Repeated no-shows may result in fees.",
            appointmentNumber
        );

        send(userId, patientId, NotificationType.EMAIL,
            NotificationCategory.APPOINTMENT, title, message,
            Map.of("appointmentNumber", appointmentNumber));
    }

    public void sendWelcome(UUID userId, String name) {
        String title = "Welcome to Healthcare Platform";
        String message = String.format(
            "Welcome, %s! Your account has been created. " +
            "Please complete your profile to get started.",
            name
        );

        send(userId, null, NotificationType.EMAIL,
            NotificationCategory.WELCOME, title, message,
            Map.of("name", name));
    }

    public void sendPasswordReset(UUID userId, String resetLink) {
        String title = "Password Reset Request";
        String message = String.format(
            "A password reset was requested for your account. " +
            "Click here to reset: %s. This link expires in 1 hour.",
            resetLink
        );

        send(userId, null, NotificationType.EMAIL,
            NotificationCategory.PASSWORD_RESET, title, message,
            Map.of());
    }

    public void sendSecurityAlert(UUID userId, String alertMessage) {
        String title = "Security Alert";

        send(userId, null, NotificationType.EMAIL,
            NotificationCategory.SECURITY_ALERT, title, alertMessage,
            Map.of());

        send(userId, null, NotificationType.PUSH,
            NotificationCategory.SECURITY_ALERT, title, alertMessage,
            Map.of());
    }

    private void send(
            UUID userId,
            UUID patientId,
            NotificationType type,
            NotificationCategory category,
            String title,
            String message,
            Map<String, Object> metadata) {

        SendNotificationRequest request = new SendNotificationRequest(
            userId, patientId, type, category, title, message, null, metadata
        );

        notificationService.send(request);
    }

    private String formatDate(Instant instant) {

        return instant.toString().substring(0, 16).replace("T", " ");
    }
}
