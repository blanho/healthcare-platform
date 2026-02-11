package com.healthcare.notification.service.listener;

import com.healthcare.notification.service.HealthcareNotificationFacade;
import com.healthcare.notification.service.PatientContactLookup;
import com.healthcare.notification.service.PatientContactLookup.PatientContact;
import com.healthcare.appointment.api.AppointmentLookup;
import com.healthcare.appointment.api.AppointmentLookup.AppointmentInfo;
import com.healthcare.provider.api.ProviderLookup;
import com.healthcare.provider.api.ProviderLookup.ProviderInfo;
import com.healthcare.billing.api.ClaimLookup;
import com.healthcare.billing.api.ClaimLookup.ClaimInfo;
import com.healthcare.location.api.LocationLookup;
import com.healthcare.location.api.LocationLookup.LocationInfo;
import com.healthcare.appointment.domain.event.AppointmentScheduledEvent;
import com.healthcare.appointment.domain.event.AppointmentCancelledEvent;
import com.healthcare.appointment.domain.event.AppointmentRescheduledEvent;
import com.healthcare.appointment.domain.event.AppointmentNoShowEvent;
import com.healthcare.billing.domain.event.InvoiceFinalizedEvent;
import com.healthcare.billing.domain.event.InvoiceOverdueEvent;
import com.healthcare.billing.domain.event.PaymentReceivedEvent;
import com.healthcare.billing.domain.event.ClaimStatusChangedEvent;
import com.healthcare.medicalrecord.domain.event.CriticalVitalsDetectedEvent;
import com.healthcare.patient.domain.event.PatientActivatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Optional;

@Component
public class DomainEventNotificationListener {

    private static final Logger log = LoggerFactory.getLogger(DomainEventNotificationListener.class);

    private final HealthcareNotificationFacade notificationFacade;
    private final PatientContactLookup patientContactLookup;
    private final AppointmentLookup appointmentLookup;
    private final ProviderLookup providerLookup;
    private final ClaimLookup claimLookup;
    private final LocationLookup locationLookup;

    public DomainEventNotificationListener(
            HealthcareNotificationFacade notificationFacade,
            PatientContactLookup patientContactLookup,
            AppointmentLookup appointmentLookup,
            ProviderLookup providerLookup,
            ClaimLookup claimLookup,
            LocationLookup locationLookup) {
        this.notificationFacade = notificationFacade;
        this.patientContactLookup = patientContactLookup;
        this.appointmentLookup = appointmentLookup;
        this.providerLookup = providerLookup;
        this.claimLookup = claimLookup;
        this.locationLookup = locationLookup;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onAppointmentScheduled(AppointmentScheduledEvent event) {
        log.info("Handling AppointmentScheduledEvent: appointmentId={}", event.aggregateId());

        try {
            Optional<PatientContact> contactOpt = patientContactLookup.findByPatientId(event.patientId());

            if (contactOpt.isEmpty()) {
                log.warn("Cannot send appointment confirmation - patient not found: {}", event.patientId());
                return;
            }

            PatientContact contact = contactOpt.get();

            String providerName = providerLookup.findById(event.providerId())
                .map(ProviderInfo::fullName)
                .orElse("Provider");

            String locationName = appointmentLookup.findById(event.aggregateId())
                .flatMap(appt -> locationLookup.findById(appt.locationId()))
                .map(LocationInfo::name)
                .orElse("Healthcare Clinic");

            notificationFacade.sendAppointmentConfirmation(
                contact.userId(),
                event.patientId(),
                event.appointmentNumber(),
                providerName,
                event.timeSlot().getStartDateTime().atZone(java.time.ZoneId.systemDefault()).toInstant(),
                locationName
            );

            log.info("Sent appointment confirmation for appointment: {}", event.appointmentNumber());
        } catch (Exception e) {
            log.error("Failed to send appointment confirmation notification for appointment: {}",
                event.appointmentNumber(), e);
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onAppointmentCancelled(AppointmentCancelledEvent event) {
        log.info("Handling AppointmentCancelledEvent: appointmentId={}", event.aggregateId());

        try {
            Optional<PatientContact> contactOpt = patientContactLookup.findByPatientId(event.patientId());

            if (contactOpt.isEmpty()) {
                log.warn("Cannot send cancellation notification - patient not found: {}", event.patientId());
                return;
            }

            PatientContact contact = contactOpt.get();
            notificationFacade.sendAppointmentCancellation(
                contact.userId(),
                event.patientId(),
                event.appointmentNumber(),
                event.reason()
            );

            log.info("Sent appointment cancellation for appointment: {}", event.appointmentNumber());
        } catch (Exception e) {
            log.error("Failed to send appointment cancellation notification for appointment: {}",
                event.appointmentNumber(), e);
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPatientActivated(PatientActivatedEvent event) {
        log.info("Handling PatientActivatedEvent: patientId={}", event.aggregateId());

        try {
            Optional<PatientContact> contactOpt = patientContactLookup.findByPatientId(event.patientId());

            if (contactOpt.isEmpty()) {
                log.warn("Cannot send welcome notification - patient not found: {}", event.patientId());
                return;
            }

            PatientContact contact = contactOpt.get();
            notificationFacade.sendWelcome(contact.userId(), contact.name());

            log.info("Sent welcome notification for patient: {}", event.medicalRecordNumber());
        } catch (Exception e) {
            log.error("Failed to send patient welcome notification for patient: {}",
                event.patientId(), e);
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onInvoiceFinalized(InvoiceFinalizedEvent event) {
        log.info("Handling InvoiceFinalizedEvent: invoiceId={}", event.getInvoiceId());

        try {
            Optional<PatientContact> contactOpt = patientContactLookup.findByPatientId(event.getPatientId());

            if (contactOpt.isEmpty()) {
                log.warn("Cannot send invoice notification - patient not found: {}", event.getPatientId());
                return;
            }

            PatientContact contact = contactOpt.get();
            notificationFacade.sendInvoiceGenerated(
                contact.userId(),
                event.getPatientId(),
                event.getInvoiceNumber(),
                event.getTotalAmount().toString(),
                java.time.Instant.now().plus(30, java.time.temporal.ChronoUnit.DAYS)
            );

            log.info("Sent invoice notification for invoice: {}", event.getInvoiceNumber());
        } catch (Exception e) {
            log.error("Failed to send invoice notification for invoice: {}",
                event.getInvoiceNumber(), e);
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPaymentReceived(PaymentReceivedEvent event) {
        log.info("Handling PaymentReceivedEvent: paymentId={}", event.getPaymentId());

        try {
            Optional<PatientContact> contactOpt = patientContactLookup.findByPatientId(event.getPatientId());

            if (contactOpt.isEmpty()) {
                log.warn("Cannot send payment confirmation - patient not found: {}", event.getPatientId());
                return;
            }

            PatientContact contact = contactOpt.get();
            notificationFacade.sendPaymentReceived(
                contact.userId(),
                event.getPatientId(),
                event.getReferenceNumber(),
                event.getAmount().toString()
            );

            log.info("Sent payment confirmation for payment: {}", event.getReferenceNumber());
        } catch (Exception e) {
            log.error("Failed to send payment confirmation for payment: {}",
                event.getReferenceNumber(), e);
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onInvoiceOverdue(InvoiceOverdueEvent event) {
        log.info("Handling InvoiceOverdueEvent: invoiceId={}", event.getInvoiceId());

        try {
            Optional<PatientContact> contactOpt = patientContactLookup.findByPatientId(event.getPatientId());

            if (contactOpt.isEmpty()) {
                log.warn("Cannot send overdue notification - patient not found: {}", event.getPatientId());
                return;
            }

            PatientContact contact = contactOpt.get();
            notificationFacade.sendPaymentReminder(
                contact.userId(),
                event.getPatientId(),
                event.getInvoiceNumber(),
                event.getBalanceDue().toString(),
                event.getDaysOverdue() + " days overdue"
            );

            log.info("Sent overdue notification for invoice: {}", event.getInvoiceNumber());
        } catch (Exception e) {
            log.error("Failed to send overdue notification for invoice: {}",
                event.getInvoiceNumber(), e);
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onClaimStatusChanged(ClaimStatusChangedEvent event) {
        log.info("Handling ClaimStatusChangedEvent: claimId={}, status={}",
            event.getClaimId(), event.getNewStatus());

        try {

            Optional<ClaimInfo> claimOpt = claimLookup.findById(event.getClaimId());

            if (claimOpt.isEmpty()) {
                log.warn("Cannot send claim notification - claim not found: {}", event.getClaimId());
                return;
            }

            ClaimInfo claim = claimOpt.get();
            Optional<PatientContact> contactOpt = patientContactLookup.findByPatientId(claim.patientId());

            if (contactOpt.isEmpty()) {
                log.warn("Cannot send claim update - patient not found: {}", claim.patientId());
                return;
            }

            PatientContact contact = contactOpt.get();
            notificationFacade.sendClaimUpdate(
                contact.userId(),
                claim.patientId(),
                event.getClaimNumber(),
                event.getNewStatus().name(),
                event.getNotes()
            );

            log.info("Sent claim update notification for claim: {}", event.getClaimNumber());
        } catch (Exception e) {
            log.error("Failed to handle claim status changed event: {}",
                event.getClaimNumber(), e);
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onAppointmentRescheduled(AppointmentRescheduledEvent event) {
        log.info("Handling AppointmentRescheduledEvent: appointmentId={}", event.aggregateId());

        try {

            Optional<AppointmentInfo> appointmentOpt = appointmentLookup.findById(event.aggregateId());

            if (appointmentOpt.isEmpty()) {
                log.warn("Cannot send reschedule notification - appointment not found: {}", event.aggregateId());
                return;
            }

            AppointmentInfo appointment = appointmentOpt.get();
            Optional<PatientContact> contactOpt = patientContactLookup.findByPatientId(appointment.patientId());

            if (contactOpt.isEmpty()) {
                log.warn("Cannot send reschedule notification - patient not found: {}", appointment.patientId());
                return;
            }

            PatientContact contact = contactOpt.get();
            notificationFacade.sendAppointmentRescheduled(
                contact.userId(),
                appointment.patientId(),
                event.appointmentNumber(),
                appointment.providerName(),
                event.previousSlot().getStartDateTime().atZone(java.time.ZoneId.systemDefault()).toInstant(),
                event.newSlot().getStartDateTime().atZone(java.time.ZoneId.systemDefault()).toInstant()
            );

            log.info("Sent appointment reschedule notification for: {}", event.appointmentNumber());
        } catch (Exception e) {
            log.error("Failed to handle appointment rescheduled event: {}",
                event.appointmentNumber(), e);
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onAppointmentNoShow(AppointmentNoShowEvent event) {
        log.info("Handling AppointmentNoShowEvent: appointmentId={}, patientId={}",
            event.aggregateId(), event.patientId());

        try {
            Optional<PatientContact> contactOpt = patientContactLookup.findByPatientId(event.patientId());

            if (contactOpt.isEmpty()) {
                log.warn("Cannot send no-show notification - patient not found: {}", event.patientId());
                return;
            }

            PatientContact contact = contactOpt.get();
            notificationFacade.sendAppointmentNoShow(
                contact.userId(),
                event.patientId(),
                event.appointmentNumber()
            );

            log.info("Sent no-show notification for appointment: {}", event.appointmentNumber());
        } catch (Exception e) {
            log.error("Failed to send no-show notification for appointment: {}",
                event.appointmentNumber(), e);
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCriticalVitalsDetected(CriticalVitalsDetectedEvent event) {
        log.info("Handling CriticalVitalsDetectedEvent: recordId={}, patientId={}",
            event.aggregateId(), event.patientId());

        try {
            Optional<PatientContact> contactOpt = patientContactLookup.findByPatientId(event.patientId());

            if (contactOpt.isEmpty()) {
                log.warn("Cannot send critical vitals notification - patient not found: {}", event.patientId());
                return;
            }

            PatientContact contact = contactOpt.get();

            StringBuilder vitalsInfo = new StringBuilder();
            var vitals = event.vitalSigns();

            if (vitals.getSystolicBp() != null && (vitals.getSystolicBp() > 180 || vitals.getSystolicBp() < 70)) {
                vitalsInfo.append("Blood Pressure: ").append(vitals.getSystolicBp()).append(" mmHg. ");
            }
            if (vitals.getHeartRate() != null && (vitals.getHeartRate() > 120 || vitals.getHeartRate() < 50)) {
                vitalsInfo.append("Heart Rate: ").append(vitals.getHeartRate()).append(" bpm. ");
            }
            if (vitals.getTemperature() != null && (vitals.getTemperature().doubleValue() > 39.0 || vitals.getTemperature().doubleValue() < 35.0)) {
                vitalsInfo.append("Temperature: ").append(vitals.getTemperature()).append("°C. ");
            }

            notificationFacade.sendCriticalVitalsAlert(
                contact.userId(),
                event.patientId(),
                "VITAL_SIGNS",
                vitalsInfo.toString().trim(),
                "CRITICAL"
            );

            log.info("Sent critical vitals alert for patient: {}", event.patientId());
        } catch (Exception e) {
            log.error("Failed to send critical vitals notification for patient: {}",
                event.patientId(), e);
        }
    }
}
